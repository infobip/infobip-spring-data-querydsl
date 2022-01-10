package com.infobip.spring.data.jdbc;

import com.infobip.spring.data.jdbc.extension.CustomQuerydslJdbcRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.sql.SQLQueryFactory;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.junit.jupiter.api.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.infobip.spring.data.jdbc.QPerson.person;
import static com.infobip.spring.data.jdbc.QPersonSettings.personSettings;
import static org.assertj.core.api.BDDAssertions.then;

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
        Person johnDoe = givenSavedPerson("John", "Doe");
        Person johnyRoe = givenSavedPerson("Johny", "Roe");
        Person janeDoe = givenSavedPerson("Jane", "Doe");
        List<Person> actual = null;

        // when
        try (Stream<Person> stream = repository.streamAll()) {
            actual = stream.collect(Collectors.toList());
        }

        then(actual).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrder(johnDoe, johnyRoe, janeDoe);
    }

    @Test
    void shouldFindAll() {

        // given
        Person johnDoe = givenSavedPerson("John", "Doe");
        Person johnyRoe = givenSavedPerson("Johny", "Roe");
        Person janeDoe = givenSavedPerson("Jane", "Doe");

        // when
        List<Person> actual = repository.findAll();

        then(actual).containsExactlyInAnyOrder(johnDoe, johnyRoe, janeDoe);
    }

    @Test
    void shouldFindAllWithPredicate() {

        // given
        Person johnDoe = givenSavedPerson("John", "Doe");
        Person johnyRoe = givenSavedPerson("Johny", "Roe");
        givenSavedPerson("Jane", "Doe");

        // when
        List<Person> actual = repository.findAll(person.firstName.in("John", "Johny"));

        then(actual).containsOnly(johnDoe, johnyRoe);
    }

    @Test
    void shouldQuery() {

        // given
        Person johnDoe = givenSavedPerson("John", "Doe");
        givenSavedPerson("Johny", "Roe");
        givenSavedPerson("Jane", "Doe");
        givenSavedPerson("John", "Roe");
        givenSavedPerson("Janie", "Doe");

        // when
        List<Person> actual = repository.query(query -> query
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
        Person johnDoe = givenSavedPerson("John", "Doe");
        givenSavedPerson("Johny", "Roe");
        givenSavedPerson("Jane", "Doe");
        givenSavedPerson("John", "Roe");
        givenSavedPerson("Janie", "Doe");

        // when
        Optional<Person> actual = repository.queryOne(query -> query
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
        Person johnDoe = givenSavedPerson("John", "Doe");
        givenSavedPerson("Johny", "Roe");
        givenSavedPerson("Jane", "Doe");
        givenSavedPerson("John", "Roe");
        givenSavedPerson("Janie", "Doe");

        // when
        List<Person> actual = repository.queryMany(query -> query
                .select(repository.entityProjection())
                .from(person)
                .where(person.firstName.in("John", "Jane"))
                .orderBy(person.firstName.asc(), person.lastName.asc())
                .limit(1)
                .offset(1));

        then(actual).containsOnly(johnDoe);
    }

    @Test
    void shouldProject() {

        // given
        Person johnDoe = givenSavedPerson("John", "Doe");

        // when
        List<PersonProjection> actual = repository.query(query -> query
                .select(Projections.constructor(PersonProjection.class, person.firstName,
                                                person.lastName))
                .from(person)
                .fetch());

        // then
        then(actual).containsExactly(new PersonProjection(johnDoe.getFirstName(), johnDoe.getLastName()));
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
        then(repository.findAll()).extracting(Person::getFirstName)
                                  .containsExactlyInAnyOrder("John", "John", "Jane")
                                  .hasSize(3);
    }

    @Test
    void shouldDelete() {

        // given
        givenSavedPerson("John", "Doe");
        givenSavedPerson("Johny", "Roe");
        Person janeDoe = givenSavedPerson("Jane", "Doe");
        givenSavedPerson("John", "Roe");

        // when
        long actual = repository.deleteWhere(person.firstName.like("John%"));

        then(repository.findAll()).containsExactly(janeDoe);
        then(actual).isEqualTo(3L);
    }

    @Test
    void shouldBeAbleToJoin() {

        // given
        Person johnDoe = givenSavedPerson("John", "Doe");
        Person johnyRoe = givenSavedPerson("Johny", "Roe");
        PersonSettings johnDoeSettings = givenSavedPersonSettings(johnDoe);
        givenSavedPersonSettings(johnyRoe);

        // when
        List<Person> actual = repository.query(query -> query
                .select(repository.entityProjection())
                .from(person)
                .innerJoin(personSettings)
                .on(person.id.eq(personSettings.personId))
                .where(personSettings.id.eq(johnDoeSettings.getId()))
                .fetch());

        then(actual).extracting(Person::getFirstName).containsExactly(johnDoe.getFirstName());
    }

    @Test
    void shouldSupportMultipleConstructorsWithEntityProjection() {
        // given
        NoArgsEntity givenNoArgsEntity = giveNoArgsEntity();

        // when
        List<NoArgsEntity> actual = noArgsRepository.query(query -> query
                .select(noArgsRepository.entityProjection())
                .from(QNoArgsEntity.noArgsEntity)
                .limit(1)
                .fetch());

        then(actual).containsExactly(givenNoArgsEntity);
    }

    @Test
    void shouldSupportMultipleConstructors() {
        // given
        NoArgsEntity givenNoArgsEntity = giveNoArgsEntity();

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

    @Test
    void springDataAndQuerydslShouldHandleTimeZoneTheSameForSameTimeZone() {
        // given
        Person givenPerson = new Person(null, "givenFirstName", "givenLastName", BEGINNING_OF_2021);

        // when
        sqlQueryFactory.insert(person)
                       .columns(person.firstName, person.lastName, person.createdAt)
                       .values(givenPerson.getFirstName(), givenPerson.getLastName(), givenPerson.getCreatedAt())
                       .execute();
        repository.save(givenPerson);

        // then
        List<Person> querydslResults = sqlQueryFactory.select(repository.entityProjection()).from(person).fetch();
        List<Person> springDataResults = repository.findAll();
        then(querydslResults).isEqualTo(springDataResults);
    }

    @Transactional
    @Test
    void shouldSupportTransactionalAnnotatedTests() {
        // given
        Person johnDoe = givenSavedPerson("John", "Doe");

        // when
        List<Person> actual = repository.query(query -> query
            .select(repository.entityProjection())
            .from(person)
            .fetch());

        // then
        then(actual).containsOnly(johnDoe);
    }

    @Value
    public static class PersonProjection {

        private final String firstName;
        private final String lastName;
    }

    private Person givenSavedPerson(String firstName, String lastName) {
        return repository.save(new Person(null, firstName, lastName, BEGINNING_OF_2021));
    }

    private NoArgsEntity giveNoArgsEntity() {
        return noArgsRepository.save(new NoArgsEntity());
    }

    private PersonSettings givenSavedPersonSettings(Person person) {
        return settingsRepository.save(new PersonSettings(null, person.getId()));
    }
}
