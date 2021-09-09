package com.infobip.spring.data.jdbc.sorting;

import com.infobip.spring.data.jdbc.TestBase;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.querydsl.QPageRequest;

import static com.infobip.spring.data.jdbc.sorting.QSortingEntity.sortingEntity;
import static org.assertj.core.api.BDDAssertions.then;

@AllArgsConstructor
public class SortingEntityPagingRepositoryTest extends TestBase {

    private final SortingEntityPagingRepository repository;

    @Test
    void shouldFindByPage() {
        // given
        SortingEntity givenEntity = repository.save(new SortingEntity(null, "givenValue"));

        // when
        Page<SortingEntity> actual = repository.findAll(PageRequest.of(0, 1));

        then(actual.getContent()).containsExactly(givenEntity);
    }

    @Test
    void shouldSortUsingQSort() {
        // given
        SortingEntity givenEntityA = repository.save(new SortingEntity(null, "A"));
        SortingEntity givenEntityC = repository.save(new SortingEntity(null, "C"));
        SortingEntity givenEntityB = repository.save(new SortingEntity(null, "B"));

        // when
        Page<SortingEntity> actual = repository.findAll(QPageRequest.of(0, 5, sortingEntity.fooBar.desc()));

        then(actual.getContent()).containsExactly(givenEntityC, givenEntityB, givenEntityA);
    }

    @Test
    void shouldSortUsingSort() {
        // given
        SortingEntity givenEntityA = repository.save(new SortingEntity(null, "A"));
        SortingEntity givenEntityC = repository.save(new SortingEntity(null, "C"));
        SortingEntity givenEntityB = repository.save(new SortingEntity(null, "B"));

        // when
        Page<SortingEntity> actual = repository.findAll(PageRequest.of(0, 5, Sort.by(Order.desc("fooBar"))));

        then(actual.getContent()).containsExactly(givenEntityC, givenEntityB, givenEntityA);
    }

    @Test
    void shouldSortUsingQSortCombinedWithPredicate() {
        // given
        SortingEntity givenEntityA = repository.save(new SortingEntity(null, "1A"));
        SortingEntity givenEntityC = repository.save(new SortingEntity(null, "1C"));
        SortingEntity givenEntityB = repository.save(new SortingEntity(null, "1B"));
        SortingEntity otherEntity = repository.save(new SortingEntity(null, "2"));

        // when
        Page<SortingEntity> actual = repository.findAll(
                sortingEntity.fooBar.startsWith("1"),
                 QPageRequest.of(0, 5, sortingEntity.fooBar.desc())
        );

        then(actual.getContent()).containsExactly(givenEntityC, givenEntityB, givenEntityA);
    }

    @Test
    void shouldSortUsingSortCombinedWithPredicate() {
        // given
        SortingEntity givenEntityA = repository.save(new SortingEntity(null, "1A"));
        SortingEntity givenEntityC = repository.save(new SortingEntity(null, "1C"));
        SortingEntity givenEntityB = repository.save(new SortingEntity(null, "1B"));
        SortingEntity otherEntity = repository.save(new SortingEntity(null, "2"));

        // when
        Page<SortingEntity> actual = repository.findAll(
                sortingEntity.fooBar.startsWith("1"),
                PageRequest.of(0, 5, Sort.by(Order.desc("fooBar")))
        );

        then(actual.getContent()).containsExactly(givenEntityC, givenEntityB, givenEntityA);
    }
}

