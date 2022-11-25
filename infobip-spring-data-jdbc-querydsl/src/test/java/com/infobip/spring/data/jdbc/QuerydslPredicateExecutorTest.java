package com.infobip.spring.data.jdbc;

import static com.infobip.spring.data.jdbc.QPerson.person;
import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

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
        var johnDoe = givenSavedPerson("John", "Doe");
        var johnyRoe = givenSavedPerson("Johny", "Roe");

        // when
        var actual = executor.findOne(person.firstName.eq("John"));

        then(actual).contains(johnDoe);
    }

    @Test
    void shouldFindAllWithPredicate() {
        // given
        var johnDoe = givenSavedPerson("John", "Doe");
        var johnyRoe = givenSavedPerson("Johny", "Roe");
        var janeDoe = givenSavedPerson("Jane", "Doe");

        // when
        var actual = executor.findAll(person.lastName.eq("Doe"));

        then(actual).containsExactlyInAnyOrder(johnDoe, janeDoe);
    }

    @Test
    void shouldFindAllWithPredicateAndSort() {
        // given
        var johnDoe = givenSavedPerson("John", "Doe");
        var johnyRoe = givenSavedPerson("Johny", "Roe");
        var janeDoe = givenSavedPerson("Jane", "Doe");

        // when
        Iterable<Person> actual = executor.findAll(person.lastName.eq("Doe"), Sort.by(Sort.Order.asc("firstName")));

        then(actual).containsExactlyInAnyOrder(janeDoe, johnDoe);
    }

    @Test
    void shouldFindAllWithPredicateAndOrderSpecifier() {
        // given
        var johnDoe = givenSavedPerson("John", "Doe");
        var johnyRoe = givenSavedPerson("Johny", "Roe");
        var janeDoe = givenSavedPerson("Jane", "Doe");

        // when
        Iterable<Person> actual = executor.findAll(person.lastName.eq("Doe"), person.firstName.asc());

        then(actual).containsExactlyInAnyOrder(janeDoe, johnDoe);
    }

    @Test
    void shouldFindAllWithOrderSpecifier() {
        // given
        var johnDoe = givenSavedPerson("John", "Doe");
        var johnyRoe = givenSavedPerson("Johny", "Roe");
        var janeDoe = givenSavedPerson("Jane", "Doe");

        // when
        var actual = executor.findAll(person.firstName.asc());

        then(actual).containsExactlyInAnyOrder(janeDoe, johnDoe, johnyRoe);
    }

    @Test
    void shouldFindAllWithPredicateAndPageable() {
        // given
        var johnDoe = givenSavedPerson("John", "Doe");
        var johnyRoe = givenSavedPerson("Johny", "Roe");
        var janeDoe = givenSavedPerson("Jane", "Doe");

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
        var johnDoe = givenSavedPerson("John", "Doe");
        var johnyRoe = givenSavedPerson("Johny", "Roe");
        var janeDoe = givenSavedPerson("Jane", "Doe");

        // when
        var actual = executor.count(person.lastName.eq("Doe"));

        then(actual).isEqualTo(2);
    }

    @Test
    void shouldExist() {
        // given
        var johnDoe = givenSavedPerson("John", "Doe");
        var johnyRoe = givenSavedPerson("Johny", "Roe");
        var janeDoe = givenSavedPerson("Jane", "Doe");

        // when
        var actual = executor.exists(person.lastName.eq("Roe"));

        then(actual).isTrue();
    }

    @Test
    void findOneShouldNotThrowAnExceptionForNoResult() {
        // when
        var actual = executor.findOne(person.firstName.eq("John"));

        then(actual).isEmpty();
    }

    private Person givenSavedPerson(String firstName, String lastName) {
        return repository.save(new Person(null, firstName, lastName, BEGINNING_OF_2021));
    }
}
