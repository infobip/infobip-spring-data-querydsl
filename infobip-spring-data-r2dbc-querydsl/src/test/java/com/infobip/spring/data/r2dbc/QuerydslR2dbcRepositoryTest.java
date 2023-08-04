package com.infobip.spring.data.r2dbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AllArgsConstructor;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.function.Predicate;

import static com.infobip.spring.data.r2dbc.QPerson.person;
import static com.infobip.spring.data.r2dbc.QPersonSettings.personSettings;
import static com.querydsl.core.types.Projections.constructor;
import static org.assertj.core.api.BDDAssertions.then;

@AllArgsConstructor
public class QuerydslR2dbcRepositoryTest extends TestBase {

    private final PersonRepository repository;
    private final PersonSettingsRepository settingsRepository;
    private final NoArgsRepository noArgsRepository;
    private final Environment env;

    @Test
    void shouldSaveWithVarArgs() {

        // given
        var johnDoe = new Person(null, "John", "Doe");
        var johnyRoe = new Person(null, "Johny", "Roe");

        // when
        var actual = repository.save(johnDoe, johnyRoe);

        // then
        StepVerifier.create(actual)
                    .expectNextMatches(person("John", "Doe"))
                    .expectNextMatches(person("Johny", "Roe"))
                    .verifyComplete();
    }

    @Test
    void shouldQuery() {

        // given
        var given = given(givenSavedPerson("John", "Doe"),
                          givenSavedPerson("Johny", "Roe"),
                          givenSavedPerson("Jane", "Doe"),
                          givenSavedPerson("John", "Roe"),
                          givenSavedPerson("Janie", "Doe"));

        // when
        var actual = given.thenMany(repository.query(query -> query.select(repository.entityProjection())
                                                                   .from(person)
                                                                   .where(person.firstName.in("John", "Jane"))
                                                                   .orderBy(person.firstName.asc(),
                                                                            person.lastName.asc())
                                                                   .limit(1)
                                                                   .offset(1))
                                              .all());

        // then
        StepVerifier.create(actual)
                    .expectNextMatches(person("John", "Doe"))
                    .verifyComplete();
    }

    @Test
    void shouldQueryWithQueryAnnotation() {

        // given
        var given = given(givenSavedPerson("John", "Doe"));

        // when
        var actual = given.thenMany(repository.firstNameIn(List.of("John")));

        // then
        StepVerifier.create(actual)
                    .expectNextMatches(person("John", "Doe"))
                    .verifyComplete();
    }

    @Test
    void shouldProject() {

        // given
        var given = given(givenSavedPerson("John", "Doe"));

        // when
        Flux<PersonProjection> actual = given.thenMany(repository.query(
                                                                         query -> query.select(constructor(PersonProjection.class, person.firstName, person.lastName))
                                                                                       .from(person))
                                                                 .all());

        // then
        StepVerifier.create(actual)
                    .expectNextMatches(projection -> projection.equals(new PersonProjection("John", "Doe")))
                    .verifyComplete();
    }

    @Test
    void shouldUpdate() {

        // given
        var given = given(givenSavedPerson("John", "Doe"),
                          givenSavedPerson("Johny", "Roe"),
                          givenSavedPerson("Jane", "Doe"));

        // when
        var actual = given.then(repository.update(query -> query.set(person.firstName, "John")
                                                                .where(person.firstName.eq("Johny"))));

        // then
        System.out.println(actual.block());
        StepVerifier.create(actual)
                    .expectNext(1L)
                    .verifyComplete();
    }

    @Test
    void shouldDelete() {

        // given
        var given = given(givenSavedPerson("John", "Doe"),
                          givenSavedPerson("Johny", "Roe"),
                          givenSavedPerson("Jane", "Doe"),
                          givenSavedPerson("John", "Roe"));

        // when
        var actual = given.then(repository.deleteWhere(person.firstName.like("John%")));

        // then
        StepVerifier.create(actual)
                    .expectNext(3L)
                    .verifyComplete();
    }

