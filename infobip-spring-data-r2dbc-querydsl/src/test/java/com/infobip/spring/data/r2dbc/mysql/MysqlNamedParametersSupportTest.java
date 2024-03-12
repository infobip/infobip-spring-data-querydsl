package com.infobip.spring.data.r2dbc.mysql;

import com.infobip.spring.data.r2dbc.*;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.*;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.function.Predicate;

import static com.infobip.spring.data.r2dbc.QPerson.person;
import static org.assertj.core.api.BDDAssertions.then;

@AllArgsConstructor
@ContextConfiguration(loader = MssqlExclusionContextLoader.class)
@ActiveProfiles("mysql")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class MysqlNamedParametersSupportTest extends TestBase {

    private final PersonRepository repository;
    private final DatabaseClient databaseClient;

    @Test
    void shouldNotFailCustomQuery() {
        // given
        var given = given(givenSavedPerson("John", "Doe"),
                          givenSavedPerson("Johny", "Roe"),
                          givenSavedPerson("Jane", "Doe"),
                          givenSavedPerson("John", "Roe"),
                          givenSavedPerson("Janie", "Doe")).block(Duration.ofSeconds(10));

        // when
        var actual = databaseClient.sql("""
                                                select * 
                                                from person 
                                                where FirstName IN (?, ?) 
                                                order by FirstName ASC, LastName 
                                                ASC LIMIT ? OFFSET ?
                                                """)
                                   .bind(0, "John")
                                   .bind(1, "Jane")
                                   .bind(2, 1)
                                   .bind(3, 1)
                                   .fetch()
                                   .all()
                                   .collectList();

        // then
        var all = actual.block(Duration.ofSeconds(10));
        then(all).isNotEmpty();
    }

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
