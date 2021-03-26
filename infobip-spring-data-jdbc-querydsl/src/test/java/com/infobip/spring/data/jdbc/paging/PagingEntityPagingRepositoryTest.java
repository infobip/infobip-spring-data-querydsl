package com.infobip.spring.data.jdbc.paging;

import com.infobip.spring.data.jdbc.TestBase;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.BDDAssertions.then;

@AllArgsConstructor
public class PagingEntityPagingRepositoryTest extends TestBase {

    private final PagingEntityPagingRepository repository;

    @Test
    void shouldFindByPage() {
        // given
        PagingEntity givenEntity = repository.save(new PagingEntity(null, "givenValue"));

        // when
        Page<PagingEntity> actual = repository.findAll(PageRequest.of(0, 1));

        then(actual.getContent()).containsExactly(givenEntity);
    }
}
