package com.infobip.spring.data;

import com.querydsl.core.types.Projections;
import lombok.Value;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static com.infobip.spring.data.QPerson.person;
import static com.infobip.spring.data.QPersonSettings.personSettings;
import static org.assertj.core.api.BDDAssertions.then;

public class SqlServerQueryDslJpaRepositoryTest extends TestBase {

    @Autowired
    private PersonRepository repository;

    @Autowired
    private PersonSettingsRepository personSettingsRepository;

    @Test
    public void shouldFindUserById() {

        // given
        Person johnDoe = new Person("John", "Doe");
        repository.save(johnDoe);

        // when
        Optional<Person> actual = repository.findOneById(johnDoe.getId());

        then(actual.orElse(null)).isEqualToComparingFieldByField(johnDoe);
    }

    @Test
    public void shouldFindUserByIdForMissingUser() {

        // when
        Optional<Person> actual = repository.findOneById(1L);

        then(actual).isEmpty();
    }

    @Test
    public void shouldFindOneByPredicate() {

        // given
        Person johnDoe = new Person("John", "Doe");
        repository.save(johnDoe);

        // when
        Optional<Person> actual = repository.findOneByPredicate(person.firstName.eq("John"));

        then(actual.orElse(null)).isEqualToComparingFieldByField(johnDoe);
    }

    @Test
    public void shouldFindAllWithPredicate() {

        // given
        Person johnDoe = new Person("John", "Doe");
        Person johnyRoe = new Person("Johny", "Roe");
        Person janeDoe = new Person("Jane", "Doe");
        repository.save(johnDoe, johnyRoe, janeDoe);

        // when
        List<Person> actual = repository.findAll(person.firstName.in("John", "Johny"));

        then(actual).usingFieldByFieldElementComparator().containsOnly(johnDoe, johnyRoe);
    }

    @Test
    public void shouldQuery() {

        // given
        Person johnDoe = new Person("John", "Doe");
        Person johnyRoe = new Person("Johny", "Roe");
        Person janeDoe = new Person("Jane", "Doe");
        Person johnRoe = new Person("John", "Roe");
        Person janieDoe = new Person("Janie", "Doe");
        repository.save(johnDoe, johnyRoe, janeDoe, johnRoe, janieDoe);

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
        Person johnDoe = new Person("John", "Doe");
        repository.save(johnDoe);

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
        Person johnDoe = new Person("John", "Doe");
        Person johnyRoe = new Person("Johny", "Roe");
        Person janeDoe = new Person("Jane", "Doe");
        repository.save(johnDoe, johnyRoe, janeDoe);

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
        Person johnDoe = new Person("John", "Doe");
        Person johnyRoe = new Person("Johny", "Roe");
        Person janeDoe = new Person("Jane", "Doe");
        Person johnRoe = new Person("John", "Roe");
        repository.save(johnDoe, johnyRoe, janeDoe, johnRoe);

        // when
        long numberOfAffectedRows = repository.deleteWhere(person.firstName.like("John%"));

        then(repository.findAll()).usingFieldByFieldElementComparator().containsExactly(janeDoe);
        then(numberOfAffectedRows).isEqualTo(3L);
    }

    @Test
    public void shouldJpaSqlQuery() {

        // given
        Person johnDoe = new Person("John", "Doe");
        Person johnyRoe = new Person("Johny", "Roe");
        Person janeDoe = new Person("Jane", "Doe");
        Person johnRoe = new Person("John", "Roe");
        Person janieDoe = new Person("Janie", "Doe");
        repository.save(johnDoe, johnyRoe, janeDoe, johnRoe, janieDoe);

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

    @Test
    public void shouldBeAbleToJoin() {

        // given
        Person johnDoe = new Person("John", "Doe");
        Person johnyRoe = new Person("Johny", "Roe");
        repository.save(johnDoe, johnyRoe);
        PersonSettings johnDoeSettings = new PersonSettings(johnDoe.getId());
        PersonSettings johnyRoeSettings = new PersonSettings(johnyRoe.getId());
        personSettingsRepository.save(johnDoeSettings, johnyRoeSettings);

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
        Person johnDoe = new Person("John", "Doe");
        Person johnyRoe = new Person("Johny", "Roe");
        Person janeDoe = new Person("Jane", "Doe");
        Person johnRoe = new Person("John", "Roe");
        Person janieDoe = new Person("Janie", "Doe");
        repository.save(johnDoe, johnyRoe, janeDoe, johnRoe, janieDoe);

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
        Person johnDoe = new Person("John", "Doe");
        Person johnyRoe = new Person("Johny", "Roe");
        Person janeDoe = new Person("Jane", "Doe");
        Person johnRoe = new Person("John", "Roe");
        Person janieDoe = new Person("Janie", "Doe");
        repository.save(johnDoe, johnyRoe, janeDoe, johnRoe, janieDoe);

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
}