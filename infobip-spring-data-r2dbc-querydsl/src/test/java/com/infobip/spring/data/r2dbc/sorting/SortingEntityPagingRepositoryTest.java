package com.infobip.spring.data.r2dbc.sorting;

import static com.infobip.spring.data.r2dbc.sorting.QSortingEntity.sortingEntity;

import java.util.Arrays;
import java.util.function.Predicate;

import com.infobip.spring.data.r2dbc.TestBase;
import lombok.AllArgsConstructor;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@AllArgsConstructor
public class SortingEntityPagingRepositoryTest extends TestBase {

    private final SortingEntityPagingRepository repository;

    @Test
    void shouldSortUsingQSort() {
        // given
        var givenEntityA = new SortingEntity(null, "A");
        var givenEntityC = new SortingEntity(null, "C");
        var givenEntityB = new SortingEntity(null, "B");

        var given = repository.saveAll(Arrays.asList(givenEntityA, givenEntityC, givenEntityB));

        // when
        Flux<SortingEntity> actual = given.thenMany(repository.findAll(sortingEntity.fooBar.desc()));

        // then
        StepVerifier.create(actual)
                .expectNextMatches(sortingEntity(givenEntityC.getFooBar()))
                .expectNextMatches(sortingEntity(givenEntityB.getFooBar()))
                .expectNextMatches(sortingEntity(givenEntityA.getFooBar()))
                .verifyComplete();
    }

    @Test
    void shouldSortUsingSort() {
        // given
        var givenEntityA = new SortingEntity(null, "A");
        var givenEntityC = new SortingEntity(null, "C");
        var givenEntityB = new SortingEntity(null, "B");

        var given = repository.saveAll(Arrays.asList(givenEntityA, givenEntityC, givenEntityB));

        // when
        var actual = given.thenMany(repository.findAll(Sort.by(Order.desc("fooBar"))));

        // then
        StepVerifier.create(actual)
                .expectNextMatches(sortingEntity(givenEntityC.getFooBar()))
                .expectNextMatches(sortingEntity(givenEntityB.getFooBar()))
                .expectNextMatches(sortingEntity(givenEntityA.getFooBar()))
                .verifyComplete();
    }

    @Test
    void shouldSortUsingQSortCombinedWithPredicate() {
        // given
        var givenEntityA = new SortingEntity(null, "1A");
        var givenEntityC = new SortingEntity(null, "1C");
        var givenEntityB = new SortingEntity(null, "1B");
        var otherEntity = new SortingEntity(null, "2");

        var given = repository.saveAll(Arrays.asList(givenEntityA, givenEntityC, givenEntityB, otherEntity));

        // when
        Flux<SortingEntity> actual = given.thenMany(repository.findAll(
                sortingEntity.fooBar.startsWith("1"),
                sortingEntity.fooBar.desc()
        ));

        // then
        StepVerifier.create(actual)
                .expectNextMatches(sortingEntity(givenEntityC.getFooBar()))
                .expectNextMatches(sortingEntity(givenEntityB.getFooBar()))
                .expectNextMatches(sortingEntity(givenEntityA.getFooBar()))
                .verifyComplete();
    }

    @Test
    void shouldSortUsingSortCombinedWithPredicate() {
        // given
        var givenEntityA = new SortingEntity(null, "1A");
        var givenEntityC = new SortingEntity(null, "1C");
        var givenEntityB = new SortingEntity(null, "1B");
        var otherEntity = new SortingEntity(null, "2");

        var given = repository.saveAll(Arrays.asList(givenEntityA, givenEntityC, givenEntityB, otherEntity));

        // when
        Flux<SortingEntity> actual = given.thenMany(repository.findAll(
                sortingEntity.fooBar.startsWith("1"),
                Sort.by(Order.desc("fooBar"))
        ));

        // then
        StepVerifier.create(actual)
                .expectNextMatches(sortingEntity(givenEntityC.getFooBar()))
                .expectNextMatches(sortingEntity(givenEntityB.getFooBar()))
                .expectNextMatches(sortingEntity(givenEntityA.getFooBar()))
                .verifyComplete();
    }

    private Predicate<SortingEntity> sortingEntity(String fooBarValue) {
        return entity -> {
            BDDAssertions.then(entity).isEqualTo(new SortingEntity(entity.getId(), fooBarValue));
            return true;
        };
    }
}

