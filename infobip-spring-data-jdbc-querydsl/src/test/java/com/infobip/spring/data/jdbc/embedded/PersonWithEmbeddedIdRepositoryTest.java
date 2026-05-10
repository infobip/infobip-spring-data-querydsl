package com.infobip.spring.data.jdbc.embedded;

import static com.infobip.spring.data.jdbc.embedded.QPersonWithEmbeddedId.personWithEmbeddedId;
import static org.assertj.core.api.BDDAssertions.then;

import java.time.ZoneOffset;
import java.util.List;
import java.util.TimeZone;

import com.infobip.spring.data.jdbc.TestBase;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@AllArgsConstructor
public class PersonWithEmbeddedIdRepositoryTest extends TestBase {

    private static final TimeZone oldTimeZone = TimeZone.getDefault();

    private final PersonWithEmbeddedIdRepository repository;

    @BeforeAll
    void setupTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.UTC));
    }

    @AfterAll
    void cleanUpTimeZone() {
        TimeZone.setDefault(oldTimeZone);
    }

    @Test
    void shouldSave() {

        // given
        var id = new FirstAndLastName("John", "Doe");

        // when
        var actual = repository.save(new PersonWithEmbeddedId(id, BEGINNING_OF_2021, true));

        then(actual.id()).isEqualTo(id);
        then(actual.createdAt()).isEqualTo(BEGINNING_OF_2021);
    }

    @Test
    void shouldFindById() {

        // given
        var id = new FirstAndLastName("John", "Doe");
        givenSaved("John", "Doe");

        // when
        var actual = repository.findById(id);

        then(actual).isPresent();
        then(actual.get().id()).isEqualTo(id);
        then(actual.get().createdAt()).isEqualTo(BEGINNING_OF_2021);
    }

    @Test
    void shouldFindAll() {

        // given
        givenSaved("John", "Doe");
        givenSaved("Jane", "Doe");

        // when
        var actual = repository.findAll();

        then(actual).hasSize(2);
        then(actual).extracting(PersonWithEmbeddedId::id)
                   .containsExactlyInAnyOrder(
                       new FirstAndLastName("John", "Doe"),
                       new FirstAndLastName("Jane", "Doe"));
    }

    @Test
    void shouldFindAllWithPredicate() {

        // given
        givenSaved("John", "Doe");
        givenSaved("Jane", "Doe");

        // when
        List<PersonWithEmbeddedId> actual = repository.findAll(
            personWithEmbeddedId.firstName.eq("John"));

        then(actual).hasSize(1);
        then(actual.get(0).id()).isEqualTo(new FirstAndLastName("John", "Doe"));
    }

    @Test
    void shouldFindByIdFirstName() {

        // given
        givenSaved("John", "Doe");
        givenSaved("John", "Roe");
        givenSaved("Jane", "Doe");

        // when
        var actual = repository.findByIdFirstName("John");

        then(actual).hasSize(2);
        then(actual).extracting(e -> e.id().lastName())
                   .containsExactlyInAnyOrder("Doe", "Roe");
    }

    @Test
    void shouldFindByIdLastName() {

        // given
        givenSaved("John", "Doe");
        givenSaved("John", "Roe");
        givenSaved("Jane", "Doe");

        // when
        var actual = repository.findByIdLastName("Doe");

        then(actual).hasSize(2);
        then(actual).extracting(e -> e.id().firstName())
                   .containsExactlyInAnyOrder("John", "Jane");
    }

    @Test
    void shouldFindByIdFirstNameAndIdLastName() {

        // given
        givenSaved("John", "Doe");
        givenSaved("John", "Roe");
        givenSaved("Jane", "Doe");

        // when
        var actual = repository.findByIdFirstNameAndIdLastName("John", "Doe");

        then(actual).isPresent();
        then(actual.get().id()).isEqualTo(new FirstAndLastName("John", "Doe"));
    }

    @Test
    void shouldFindByIdFirstNameAndIdLastNameNotFound() {

        // given
        givenSaved("John", "Doe");

        // when
        var actual = repository.findByIdFirstNameAndIdLastName("Jane", "Doe");

        then(actual).isEmpty();
    }

    @Test
    void shouldUpdate() {

        // given
        givenSaved("John", "Doe");
        givenSaved("Jane", "Doe");

        // when
        Long actual = repository.update(query -> query
            .set(personWithEmbeddedId.firstName, "Jonathan")
            .where(personWithEmbeddedId.firstName.eq("John"))
            .execute());

        then(actual).isEqualTo(1);
        then(repository.findAll()).extracting(e -> e.id().firstName())
                                  .containsExactlyInAnyOrder("Jonathan", "Jane");
    }

    @Test
    void shouldDelete() {

        // given
        givenSaved("John", "Doe");
        givenSaved("Jane", "Doe");

        // when
        var actual = repository.deleteWhere(personWithEmbeddedId.firstName.like("John%"));

        then(repository.findAll()).hasSize(1);
        then(repository.findAll().get(0).id().firstName()).isEqualTo("Jane");
        then(actual).isEqualTo(1L);
    }

    private PersonWithEmbeddedId givenSaved(String firstName, String lastName) {
        return repository.save(
            new PersonWithEmbeddedId(new FirstAndLastName(firstName, lastName), BEGINNING_OF_2021, true));
    }
}
