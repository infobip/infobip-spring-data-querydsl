package com.infobip.spring.data.r2dbc;

import com.querydsl.core.types.Projections;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

import static com.infobip.spring.data.r2dbc.QPerson.person;
import static com.infobip.spring.data.r2dbc.QPersonSettings.personSettings;
import static org.assertj.core.api.BDDAssertions.then;

@AllArgsConstructor
public class QuerydslR2dbcRepositoryTest extends TestBase {

    private final PersonRepository repository;
    private final PersonSettingsRepository settingsRepository;
    private final NoArgsRepository noArgsRepository;

    @Test
    void shouldSaveWithVarArgs() {

        // given
        Person johnDoe = new Person(null, "John", "Doe");
        Person johnyRoe = new Person(null, "Johny", "Roe");

        // when
        List<Person> actual = repository.save(johnDoe, johnyRoe).collectList().block(Duration.ofSeconds(10));

        // then
        then(actual).usingElementComparatorIgnoringFields("id")
                    .containsExactly(johnDoe, johnyRoe);
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
        Flux<Person> actual = repository.query(builder -> builder.query(
                query -> query.select(repository.entityProjection())
                              .from(person)
                              .where(person.firstName.in("John",
                                                         "Jane"))
                              .orderBy(person.firstName.asc(),
                                       person.lastName.asc())
                              .limit(1)
                              .offset(1)).all());

        // then
        StepVerifier.create(actual)
                    .expectNext(johnDoe)
                    .verifyComplete();
    }

    @Test
    void shouldProject() {

        // given
        Person johnDoe = givenSavedPerson("John", "Doe");

        // when
        Flux<PersonProjection> actual = repository.query(builder -> builder.query(
                query -> query.select(Projections.constructor(PersonProjection.class, person.firstName,
                                                              person.lastName))
                              .from(person)).all());

        // then
        StepVerifier.create(actual)
                    .expectNext(new PersonProjection(johnDoe.getFirstName(), johnDoe.getLastName()))
                    .verifyComplete();
    }

    @Test
    void shouldUpdate() {

        // given
        givenSavedPerson("John", "Doe");
        givenSavedPerson("Johny", "Roe");
        givenSavedPerson("Jane", "Doe");

        // when
        Mono<Integer> numberOfAffectedRows = repository.update(query -> query
                .set(person.firstName, "John")
                .where(person.firstName.eq("Johny")));

        // then
        StepVerifier.create(numberOfAffectedRows)
                    .expectNext(1)
                    .verifyComplete();
        StepVerifier.create(repository.findAll().map(Person::getFirstName))
                    .expectNext("John", "John", "Jane")
                    .verifyComplete();
    }

    @Test
    void shouldDelete() {

        // given
        givenSavedPerson("John", "Doe");
        givenSavedPerson("Johny", "Roe");
        Person janeDoe = givenSavedPerson("Jane", "Doe");
        givenSavedPerson("John", "Roe");

        // when
        Mono<Integer> numberOfAffectedRows = repository.deleteWhere(person.firstName.like("John%"));

        // then
        StepVerifier.create(numberOfAffectedRows)
                    .expectNext(3)
                    .verifyComplete();
        StepVerifier.create(repository.findAll())
                    .expectNext(janeDoe)
                    .verifyComplete();
    }

    @Test
    void shouldBeAbleToJoin() {

        // given
        Person johnDoe = givenSavedPerson("John", "Doe");
        Person johnyRoe = givenSavedPerson("Johny", "Roe");
        PersonSettings johnDoeSettings = givenSavedPersonSettings(johnDoe);
        givenSavedPersonSettings(johnyRoe);

        // when
        Flux<Person> actual = repository.query(
                builder -> builder.query(
                        query -> query.select(repository.entityProjection())
                                      .from(person)
                                      .innerJoin(personSettings)
                                      .on(person.id.eq(personSettings.personId))
                                      .where(personSettings.id.eq(johnDoeSettings.getId()))).all());

        // then
        StepVerifier.create(actual)
                    .expectNext(johnDoe)
                    .verifyComplete();
    }

    @Test
    void shouldSupportMultipleConstructors() {
        // given
        NoArgsEntity givenNoArgsEntity = giveNoArgsEntity("value");

        // when
        Flux<NoArgsEntity> actual = noArgsRepository.query(builder -> builder.query(
                query -> query.select(noArgsRepository.entityProjection())
                              .from(QNoArgsEntity.noArgsEntity)
                              .limit(1)).all());

        // then
        StepVerifier.create(actual)
                    .expectNext(givenNoArgsEntity)
                    .verifyComplete();
    }

    @Test
    void shouldExtendSimpleQuerydslJdbcRepository() {
        // then
        then(repository).isInstanceOf(QuerydslR2dbcRepository.class);
    }

    @Value
    public static class PersonProjection {

        private final String firstName;
        private final String lastName;
    }

    private Person givenSavedPerson(String john, String doe) {
        return repository.save(new Person(null, john, doe)).block(Duration.ofSeconds(10));
    }

    private NoArgsEntity giveNoArgsEntity(String value) {
        return noArgsRepository.save(new NoArgsEntity(value)).block(Duration.ofSeconds(10));
    }

    private PersonSettings givenSavedPersonSettings(Person person) {
        return settingsRepository.save(new PersonSettings(null, person.getId())).block(Duration.ofSeconds(10));
    }
}
