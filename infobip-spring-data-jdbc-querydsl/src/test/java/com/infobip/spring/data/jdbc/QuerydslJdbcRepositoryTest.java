package com.infobip.spring.data.jdbc;

import com.infobip.spring.data.jdbc.extension.CustomQuerydslJdbcRepository;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.sql.SQLQueryFactory;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static com.infobip.spring.data.jdbc.QPerson.person;
import static com.infobip.spring.data.jdbc.QPersonSettings.personSettings;
import static org.assertj.core.api.BDDAssertions.then;

@AllArgsConstructor
public class QuerydslJdbcRepositoryTest extends TestBase {

    private final PersonRepository repository;
    private final PersonSettingsRepository settingsRepository;
    private final NoArgsRepository noArgsRepository;
    private final JdbcTemplate jdbcTemplate;
    private final SQLQueryFactory sqlQueryFactory;

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
    void shouldSupportMultipleConstructors() {
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
    void shouldExtendSimpleQuerydslJdbcRepository() {
        // then
        then(repository).isInstanceOf(QuerydslJdbcRepository.class)
                        .isNotInstanceOf(CustomQuerydslJdbcRepository.class);
    }

    @Test
    void issue31Test() {
        Person givenPerson = new Person(null, "givenFirstName", "givenLastName", BEGINNING_OF_2021);

        sqlQueryFactory.insert(person)
                       .columns(person.firstName, person.lastName, person.createdAt)
                       .values(givenPerson.getFirstName(), givenPerson.getLastName(), givenPerson.getCreatedAt())
                       .execute();
        repository.save(givenPerson);

        List<Person> querydslResults = sqlQueryFactory.select(repository.entityProjection()).from(person).fetch();
        List<Person> springDataResults = repository.findAll();
        System.out.println(querydslResults);
        System.out.println(springDataResults);
        then(querydslResults).isEqualTo(springDataResults);
    }

    @Test
    void queryShouldIncorrectlyHandleDateTime2Conversion() {

        // given
        repository.save(new Person(null, "givenFirstName", "givenLastName", BEGINNING_OF_2021));

        // when
        List<Person> actual = repository.query(query -> query
                .select(repository.entityProjection())
                .from(person)
                .fetch());

        then(actual).map(Person::getCreatedAt).containsExactly(BEGINNING_OF_2021.plus(1, ChronoUnit.HOURS));
    }

    @Test
    void queryManyShouldCorrectlyHandleDateTime2Conversion() {

        // given
        repository.save(new Person(null, "givenFirstName", "givenLastName", BEGINNING_OF_2021));

        // when
        List<Person> actual = repository.queryMany(query -> query
                .select(repository.entityProjection())
                .from(person));

        then(actual).map(Person::getCreatedAt).containsExactly(BEGINNING_OF_2021);
    }

    @Test
    void queryOneShouldCorrectlyHandleDateTime2Conversion() {

        // given
        repository.save(new Person(null, "givenFirstName", "givenLastName", BEGINNING_OF_2021));

        // when
        Optional<Person> actual = repository.queryOne(query -> query
                .select(repository.entityProjection())
                .from(person));

        then(actual).map(Person::getCreatedAt).contains(BEGINNING_OF_2021);
    }

    @Value
    public static class PersonProjection {

        private final String firstName;
        private final String lastName;
    }

    private Person givenSavedPerson(String john, String doe) {
        return repository.save(new Person(null, john, doe, BEGINNING_OF_2021));
    }

    private NoArgsEntity giveNoArgsEntity() {
        return noArgsRepository.save(new NoArgsEntity());
    }

    private PersonSettings givenSavedPersonSettings(Person person) {
        return settingsRepository.save(new PersonSettings(null, person.getId()));
    }
}
