package com.infobip.spring.data.r2dbc.transientannotation;

import java.util.function.Predicate;

import com.infobip.spring.data.r2dbc.TestBase;
import lombok.AllArgsConstructor;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

@AllArgsConstructor
public class TransientEntityRepositoryTest extends TestBase {

    private final TransientEntityRepository repository;

    @Test
    void shouldFindAll() {
        // given
        var givenValue = "givenValue";
        var given = repository.save(new TransientEntity(null, givenValue, "givenTransientValue"));

        // when
        var actual = given.thenMany(repository.findAll());

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
