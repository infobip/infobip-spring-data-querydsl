package com.infobip.spring.data.r2dbc;

import org.springframework.data.querydsl.ReactiveQuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.util.Arrays;

@NoRepositoryBean
public interface QuerydslR2dbcRepository<T, ID> extends ReactiveSortingRepository<T, ID>, ReactiveQuerydslPredicateExecutor<T>, QuerydslR2dbcFragment<T> {

    @Transactional
    default Flux<T> save(T... entities) {
        return this.saveAll(Arrays.asList(entities));
    }
}
