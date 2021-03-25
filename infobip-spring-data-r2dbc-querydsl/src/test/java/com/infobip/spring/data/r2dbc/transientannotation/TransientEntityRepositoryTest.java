package com.infobip.spring.data.r2dbc.transientannotation;

import com.infobip.spring.data.r2dbc.TestBase;
import lombok.AllArgsConstructor;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Predicate;

@AllArgsConstructor
public class TransientEntityRepositoryTest extends TestBase {

    private final TransientEntityRepository repository;

    @Test
    void shouldFindAll() {
        // given
        String givenValue = "givenValue";
        Mono<TransientEntity> given = repository.save(new TransientEntity(null, givenValue, "givenTransientValue"));

        // when
        Flux<TransientEntity> actual = given.thenMany(repository.findAll());

        // then
        StepVerifier.create(actual)
                    .expectNextMatches(transientEntity(givenValue))
                    .verifyComplete();
    }

    private Predicate<TransientEntity> transientEntity(String value) {
        return entity -> {
            BDDAssertions.then(entity).isEqualTo(new TransientEntity(entity.getId(), value, null));
            return true;
        };
    }
}
