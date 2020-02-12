package com.infobip.spring.data.jdbc;

import com.querydsl.core.types.Projections;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.infobip.spring.data.jdbc.QPerson.person;
import static com.infobip.spring.data.jdbc.QPersonSettings.personSettings;
import static org.assertj.core.api.BDDAssertions.then;

@AllArgsConstructor
public class QuerydslJdbcRepositoryTest extends TestBase {

    private final PersonRepository repository;
    private final PersonSettingsRepository settingsRepository;

    @Test
    void shouldFindOneWithPredicate() {

        // given
        Person johnDoe = givenSavedPerson("John", "Doe");
        Person johnyRoe = givenSavedPerson("Johny", "Roe");
        givenSavedPerson("Jane", "Doe");

        // when
        Optional<Person> actual = repository.findOne(person.firstName.eq("John"));

        then(actual).usingFieldByFieldValueComparator().contains(johnDoe);
    }

    @Test
    void shouldFindAll() {

        // given
        Person johnDoe = givenSavedPerson("John", "Doe");
        Person johnyRoe = givenSavedPerson("Johny", "Roe");
        Person janeDoe = givenSavedPerson("Jane", "Doe");

        // when
        List<Person> actual = repository.findAll();

        then(actual).usingFieldByFieldElementComparator().containsExactlyInAnyOrder(johnDoe, johnyRoe, janeDoe);
    }

    @Test
    void shouldFindAllWithPredicate() {

        // given
        Person johnDoe = givenSavedPerson("John", "Doe");
        Person johnyRoe = givenSavedPerson("Johny", "Roe");
        givenSavedPerson("Jane", "Doe");

        // when
        List<Person> actual = repository.findAll(person.firstName.in("John", "Johny"));

        then(actual).usingFieldByFieldElementComparator().containsOnly(johnDoe, johnyRoe);
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

        then(actual).usingFieldByFieldElementComparator().containsOnly(johnDoe);
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
        repository.update(query -> query
                .set(person.firstName, "John")
                .where(person.firstName.eq("Johny"))
                .execute());

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
        long numberOfAffectedRows = repository.deleteWhere(person.firstName.like("John%"));

        then(repository.findAll()).usingFieldByFieldElementComparator().containsExactly(janeDoe);
        then(numberOfAffectedRows).isEqualTo(3L);
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