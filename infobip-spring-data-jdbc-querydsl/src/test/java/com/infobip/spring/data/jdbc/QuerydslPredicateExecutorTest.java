package com.infobip.spring.data.jdbc;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

import static com.infobip.spring.data.jdbc.QPerson.person;
import static org.assertj.core.api.BDDAssertions.then;

public class QuerydslPredicateExecutorTest extends TestBase {

    private final PersonRepository repository;
    private final QuerydslPredicateExecutor<Person> executor;

    public QuerydslPredicateExecutorTest(PersonRepository repository,
                                         QuerydslPredicateExecutor<Person> personRepository) {
        this.repository = repository;
        this.executor = personRepository;
    }

    @Test
    void shouldFindOne() {
        // given
        Person johnDoe = givenSavedPerson("John", "Doe");
        Person johnyRoe = givenSavedPerson("Johny", "Roe");

        // when
        Optional<Person> actual = executor.findOne(person.firstName.eq("John"));

        then(actual).contains(johnDoe);
    }

    @Test
    void shouldFindAllWithPredicate() {
        // given
        Person johnDoe = givenSavedPerson("John", "Doe");
        Person johnyRoe = givenSavedPerson("Johny", "Roe");
        Person janeDoe = givenSavedPerson("Jane", "Doe");

        // when
        Iterable<Person> actual = executor.findAll(person.lastName.eq("Doe"));

        then(actual).containsExactlyInAnyOrder(johnDoe, janeDoe);
    }

    @Test
    void shouldFindAllWithPredicateAndSort() {
        // given
        Person johnDoe = givenSavedPerson("John", "Doe");
        Person johnyRoe = givenSavedPerson("Johny", "Roe");
        Person janeDoe = givenSavedPerson("Jane", "Doe");

        // when
        Iterable<Person> actual = executor.findAll(person.lastName.eq("Doe"), Sort.by(Sort.Order.asc("firstName")));

        then(actual).containsExactlyInAnyOrder(janeDoe, johnDoe);
    }

    @Test
    void shouldFindAllWithPredicateAndOrderSpecifier() {
        // given
        Person johnDoe = givenSavedPerson("John", "Doe");
        Person johnyRoe = givenSavedPerson("Johny", "Roe");
        Person janeDoe = givenSavedPerson("Jane", "Doe");

        // when
        Iterable<Person> actual = executor.findAll(person.lastName.eq("Doe"), person.firstName.asc());

        then(actual).containsExactlyInAnyOrder(janeDoe, johnDoe);
    }

    @Test
    void shouldFindAllWithOrderSpecifier() {
        // given
        Person johnDoe = givenSavedPerson("John", "Doe");
        Person johnyRoe = givenSavedPerson("Johny", "Roe");
        Person janeDoe = givenSavedPerson("Jane", "Doe");

        // when
        Iterable<Person> actual = executor.findAll(person.firstName.asc());

        then(actual).containsExactlyInAnyOrder(janeDoe, johnDoe, johnyRoe);
    }

    @Test
    void shouldFindAllWithPredicateAndPageable() {
        // given
        Person johnDoe = givenSavedPerson("John", "Doe");
        Person johnyRoe = givenSavedPerson("Johny", "Roe");
        Person janeDoe = givenSavedPerson("Jane", "Doe");

        // when
        Page<Person> actual = executor.findAll(person.lastName.eq("Doe"),
                                               PageRequest.of(0, 1, Sort.by(Sort.Order.asc("firstName"))));

        then(actual.getSize()).isOne();
        then(actual.getTotalElements()).isEqualTo(2);
        then(actual.getTotalPages()).isEqualTo(2);
        then(actual).containsExactlyInAnyOrder(janeDoe);
    }

    @Test
    void shouldCount() {
        // given
        Person johnDoe = givenSavedPerson("John", "Doe");
        Person johnyRoe = givenSavedPerson("Johny", "Roe");
        Person janeDoe = givenSavedPerson("Jane", "Doe");

        // when
        long actual = executor.count(person.lastName.eq("Doe"));

        then(actual).isEqualTo(2);
    }

    @Test
    void shouldExist() {
        // given
        Person johnDoe = givenSavedPerson("John", "Doe");
        Person johnyRoe = givenSavedPerson("Johny", "Roe");
        Person janeDoe = givenSavedPerson("Jane", "Doe");

        // when
        boolean actual = executor.exists(person.lastName.eq("Roe"));

        then(actual).isTrue();
    }

    private Person givenSavedPerson(String john, String doe) {
        return repository.save(new Person(null, john, doe, BEGINNING_OF_2021));
    }
}
