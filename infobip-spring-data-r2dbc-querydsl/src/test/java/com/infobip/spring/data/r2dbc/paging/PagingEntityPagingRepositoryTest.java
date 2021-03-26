package com.infobip.spring.data.r2dbc.paging;

import com.infobip.spring.data.r2dbc.TestBase;
import lombok.AllArgsConstructor;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Predicate;

@AllArgsConstructor
public class PagingEntityPagingRepositoryTest extends TestBase {

    private final PagingEntityPagingRepository repository;

    @Test
    void shouldFindByPage() {
        // given
        PagingEntity givenEntity = new PagingEntity(null, "givenValue");
        Mono<PagingEntity> given = repository.save(givenEntity);

        // when
        Flux<PagingEntity> actual = given.thenMany(repository.findAll(Sort.by(Sort.Direction.ASC, "id")));

        // then
        StepVerifier.create(actual)
                    .expectNextMatches(pagingEntity(givenEntity.getValue()))
                    .verifyComplete();
    }

    private Predicate<PagingEntity> pagingEntity(String value) {
        return entity -> {
            BDDAssertions.then(entity).isEqualTo(new PagingEntity(entity.getId(), value));
            return true;
        };
    }
}
