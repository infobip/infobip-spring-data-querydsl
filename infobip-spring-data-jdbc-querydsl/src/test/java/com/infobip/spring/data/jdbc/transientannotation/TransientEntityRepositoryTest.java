package com.infobip.spring.data.jdbc.transientannotation;

import com.infobip.spring.data.jdbc.TestBase;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.infobip.spring.data.jdbc.transientannotation.QTransientEntity.transientEntity;
import static org.assertj.core.api.BDDAssertions.then;

@AllArgsConstructor
public class TransientEntityRepositoryTest extends TestBase {

    private final TransientEntityRepository repository;

    @Test
    void shouldFindAll() {
        // given
        TransientEntity givenEntity = repository.save(new TransientEntity(null, "givenValue", "givenTransientValue"));

        // when
        Optional<TransientEntity> actual = repository.findOne(transientEntity.id.eq(givenEntity.getId()));

        // then
        then(actual).contains(new TransientEntity(givenEntity.getId(), givenEntity.getValue(), null));
    }
}
