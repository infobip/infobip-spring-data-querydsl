package com.infobip.spring.data.r2dbc;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;

import static com.infobip.spring.data.r2dbc.QPerson.person;
import static com.infobip.spring.data.r2dbc.QPersonSettings.personSettings;
import static com.querydsl.core.types.Projections.constructor;
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
        Flux<Person> actual = repository.save(johnDoe, johnyRoe);

        // then
        then(block(actual)).usingElementComparatorIgnoringFields("id")
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
        Flux<Person> actual = repository.query(query -> query.select(repository.entityProjection())
                                                             .from(person)
                                                             .where(person.firstName.in("John", "Jane"))
                                                             .orderBy(person.firstName.asc(), person.lastName.asc())
                                                             .limit(1)
                                                             .offset(1))
                                        .all();

        // then
        then(block(actual)).usingFieldByFieldElementComparator()
                           .containsOnly(johnDoe);
    }

    @Test
    void shouldProject() {

        // given
        Person johnDoe = givenSavedPerson("John", "Doe");

        // when
        Flux<PersonProjection> actual = repository.query(
                query -> query.select(constructor(PersonProjection.class, person.firstName, person.lastName))
                              .from(person))
                                                  .all();

        // then
        then(block(actual)).containsExactly(new PersonProjection(johnDoe.getFirstName(), johnDoe.getLastName()));
    }

    @Test
    void shouldUpdate() {

        // given
        givenSavedPerson("John", "Doe");
        givenSavedPerson("Johny", "Roe");
        givenSavedPerson("Jane", "Doe");

        // when
        Mono<Integer> actual = repository.update(query -> query.set(person.firstName, "John")
                                                               .where(person.firstName.eq("Johny")));

        // then
        then(block(actual)).isEqualTo(1);
        then(block(repository.findAll())).extracting(Person::getFirstName)
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
        Mono<Integer> actual = repository.deleteWhere(person.firstName.like("John%"));

        // then
        then(block(actual)).isEqualTo(3L);
        then(block(repository.findAll())).containsExactly(janeDoe);
    }

    @Test
    void shouldBeAbleToJoin() {

        // given
        Person johnDoe = givenSavedPerson("John", "Doe");
        Person johnyRoe = givenSavedPerson("Johny", "Roe");
        PersonSettings johnDoeSettings = givenSavedPersonSettings(johnDoe);
        givenSavedPersonSettings(johnyRoe);

        // when
        Flux<Person> actual = repository.query(query -> query.select(repository.entityProjection())
                                                             .from(person)
                                                             .innerJoin(personSettings)
                                                             .on(person.id.eq(personSettings.personId))
                                                             .where(personSettings.id.eq(johnDoeSettings.getId())))
                                        .all();

        // then
        then(block(actual)).extracting(Person::getFirstName).containsExactly(johnDoe.getFirstName());
    }

    @Test
    void shouldSupportMultipleConstructors() {
        // given
        NoArgsEntity givenNoArgsEntity = giveNoArgsEntity("value");

        // when
        Flux<NoArgsEntity> actual = noArgsRepository.query(query -> query.select(noArgsRepository.entityProjection())
                                                                         .from(QNoArgsEntity.noArgsEntity)
                                                                         .limit(1))
                                                    .all();

        // then
        then(block(actual)).containsExactly(givenNoArgsEntity);
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

    @Nullable
    private Person givenSavedPerson(String john, String doe) {
        return block(repository.save(new Person(null, john, doe)));
    }

    @Nullable
    private NoArgsEntity giveNoArgsEntity(String value) {
        return block(noArgsRepository.save(new NoArgsEntity(value)));
    }

    @Nullable
    private PersonSettings givenSavedPersonSettings(Person person) {
        return block(settingsRepository.save(new PersonSettings(null, person.getId())));
    }
}
