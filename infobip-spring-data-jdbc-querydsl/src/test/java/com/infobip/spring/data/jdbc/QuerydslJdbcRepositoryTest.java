package com.infobip.spring.data.jdbc;

import static com.infobip.spring.data.jdbc.QPerson.*;
import static com.infobip.spring.data.jdbc.QPersonSettings.*;
import static org.assertj.core.api.BDDAssertions.*;

import java.time.ZoneOffset;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.infobip.spring.data.jdbc.extension.CustomQuerydslJdbcRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.sql.SQLQueryFactory;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class QuerydslJdbcRepositoryTest extends TestBase {

    private static final TimeZone oldTimeZone = TimeZone.getDefault();

    private final PersonRepository repository;
    private final PersonSettingsRepository settingsRepository;
    private final NoArgsRepository noArgsRepository;
    private final SQLQueryFactory sqlQueryFactory;

    @BeforeAll
    void setupTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.UTC));
    }

    @AfterAll
    void cleanUpTimeZone() {
        TimeZone.setDefault(oldTimeZone);
    }

    @Transactional
    @Test
    void shouldStreamAll() {

        // given
        var johnDoe = givenSavedPerson("John", "Doe");
        var johnyRoe = givenSavedPerson("Johny", "Roe");
        var janeDoe = givenSavedPerson("Jane", "Doe");
        List<Person> actual = null;

        // when
        try (var stream = repository.streamAll()) {
            actual = stream.collect(Collectors.toList());
        }

        then(actual).usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(johnDoe, johnyRoe, janeDoe);
    }

    @Test
    void shouldFindAll() {

        // given
        var johnDoe = givenSavedPerson("John", "Doe");
        var johnyRoe = givenSavedPerson("Johny", "Roe");
        var janeDoe = givenSavedPerson("Jane", "Doe");

        // when
        var actual = repository.findAll();

        then(actual).containsExactlyInAnyOrder(johnDoe, johnyRoe, janeDoe);
    }

    @Test
    void shouldFindAllWithPredicate() {

        // given
        var johnDoe = givenSavedPerson("John", "Doe");
        var johnyRoe = givenSavedPerson("Johny", "Roe");
        givenSavedPerson("Jane", "Doe");

        // when
        List<Person> actual = repository.findAll(person.firstName.in("John", "Johny"));

        then(actual).containsOnly(johnDoe, johnyRoe);
    }

    @Test
    void shouldQuery() {

        // given
        var johnDoe = givenSavedPerson("John", "Doe");
        givenSavedPerson("Johny", "Roe");
        givenSavedPerson("Jane", "Doe");
        givenSavedPerson("John", "Roe");
        givenSavedPerson("Janie", "Doe");

        // when
        var actual = repository.query(query -> query
            .select(repository.entityProjection())
            .from(person)
            .where(person.firstName.in("John", "Jane"))
            .orderBy(person.firstName.asc(), person.lastName.asc())
            .limit(1)
            .offset(1)
            .fetch());

        then(actual).containsOnly(johnDoe);
    }

    @Test
    void shouldQueryOne() {

        // given
        var johnDoe = givenSavedPerson("John", "Doe");
        givenSavedPerson("Johny", "Roe");
        givenSavedPerson("Jane", "Doe");
        givenSavedPerson("John", "Roe");
        givenSavedPerson("Janie", "Doe");

        // when
        var actual = repository.queryOne(query -> query
            .select(repository.entityProjection())
            .from(person)
            .where(person.firstName.in("John", "Jane"))
            .orderBy(person.firstName.asc(), person.lastName.asc())
            .limit(1)
            .offset(1));

        then(actual).contains(johnDoe);
    }

    @Test
    void shouldQueryMany() {

        // given
        var johnDoe = givenSavedPerson("John", "Doe");
        givenSavedPerson("Johny", "Roe");
        givenSavedPerson("Jane", "Doe");
        givenSavedPerson("John", "Roe");
        givenSavedPerson("Janie", "Doe");

        // when
        var actual = repository.queryMany(query -> query
            .select(repository.entityProjection())
            .from(person)
            .where(person.firstName.in("John", "Jane"))
            .orderBy(person.firstName.asc(), person.lastName.asc())
            .limit(1)
            .offset(1));

        then(actual).containsOnly(johnDoe);
    }

    @Test
    void shouldQueryManyWithPageable() {
        // given
        givenSavedPerson("John", "Doe");
        givenSavedPerson("Johny", "Roe");
        var janeDoe = givenSavedPerson("Jane", "Doe");
        givenSavedPerson("John", "Roe");
        givenSavedPerson("Janie", "Doe");
        var janeStone = givenSavedPerson("Jane", "Stone");

        var page = PageRequest.of(0, 2, Sort.by(Sort.Order.asc("firstName")));

        var actual = repository.queryMany(sqlQuery -> sqlQuery.from(person)
            .where(person.firstName.in("John", "Jane")), page);

        then(actual.getSize()).isEqualTo(2);
        then(actual.getTotalElements()).isEqualTo(4);
        then(actual.getTotalPages()).isEqualTo(2);
        then(actual).containsExactlyInAnyOrder(janeDoe, janeStone);
    }

    @Test
    void shouldProject() {

        // given
        var johnDoe = givenSavedPerson("John", "Doe");

        // when
        List<PersonProjection> actual = repository.query(query -> query
            .select(Projections.constructor(PersonProjection.class, person.firstName,
                                            person.lastName))
            .from(person)
            .fetch());

        // then
        then(actual).containsExactly(new PersonProjection(johnDoe.firstName(), johnDoe.lastName()));
    }

    @Test
    void shouldUpdate() {

        // given
        givenSavedPerson("John", "Doe");
        givenSavedPerson("Johny", "Roe");
        givenSavedPerson("Jane", "Doe");

        // when
        Long actual = repository.update(query -> query
            .set(person.firstName, "John")
            .where(person.firstName.eq("Johny"))
            .execute());

        then(actual).isEqualTo(1);
        then(repository.findAll()).extracting(Person::firstName)
            .containsExactlyInAnyOrder("John", "John", "Jane")
            .hasSize(3);
    }

    @Test
    void shouldDelete() {

        // given
        givenSavedPerson("John", "Doe");
        givenSavedPerson("Johny", "Roe");
        var janeDoe = givenSavedPerson("Jane", "Doe");
        givenSavedPerson("John", "Roe");

        // when
        var actual = repository.deleteWhere(person.firstName.like("John%"));

        then(repository.findAll()).containsExactly(janeDoe);
        then(actual).isEqualTo(3L);
    }

    @Test
    void shouldBeAbleToJoin() {

        // given
        var johnDoe = givenSavedPerson("John", "Doe");
        var johnyRoe = givenSavedPerson("Johny", "Roe");
        var johnDoeSettings = givenSavedPersonSettings(johnDoe);
        givenSavedPersonSettings(johnyRoe);

        // when
        var actual = repository.query(query -> query
            .select(repository.entityProjection())
            .from(person)
            .innerJoin(personSettings)
            .on(person.id.eq(personSettings.personId))
            .where(personSettings.id.eq(johnDoeSettings.id()))
            .fetch());

        then(actual).extracting(Person::firstName).containsExactly(johnDoe.firstName());
    }

    @Test
    void shouldSupportMultipleConstructorsWithEntityProjection() {
        // given
        var givenNoArgsEntity = giveNoArgsEntity();

        // when
        var actual = noArgsRepository.query(query -> query
            .select(noArgsRepository.entityProjection())
            .from(QNoArgsEntity.noArgsEntity)
            .limit(1)
            .fetch());

        then(actual).containsExactly(givenNoArgsEntity);
    }

    @Test
    void shouldSupportMultipleConstructors() {
        // given
        var givenNoArgsEntity = giveNoArgsEntity();

        // when
        List<NoArgsEntity> actual = noArgsRepository.query(query -> query
            .select(QNoArgsEntity.noArgsEntity)
            .from(QNoArgsEntity.noArgsEntity)
            .limit(1)
            .fetch());

        then(actual).containsExactly(givenNoArgsEntity);
    }

    @Test
    void shouldExtendSimpleQuerydslJdbcRepository() {
        // then
        then(repository).isInstanceOf(QuerydslJdbcRepository.class)
            .isNotInstanceOf(CustomQuerydslJdbcRepository.class);
    }

    @Transactional
    @Test
    void springDataAndQuerydslShouldHandleTimeZoneTheSameForSameTimeZone() {
        // given
        var givenPerson = new Person(null, "givenFirstName", "givenLastName", BEGINNING_OF_2021);

        // when
        sqlQueryFactory.insert(person)
            .columns(person.firstName, person.lastName, person.createdAt)
            .values(givenPerson.firstName(), givenPerson.lastName(), givenPerson.createdAt())
            .execute();
        repository.save(givenPerson);

        // then
        var querydslResults = sqlQueryFactory.select(repository.entityProjection()).from(person).fetch();
        var springDataResults = repository.findAll();
        then(querydslResults).isEqualTo(springDataResults);
    }

    @Transactional
    @Test
    void shouldSupportTransactionalAnnotatedTests() {
        // given
        var johnDoe = givenSavedPerson("John", "Doe");

        // when
        var actual = repository.query(query -> query
            .select(repository.entityProjection())
            .from(person)
            .fetch());

        // then
        then(actual).containsOnly(johnDoe);
    }

    public record PersonProjection(
        String firstName,
        String lastName
    ) {

    }

    private Person givenSavedPerson(String firstName, String lastName) {
        return repository.save(new Person(null, firstName, lastName, BEGINNING_OF_2021));
    }

    private NoArgsEntity giveNoArgsEntity() {
        return noArgsRepository.save(new NoArgsEntity());
    }

    private PersonSettings givenSavedPersonSettings(Person person) {
        return settingsRepository.save(new PersonSettings(null, person.id()));
    }

}
