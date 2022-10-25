package com.infobip.spring.data.jdbc.paging;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PagingEntityPagingRepository extends PagingAndSortingRepository<PagingEntity, Long>, CrudRepository<PagingEntity, Long> {

}