    @Test
    void shouldBeAbleToJoin() {

        // given
        var givenJohnDoeSettings = given(givenSavedPersonAndSettings("Johny", "Roe"),
                                         givenSavedPerson("Jane", "Doe"),
                                         givenSavedPerson("John", "Roe"))
                .then(givenSavedPersonAndSettings("John", "Doe"));

        // when
        Flux<Person> actual = givenJohnDoeSettings.flatMapMany(
                johnDoeSettings -> repository.query(query -> query.select(repository.entityProjection())
                                                                  .from(person)
                                                                  .innerJoin(personSettings)
                                                                  .on(person.id.eq(personSettings.personId))
                                                                  .where(personSettings.id.eq(
                                                                          johnDoeSettings.id())))
                                             .all());

        // then
        StepVerifier.create(actual)
                    .expectNextMatches(person("John", "Doe"))
                    .verifyComplete();
    }

    @Test
    void shouldSupportMultipleConstructors() {
        // given
        var given = given(giveNoArgsEntity("value"));

        // when
        var actual = given.thenMany(
                noArgsRepository.query(query -> query.select(noArgsRepository.entityProjection())
                                                     .from(QNoArgsEntity.noArgsEntity)
                                                     .limit(1))
                                .all());

        // then
        StepVerifier.create(actual)
                    .expectNextMatches(
                            noArgsEntity("value"))
                    .verifyComplete();
    }

    @Test
    void shouldExtendSimpleQuerydslJdbcRepository() {
        // then
        then(repository).isInstanceOf(QuerydslR2dbcRepository.class);
    }

    @Test
    void shouldNotExecuteSqlInjectionWithQuery() {

        // given
        var givenBobbyLittleTablesSurname = "';DROP TABLE PersonSettings;DROP TABLE Person;--";
        var givenBobbyLittleTables = given(givenSavedPerson("Robert", givenBobbyLittleTablesSurname)).then().block();

        // when
        repository.query(query -> query.select(repository.entityProjection())
                                       .from(person)
                                       .where(person.lastName.eq(givenBobbyLittleTablesSurname)));

        // then
        StepVerifier.create(repository.findAll())
                    .expectNextMatches(person("Robert", givenBobbyLittleTablesSurname))
                    .verifyComplete();
        var config = new HikariConfig();
        config.setJdbcUrl(env.getRequiredProperty("spring.flyway.url"));
        config.setUsername(env.getRequiredProperty("spring.flyway.username"));
        config.setPassword(env.getRequiredProperty("spring.flyway.password"));
        var tableExists = new JdbcTemplate(new HikariDataSource(config)).queryForObject("""
                                                                   select count(*)
                                                                   from information_schema.tables
                                                                   where table_name = 'Person'
                                                                   """, Integer.class);
        BDDAssertions.then(tableExists).isEqualTo(1);
    }

    public record PersonProjection(
            String firstName,
            String lastName
    ) {

    }

    private Mono<Person> givenSavedPerson(String firstName, String lastName) {
        return repository.save(new Person(null, firstName, lastName));
    }

    private Mono<NoArgsEntity> giveNoArgsEntity(String value) {
        return noArgsRepository.save(new NoArgsEntity(value));
    }

    private Mono<PersonSettings> givenSavedPersonAndSettings(String firstName, String lastName) {
        return repository.save(new Person(null, firstName, lastName)).flatMap(
                person -> settingsRepository.save(new PersonSettings(null, person.id())));
    }

    private Predicate<? super Person> person(String firstName, String lastName) {
        return person -> {
            BDDAssertions.then(person).isEqualTo(new Person(person.id(), firstName, lastName));
            return true;
        };
    }

    private Predicate<NoArgsEntity> noArgsEntity(String value) {
        return noArgsEntity -> {
            BDDAssertions.then(noArgsEntity).isEqualTo(new NoArgsEntity(noArgsEntity.getId(), value));
            return true;
        };
    }
}
