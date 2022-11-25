package com.infobip.spring.data.r2dbc.paging;

import java.util.function.Predicate;

import com.infobip.spring.data.r2dbc.TestBase;
import lombok.AllArgsConstructor;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import reactor.test.StepVerifier;

@AllArgsConstructor
public class PagingEntityPagingRepositoryTest extends TestBase {

    private final PagingEntityPagingRepository repository;

    @Test
    void shouldFindByPage() {
        // given
        var givenEntity = new PagingEntity(null, "givenValue");
        var given = repository.save(givenEntity);

        // when
        var actual = given.thenMany(repository.findAll(Sort.by(Sort.Direction.ASC, "id")));

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
