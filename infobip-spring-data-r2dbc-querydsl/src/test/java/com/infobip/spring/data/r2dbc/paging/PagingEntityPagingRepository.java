package com.infobip.spring.data.r2dbc.paging;

import org.springframework.data.repository.reactive.ReactiveSortingRepository;

public interface PagingEntityPagingRepository extends ReactiveSortingRepository<PagingEntity, Long> {

}
