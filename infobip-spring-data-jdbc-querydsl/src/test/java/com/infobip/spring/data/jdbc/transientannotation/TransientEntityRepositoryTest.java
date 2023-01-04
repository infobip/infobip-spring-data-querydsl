package com.infobip.spring.data.jdbc.transientannotation;

import static com.infobip.spring.data.jdbc.transientannotation.QTransientEntity.transientEntity;
import static org.assertj.core.api.BDDAssertions.then;

import com.infobip.spring.data.jdbc.TestBase;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;

@AllArgsConstructor
public class TransientEntityRepositoryTest extends TestBase {

    private final TransientEntityRepository repository;

    @Test
    void shouldFindAll() {
        // given
        var givenEntity = repository.save(new TransientEntity(null, "givenValue", "givenTransientValue"));

        // when
        var actual = repository.findOne(transientEntity.id.eq(givenEntity.id()));

        // then
        then(actual).contains(new TransientEntity(givenEntity.id(), givenEntity.value(), null));
    }
}
