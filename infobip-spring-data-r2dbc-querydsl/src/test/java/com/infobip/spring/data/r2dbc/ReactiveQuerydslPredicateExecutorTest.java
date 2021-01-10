package com.infobip.spring.data.r2dbc;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.ReactiveQuerydslPredicateExecutor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Objects;

import static com.infobip.spring.data.r2dbc.QPerson.person;

public class ReactiveQuerydslPredicateExecutorTest extends TestBase {

    private final PersonRepository repository;
    private final ReactiveQuerydslPredicateExecutor<Person> executor;

    public ReactiveQuerydslPredicateExecutorTest(PersonRepository repository,
                                                 ReactiveQuerydslPredicateExecutor<Person> personRepository) {
        this.repository = repository;
        this.executor = personRepository;
    }

    @Test
    void shouldFindOne() {
        // given
        Person johnDoe = givenSavedPerson("John", "Doe");
        Person johnyRoe = givenSavedPerson("Johny", "Roe");

        // when
        Mono<Person> actual = executor.findOne(person.firstName.eq("John"));

        // then
        StepVerifier.create(actual)
                    .expectNext(johnDoe)
                    .verifyComplete();
    }

    @Test
    void shouldFindAllWithPredicate() {
        // given
        Person johnDoe = givenSavedPerson("John", "Doe");
        Person johnyRoe = givenSavedPerson("Johny", "Roe");
        Person janeDoe = givenSavedPerson("Jane", "Doe");

        // when
        Flux<Person> actual = executor.findAll(person.lastName.eq("Doe"));

        // then
        StepVerifier.create(actual)
                    .expectNext(johnDoe, janeDoe)
                    .verifyComplete();
    }

    @Test
    void shouldFindAllWithPredicateAndSort() {
        // given
        Person johnDoe = givenSavedPerson("John", "Doe");
        Person johnyRoe = givenSavedPerson("Johny", "Roe");
        Person janeDoe = givenSavedPerson("Jane", "Doe");

        // when
        Flux<Person> actual = executor.findAll(person.lastName.eq("Doe"), Sort.by(Sort.Order.asc("firstName")));

        // then
        StepVerifier.create(actual)
                    .expectNext(janeDoe, johnDoe)
                    .verifyComplete();
    }

    @Test
    void shouldFindAllWithPredicateAndOrderSpecifier() {
        // given
        Person johnDoe = givenSavedPerson("John", "Doe");
        Person johnyRoe = givenSavedPerson("Johny", "Roe");
        Person janeDoe = givenSavedPerson("Jane", "Doe");

        // when
        Flux<Person> actual = executor.findAll(person.lastName.eq("Doe"), person.firstName.asc());

        // then
        StepVerifier.create(actual)
                    .expectNext(janeDoe, johnDoe)
                    .verifyComplete();
    }

    @Test
    void shouldFindAllWithOrderSpecifier() {
        // given
        Person johnDoe = givenSavedPerson("John", "Doe");
        Person johnyRoe = givenSavedPerson("Johny", "Roe");
        Person janeDoe = givenSavedPerson("Jane", "Doe");

        // when
        Flux<Person> actual = executor.findAll(person.firstName.asc());

        // then
        StepVerifier.create(actual)
                    .expectNext(janeDoe, johnDoe, johnyRoe)
                    .verifyComplete();
    }

    @Test
    void shouldCount() {
        // given
        Person johnDoe = givenSavedPerson("John", "Doe");
        Person johnyRoe = givenSavedPerson("Johny", "Roe");
        Person janeDoe = givenSavedPerson("Jane", "Doe");

        // when
        Mono<Long> actual = executor.count(person.lastName.eq("Doe"));

        // then
        StepVerifier.create(actual)
                    .expectNext(2L)
                    .verifyComplete();
    }

    @Test
    void shouldExist() {
        // given
        Person johnDoe = givenSavedPerson("John", "Doe");
        Person johnyRoe = givenSavedPerson("Johny", "Roe");
        Person janeDoe = givenSavedPerson("Jane", "Doe");

        // when
        Mono<Boolean> actual = executor.exists(person.lastName.eq("Roe"));

        // then
        StepVerifier.create(actual)
                    .expectNext(true)
                    .verifyComplete();
    }

    private Person givenSavedPerson(String john, String doe) {
        return Objects.requireNonNull(repository.save(new Person(null, john, doe)).block(Duration.ofSeconds(10)));
    }
}
