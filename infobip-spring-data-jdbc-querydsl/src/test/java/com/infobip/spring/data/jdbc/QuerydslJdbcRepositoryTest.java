package com.infobip.spring.data.jdbc;

import com.querydsl.core.types.Projections;
import lombok.Value;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

public class QuerydslJdbcRepositoryTest extends TestBase {

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
        List<Person> actual = repository.findAll(QPerson.Person.firstName.in("John", "Johny"));

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
                .select(repository.entityProjection())
                .from(QPerson.Person)
                .where(QPerson.Person.firstName.in("John", "Jane"))
                .orderBy(QPerson.Person.firstName.asc(), QPerson.Person.lastName.asc())
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
                .select(Projections.constructor(PersonProjection.class, QPerson.Person.firstName,
                                                QPerson.Person.lastName))
                .from(QPerson.Person)
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
                .set(QPerson.Person.firstName, "John")
                .where(QPerson.Person.firstName.eq("Johny"))
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
        long numberOfAffectedRows = repository.deleteWhere(QPerson.Person.firstName.like("John%"));

        then(repository.findAll()).usingFieldByFieldElementComparator().containsExactly(janeDoe);
        then(numberOfAffectedRows).isEqualTo(3L);
    }

    @Test
    public void shouldBeAbleToJoin() {

        // given
        Person johnDoe = givenSavedPerson("John", "Doe");
        Person johnyRoe = givenSavedPerson("Johny", "Roe");
        PersonSettings johnDoeSettings = givenSavedPersonSettings(johnDoe);
        givenSavedPersonSettings(johnyRoe);

        // when
        List<Person> actual = repository.query(query -> query
                .select(repository.entityProjection())
                .from(QPerson.Person)
                .innerJoin(QPersonSettings.PersonSettings)
                .on(QPerson.Person.id.eq(QPersonSettings.PersonSettings.personId))
                .where(QPersonSettings.PersonSettings.id.eq(johnDoeSettings.getId()))
                .fetch());

        then(actual).extracting(Person::getFirstName).containsExactly(johnDoe.getFirstName());
    }

    @Value
    public static class PersonProjection {
        private final String firstName;
        private final String lastName;
    }

    private Person givenSavedPerson(String john, String doe) {
        return repository.save(new Person(null, john, doe));
    }

    private PersonSettings givenSavedPersonSettings(Person person) {
        return settingsRepository.save(new PersonSettings(null, person.getId()));
    }
}