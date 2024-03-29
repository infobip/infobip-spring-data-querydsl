package com.infobip.spring.data.r2dbc.mysql;

import com.infobip.spring.data.r2dbc.TestBase;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.*;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Predicate;

import static com.infobip.spring.data.r2dbc.mysql.QPerson.person;
import static org.assertj.core.api.BDDAssertions.then;

@AllArgsConstructor
@ContextConfiguration(loader = MssqlExclusionContextLoader.class)
@ActiveProfiles("mysql")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest(classes = Main.class)
public class MysqlNamedParametersSupportTest extends TestBase {

    private final PersonRepository repository;
    private final DatabaseClient databaseClient;

    @Test
    void shouldNotFailQuerydslQuery() {
        // given
        var given = given(givenSavedPerson("John", "Doe"),
                          givenSavedPerson("Johny", "Roe"),
                          givenSavedPerson("Jane", "Doe"),
                          givenSavedPerson("John", "Roe"),
                          givenSavedPerson("Janie", "Doe"));

        // when
        var actual = given.thenMany(repository.query(query -> query.select(repository.entityProjection())
                                                                   .from(person)
                                                                   .where(person.firstName.in("John", "Jane"))
                                                                   .orderBy(person.firstName.asc(),
                                                                            person.lastName.asc())
                                                                   .limit(1)
                                                                   .offset(1))
                                              .all());

        // then
        StepVerifier.create(actual)
                    .expectNextMatches(person("John", "Doe"))
                    .verifyComplete();
    }

    private Mono<Person> givenSavedPerson(String firstName, String lastName) {
        return repository.save(new Person(null, firstName, lastName));
    }

    private Predicate<? super Person> person(String firstName, String lastName) {
        return person -> {
            then(person).isEqualTo(new Person(person.id(), firstName, lastName));
            return true;
        };
    }
}
