package com.infobip.spring.data.jdbc.embedded;

import static com.infobip.spring.data.jdbc.embedded.QPersonWithEmbeddedNullableId.personWithEmbeddedNullableId;
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
public class PersonWithEmbeddedNullableIdRepositoryTest extends TestBase {

    private static final TimeZone oldTimeZone = TimeZone.getDefault();

    private final PersonWithEmbeddedNullableIdRepository repository;

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
        var actual = repository.save(new PersonWithEmbeddedNullableId(id, BEGINNING_OF_2021, true));

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
    void shouldFindAllWithPredicate() {

        // given
        givenSaved("John", "Doe");
        givenSaved("Jane", "Doe");

        // when
        List<PersonWithEmbeddedNullableId> actual = repository.findAll(
            personWithEmbeddedNullableId.firstName.eq("John"));

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
    void shouldUpdate() {

        // given
        givenSaved("John", "Doe");
        givenSaved("Jane", "Doe");

        // when
        Long actual = repository.update(query -> query
            .set(personWithEmbeddedNullableId.firstName, "Jonathan")
            .where(personWithEmbeddedNullableId.firstName.eq("John"))
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
        var actual = repository.deleteWhere(personWithEmbeddedNullableId.firstName.like("John%"));

        then(repository.findAll()).hasSize(1);
        then(repository.findAll().get(0).id().firstName()).isEqualTo("Jane");
        then(actual).isEqualTo(1L);
    }

    private PersonWithEmbeddedNullableId givenSaved(String firstName, String lastName) {
        return repository.save(
            new PersonWithEmbeddedNullableId(new FirstAndLastName(firstName, lastName), BEGINNING_OF_2021, true));
    }
}
