package com.infobip.spring.data.r2dbc;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.ReactiveQuerydslPredicateExecutor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;

import static com.infobip.spring.data.r2dbc.QPerson.person;
import static org.assertj.core.api.BDDAssertions.then;

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
        then(block(actual)).isEqualTo(johnDoe);
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
        then(block(actual)).containsExactlyInAnyOrder(johnDoe, janeDoe);
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
        then(block(actual)).usingFieldByFieldElementComparator().containsExactlyInAnyOrder(janeDoe, johnDoe);
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
        then(block(actual)).usingFieldByFieldElementComparator().containsExactlyInAnyOrder(janeDoe, johnDoe);
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
        then(block(actual)).usingFieldByFieldElementComparator().containsExactlyInAnyOrder(janeDoe, johnDoe, johnyRoe);
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
        then(block(actual)).isEqualTo(2);
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
        then(block(actual)).isTrue();
    }

    @Nullable
    private Person givenSavedPerson(String john, String doe) {
        return block(repository.save(new Person(null, john, doe)));
    }
}
