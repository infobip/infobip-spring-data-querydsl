package com.infobip.spring.data.r2dbc;

import java.util.Arrays;

import org.springframework.data.querydsl.ReactiveQuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

@NoRepositoryBean
public interface QuerydslR2dbcRepository<T, ID> extends ReactiveSortingRepository<T, ID>, ReactiveCrudRepository<T, ID>, ReactiveQuerydslPredicateExecutor<T>, QuerydslR2dbcFragment<T> {

    default Flux<T> save(T... entities) {
        return this.saveAll(Arrays.asList(entities));
    }

}
