package com.infobip.spring.data.r2dbc.sorting;

import com.infobip.spring.data.r2dbc.TestBase;
import lombok.AllArgsConstructor;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.function.Predicate;

import static com.infobip.spring.data.r2dbc.sorting.QSortingEntity.sortingEntity;

@AllArgsConstructor
public class SortingEntityPagingRepositoryTest extends TestBase {

    private final SortingEntityPagingRepository repository;

    @Test
    void shouldSortUsingQSort() {
        // given
        SortingEntity givenEntityA = new SortingEntity(null, "A");
        SortingEntity givenEntityC = new SortingEntity(null, "C");
        SortingEntity givenEntityB = new SortingEntity(null, "B");

        Flux<SortingEntity> given = repository.saveAll(Arrays.asList(givenEntityA, givenEntityC, givenEntityB));

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
        SortingEntity givenEntityA = new SortingEntity(null, "A");
        SortingEntity givenEntityC = new SortingEntity(null, "C");
        SortingEntity givenEntityB = new SortingEntity(null, "B");

        Flux<SortingEntity> given = repository.saveAll(Arrays.asList(givenEntityA, givenEntityC, givenEntityB));

        // when
        Flux<SortingEntity> actual = given.thenMany(repository.findAll(Sort.by(Order.desc("fooBar"))));

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
        SortingEntity givenEntityA = new SortingEntity(null, "1A");
        SortingEntity givenEntityC = new SortingEntity(null, "1C");
        SortingEntity givenEntityB = new SortingEntity(null, "1B");
        SortingEntity otherEntity = new SortingEntity(null, "2");

        Flux<SortingEntity> given = repository.saveAll(Arrays.asList(givenEntityA, givenEntityC, givenEntityB, otherEntity));

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
        SortingEntity givenEntityA = new SortingEntity(null, "1A");
        SortingEntity givenEntityC = new SortingEntity(null, "1C");
        SortingEntity givenEntityB = new SortingEntity(null, "1B");
        SortingEntity otherEntity = new SortingEntity(null, "2");

        Flux<SortingEntity> given = repository.saveAll(Arrays.asList(givenEntityA, givenEntityC, givenEntityB, otherEntity));

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

