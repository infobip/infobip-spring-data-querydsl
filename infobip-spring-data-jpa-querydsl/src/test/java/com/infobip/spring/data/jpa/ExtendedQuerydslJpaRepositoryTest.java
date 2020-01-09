package com.infobip.spring.data.jpa;

import com.querydsl.core.types.Projections;
import lombok.Value;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.infobip.spring.data.jpa.QPerson.person;
import static com.infobip.spring.data.jpa.QPersonSettings.personSettings;
import static org.assertj.core.api.BDDAssertions.then;

public class ExtendedQuerydslJpaRepositoryTest extends TestBase {

    @Autowired
    private PersonRepository repository;

    @Autowired
    private PersonSettingsRepository settingsRepository;

    @Test
    public void shouldFindAllWithPredicate() {

        // given
        Person johnDoe = givenSavedPerson("John", "Doe");
        Person johnyRoe = givenSavedPerson("Johny", "Roe");
        givenSavedPerson("Jane", "Doe");

        // when
        List<Person> actual = repository.findAll(person.firstName.in("John", "Johny"));

        then(actual).usingFieldByFieldElementComparator().containsOnly(johnDoe, johnyRoe);
    }

    @Test
    public void shouldQuery() {

        // given
        Person johnDoe = givenSavedPerson("John", "Doe");
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

        then(actual).usingFieldByFieldElementComparator().containsOnly(johnDoe);
    }

    @Test
    public void shouldProject() {

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
    public void shouldUpdate() {

        // given
        givenSavedPerson("John", "Doe");
        givenSavedPerson("Johny", "Roe");
        givenSavedPerson("Jane", "Doe");

        // when
        repository.update(query -> query
                .set(person.firstName, "John")
                .where(person.firstName.eq("Johny"))
                .execute());

        then(repository.findAll()).extracting(Person::getFirstName)
                                  .containsExactlyInAnyOrder("John", "John", "Jane")
                                  .hasSize(3);
    }

    @Test
    public void shouldDelete() {

        // given
        givenSavedPerson("John", "Doe");
        givenSavedPerson("Johny", "Roe");
        Person janeDoe = givenSavedPerson("Jane", "Doe");
        givenSavedPerson("John", "Roe");

        // when
        long numberOfAffectedRows = repository.deleteWhere(person.firstName.like("John%"));

        then(repository.findAll()).usingFieldByFieldElementComparator().containsExactly(janeDoe);
        then(numberOfAffectedRows).isEqualTo(3L);
    }

    // https://github.com/querydsl/querydsl/issues/1917
    @Ignore
    @Test
    public void shouldJpaSqlQuery() {

        // given
        Person johnDoe = givenSavedPerson("John", "Doe");
        givenSavedPerson("Johny", "Roe");
        Person janeDoe = givenSavedPerson("Jane", "Doe");
        Person johnRoe = givenSavedPerson("John", "Roe");
        Person janieDoe = givenSavedPerson("Janie", "Doe");

        // when
        List<Person> actual = repository.jpaSqlQuery(query -> query
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

        then(actual).usingFieldByFieldElementComparator().containsExactly(janeDoe, janieDoe, johnDoe, johnRoe);
    }

    // https://github.com/querydsl/querydsl/issues/1917
    @Ignore
    @Test
    public void shouldBeAbleToJoin() {

        // given
        Person johnDoe = givenSavedPerson("John", "Doe");
        Person johnyRoe = givenSavedPerson("Johny", "Roe");
        PersonSettings johnDoeSettings = givenSavedPersonSettings(johnDoe);
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
    public void shouldExecuteStoredProcedure() {

        // given
        Person johnDoe = givenSavedPerson("John", "Doe");
        Person johnyRoe = givenSavedPerson("Johny", "Roe");
        Person janeDoe = givenSavedPerson("Jane", "Doe");
        Person johnRoe = givenSavedPerson("John", "Roe");
        Person janieDoe = givenSavedPerson("Janie", "Doe");

        // when
        List<String> actual = repository.executeStoredProcedure("Person_DeleteAndGetFirstNames", builder ->
                builder.addInParameter(person.lastName, johnyRoe.getLastName()).getResultList());

        // then
        then(actual).containsExactlyInAnyOrder(johnyRoe.getFirstName(), johnRoe.getFirstName());
        then(repository.findAll()).usingFieldByFieldElementComparator()
                                  .containsExactlyInAnyOrder(johnDoe, janeDoe, janieDoe);
    }

    @Test
    public void shouldExecuteStoredProcedureWithResultClasses() {

        // given
        Person johnDoe = givenSavedPerson("John", "Doe");
        Person johnyRoe = givenSavedPerson("Johny", "Roe");
        Person janeDoe = givenSavedPerson("Jane", "Doe");
        Person johnRoe = givenSavedPerson("John", "Roe");
        Person janieDoe = givenSavedPerson("Janie", "Doe");

        // when
        List<Person> actual = repository.executeStoredProcedure(
                "Person_Delete",
                builder -> builder.addInParameter(person.firstName, johnyRoe.getFirstName())
                                  .addInParameter(person.lastName, johnyRoe.getLastName())
                                  .setResultClasses(Person.class)
                                  .getResultList());

        // then
        then(actual).usingFieldByFieldElementComparator().containsExactlyInAnyOrder(johnyRoe);
        then(repository.findAll()).usingFieldByFieldElementComparator()
                                  .containsExactlyInAnyOrder(johnDoe, janeDoe, johnRoe, janieDoe);
    }

    @Value
    public static class PersonProjection {
        private final String firstName;
        private final String lastName;
    }

    private Person givenSavedPerson(String john, String doe) {
        return repository.save(new Person(john, doe));
    }

    private PersonSettings givenSavedPersonSettings(Person person) {
        return settingsRepository.save(new PersonSettings(person.getId()));
    }
}