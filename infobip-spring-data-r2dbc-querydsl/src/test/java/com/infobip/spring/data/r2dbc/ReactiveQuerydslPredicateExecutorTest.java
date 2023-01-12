package com.infobip.spring.data.r2dbc;

import static com.infobip.spring.data.r2dbc.QPerson.person;

import java.util.function.Predicate;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.ReactiveQuerydslPredicateExecutor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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
        var given = given(givenSavedPerson("John", "Doe"),
                          givenSavedPerson("Johny", "Roe"));

        // when
        var actual = given.then(executor.findOne(person.firstName.eq("John")));

        // then
        StepVerifier.create(actual)
                    .expectNextMatches(person("John", "Doe"))
                    .verifyComplete();
    }

    @Test
    void shouldFindAllWithPredicate() {
        // given
        var given = given(givenSavedPerson("John", "Doe"),
                          givenSavedPerson("Johny", "Roe"),
                          givenSavedPerson("Jane", "Doe"));

        // when
        Flux<Person> actual = given.thenMany(executor.findAll(person.lastName.eq("Doe")));

        // then
        StepVerifier.create(actual)
                    .expectNextMatches(person("John", "Doe"))
                    .expectNextMatches(person("Jane", "Doe"))
                    .verifyComplete();
    }

    @Test
    void shouldFindAllWithPredicateAndSort() {
        // given
        var given = given(givenSavedPerson("John", "Doe"),
                          givenSavedPerson("Johny", "Roe"),
                          givenSavedPerson("Jane", "Doe"));

        // when
        Flux<Person> actual = given.thenMany(executor.findAll(person.lastName.eq("Doe"), Sort.by(Sort.Order.asc("firstName"))));

        // then
        StepVerifier.create(actual)
                    .expectNextMatches(person("Jane", "Doe"))
                    .expectNextMatches(person("John", "Doe"))
                    .verifyComplete();
    }

    @Test
    void shouldFindAllWithPredicateAndOrderSpecifier() {
        // given
        var given = given(givenSavedPerson("John", "Doe"),
                          givenSavedPerson("Johny", "Roe"),
                          givenSavedPerson("Jane", "Doe"));

        // when
        Flux<Person> actual = given.thenMany(executor.findAll(person.lastName.eq("Doe"), person.firstName.asc()));

        // then
        StepVerifier.create(actual)
                    .expectNextMatches(person("Jane", "Doe"))
                    .expectNextMatches(person("John", "Doe"))
                    .verifyComplete();
    }

    @Test
    void shouldFindAllWithOrderSpecifier() {
        // given
        var given = given(givenSavedPerson("John", "Doe"),
                          givenSavedPerson("Johny", "Roe"),
                          givenSavedPerson("Jane", "Doe"));

        // when
        Flux<Person> actual = given.thenMany(executor.findAll(person.firstName.asc()));

        // then
        StepVerifier.create(actual)
                    .expectNextMatches(person("Jane", "Doe"))
                    .expectNextMatches(person("John", "Doe"))
                    .expectNextMatches(person("Johny", "Roe"))
                    .verifyComplete();
    }

    @Test
    void shouldCount() {
        // given
        var given = given(givenSavedPerson("John", "Doe"),
                          givenSavedPerson("Johny", "Roe"),
                          givenSavedPerson("Jane", "Doe"));

        // when
        var actual = given.then(executor.count(person.lastName.eq("Doe")));

        // then
        StepVerifier.create(actual)
                    .expectNext(2L)
                    .verifyComplete();
    }

    @Test
    void shouldExist() {
        // given
        var given = given(givenSavedPerson("John", "Doe"),
                          givenSavedPerson("Johny", "Roe"),
                          givenSavedPerson("Jane", "Doe"));

        // when
        var actual = given.then(executor.exists(person.lastName.eq("Roe")));

        // then
        StepVerifier.create(actual)
                    .expectNext(true)
                    .verifyComplete();
    }

    private Mono<Person> givenSavedPerson(String firstName, String lastName) {
        return repository.save(new Person(null, firstName, lastName));
    }

    private Predicate<? super Person> person(String firstName, String lastName) {
        return person -> person.equals(new Person(person.id(), firstName, lastName));
    }
}
