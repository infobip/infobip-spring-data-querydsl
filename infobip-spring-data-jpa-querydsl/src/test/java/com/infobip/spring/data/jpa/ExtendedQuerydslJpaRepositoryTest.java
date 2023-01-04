package com.infobip.spring.data.jpa;

import static com.infobip.spring.data.jpa.QPerson.person;
import static com.infobip.spring.data.jpa.QPersonSettings.personSettings;
import static org.assertj.core.api.BDDAssertions.then;

import java.util.List;
import java.util.stream.Collectors;

import com.querydsl.core.types.Projections;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
public class ExtendedQuerydslJpaRepositoryTest extends TestBase {

    private final PersonRepository repository;
    private final PersonSettingsRepository settingsRepository;

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

        then(actual).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrder(johnDoe, johnyRoe, janeDoe);
    }

    @Test
    void shouldFindAllWithPredicate() {

        // given
        var johnDoe = givenSavedPerson("John", "Doe");
        var johnyRoe = givenSavedPerson("Johny", "Roe");
        givenSavedPerson("Jane", "Doe");

        // when
        List<Person> actual = repository.findAll(person.firstName.in("John", "Johny"));

        then(actual).usingRecursiveFieldByFieldElementComparator().containsOnly(johnDoe, johnyRoe);
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
        List<Person> actual = repository.query(query -> query
            .select(person)
            .from(person)
            .where(person.firstName.in("John", "Jane"))
            .orderBy(person.firstName.asc(), person.lastName.asc())
            .limit(1)
            .offset(1)
            .fetch());

        then(actual).usingRecursiveFieldByFieldElementComparator().containsOnly(johnDoe);
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
        var janeDoe = givenSavedPerson("Jane", "Doe");
        givenSavedPerson("John", "Roe");

        // when
        var numberOfAffectedRows = repository.deleteWhere(person.firstName.like("John%"));

        then(repository.findAll()).usingRecursiveFieldByFieldElementComparator().containsExactly(janeDoe);
        then(numberOfAffectedRows).isEqualTo(3L);
    }

    @Test
    void shouldJpaSqlQuery() {

        // given
        var johnDoe = givenSavedPerson("John", "Doe");
        givenSavedPerson("Johny", "Roe");
        var janeDoe = givenSavedPerson("Jane", "Doe");
        var johnRoe = givenSavedPerson("John", "Roe");
        var janieDoe = givenSavedPerson("Janie", "Doe");

        // when
        var actual = repository.jpaSqlQuery(query -> query
                                                .union(
                                                    repository.jpaSqlSubQuery(subQuery ->
                                                                                  subQuery.select(person)
                                                                                          .from(person)
                                                                                          .where(person.firstName.like("John"))),
                                                    repository.jpaSqlSubQuery(subQuery ->
                                                                                  subQuery.select(person)
                                                                                          .from(person)
                                                                                          .where(person.firstName.like("Jan%")))
                                                      )
                                                .orderBy(person.firstName.asc(), person.lastName.asc())
                                                .fetch()
                                           );

        then(actual).usingRecursiveFieldByFieldElementComparator().containsExactly(janeDoe, janieDoe, johnDoe, johnRoe);
    }

    @Test
    void shouldBeAbleToJoin() {

        // given
        var johnDoe = givenSavedPerson("John", "Doe");
        var johnyRoe = givenSavedPerson("Johny", "Roe");
        var johnDoeSettings = givenSavedPersonSettings(johnDoe);
        givenSavedPersonSettings(johnyRoe);

        // when
        List<Person> actual = repository.jpaSqlQuery(query -> query
            .select(person)
            .from(person)
            .innerJoin(personSettings)
            .on(person.id.eq(personSettings.personId))
            .where(personSettings.id.eq(johnDoeSettings.getId()))
            .fetch());

        then(actual).extracting(Person::getFirstName).containsExactly(johnDoe.getFirstName());
    }

    @Test
    void shouldExecuteStoredProcedure() {

        // given
        var johnDoe = givenSavedPerson("John", "Doe");
        var johnyRoe = givenSavedPerson("Johny", "Roe");
        var janeDoe = givenSavedPerson("Jane", "Doe");
        var johnRoe = givenSavedPerson("John", "Roe");
        var janieDoe = givenSavedPerson("Janie", "Doe");

        // when
        List<String> actual = repository.executeStoredProcedure("Person_DeleteAndGetFirstNames", builder ->
            builder.addInParameter(person.lastName, johnyRoe.getLastName()).getResultList());

        // then
        then(actual).containsExactlyInAnyOrder(johnyRoe.getFirstName(), johnRoe.getFirstName());
        then(repository.findAll()).usingRecursiveFieldByFieldElementComparator()
                                  .containsExactlyInAnyOrder(johnDoe, janeDoe, janieDoe);
    }

    @Test
    void shouldExecuteStoredProcedureWithResultClasses() {

        // given
        var johnDoe = givenSavedPerson("John", "Doe");
        var johnyRoe = givenSavedPerson("Johny", "Roe");
        var janeDoe = givenSavedPerson("Jane", "Doe");
        var johnRoe = givenSavedPerson("John", "Roe");
        var janieDoe = givenSavedPerson("Janie", "Doe");

        // when
        List<Person> actual = repository.executeStoredProcedure(
            "Person_Delete",
            builder -> builder.addInParameter(person.firstName, johnyRoe.getFirstName())
                              .addInParameter(person.lastName, johnyRoe.getLastName())
                              .setResultClasses(Person.class)
                              .getResultList());

        // then
        then(actual).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrder(johnyRoe);
        then(repository.findAll()).usingRecursiveFieldByFieldElementComparator()
                                  .containsExactlyInAnyOrder(johnDoe, janeDoe, johnRoe, janieDoe);
    }

    public record PersonProjection(
        String firstName,
        String lastName
    ) {

    }

    private Person givenSavedPerson(String firstName, String lastName) {
        return repository.save(new Person(firstName, lastName));
    }

    private PersonSettings givenSavedPersonSettings(Person person) {
        return settingsRepository.save(new PersonSettings(person.getId()));
    }

}
