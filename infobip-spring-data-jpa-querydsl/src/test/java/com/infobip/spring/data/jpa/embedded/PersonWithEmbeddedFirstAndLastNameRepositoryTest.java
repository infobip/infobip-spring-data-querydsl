package com.infobip.spring.data.jpa.embedded;

import com.infobip.spring.data.jpa.TestBase;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.*;

import java.time.ZoneOffset;
import java.util.List;
import java.util.TimeZone;

import static com.infobip.spring.data.jpa.embedded.QPersonWithEmbeddedFirstAndLastName.personWithEmbeddedFirstAndLastName;
import static org.assertj.core.api.BDDAssertions.then;

@AllArgsConstructor
public class PersonWithEmbeddedFirstAndLastNameRepositoryTest extends TestBase {

    private static final TimeZone oldTimeZone = TimeZone.getDefault();

    private final PersonWithEmbeddedFirstAndLastNameRepository repository;

    @BeforeAll
    void setupTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.UTC));
    }

    @AfterAll
    void cleanUpTimeZone() {
        TimeZone.setDefault(oldTimeZone);
    }

    @Test
    void shouldFindAll() {

        // given
        PersonWithEmbeddedFirstAndLastName johnDoe = givenSavedPerson("John", "Doe");
        PersonWithEmbeddedFirstAndLastName johnyRoe = givenSavedPerson("Johny", "Roe");
        PersonWithEmbeddedFirstAndLastName janeDoe = givenSavedPerson("Jane", "Doe");

        // when
        List<PersonWithEmbeddedFirstAndLastName> actual = repository.findAll();

        then(actual).usingRecursiveFieldByFieldElementComparator()
                    .containsExactlyInAnyOrder(johnDoe, johnyRoe, janeDoe);
    }

    @Test
    void shouldFindAllWithPredicateWithEmbeddedField() {

        // given
        PersonWithEmbeddedFirstAndLastName johnDoe = givenSavedPerson("John", "Doe");
        PersonWithEmbeddedFirstAndLastName johnyRoe = givenSavedPerson("Johny", "Roe");
        givenSavedPerson("Jane", "Doe");

        // when
        List<PersonWithEmbeddedFirstAndLastName> actual = repository.findAll(
                personWithEmbeddedFirstAndLastName.firstAndLastName.firstName.in("John", "Johny"));

        then(actual).usingRecursiveFieldByFieldElementComparator().containsOnly(johnDoe, johnyRoe);
    }

    private PersonWithEmbeddedFirstAndLastName givenSavedPerson(String firstName, String lastName) {
        return repository.save(new PersonWithEmbeddedFirstAndLastName(null, new FirstAndLastName(firstName, lastName)));
    }
}
