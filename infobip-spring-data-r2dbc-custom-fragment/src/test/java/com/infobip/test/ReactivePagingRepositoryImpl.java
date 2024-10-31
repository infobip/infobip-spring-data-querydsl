package com.infobip.test;

import com.infobip.spring.data.r2dbc.QuerydslR2dbcFragment;
import org.springframework.context.annotation.Lazy;
import reactor.core.publisher.Mono;

public class ReactivePagingRepositoryImpl<T> implements ReactivePagingRepository<T> {

    private final QuerydslR2dbcFragment querydslR2dbcFragment;

    public ReactivePagingRepositoryImpl(@Lazy QuerydslR2dbcFragment querydslR2dbcFragment) {
        this.querydslR2dbcFragment = querydslR2dbcFragment;
    }

    @Override
    public Mono<T> simplePaging(String string) {
        throw new UnsupportedOperationException();
    }
}
