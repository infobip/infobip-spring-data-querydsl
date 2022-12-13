package com.infobip.spring.data.r2dbc;

import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class SimpleRowsFetchSpec<T> implements RowsFetchSpec<T> {

    private final RowsFetchSpec<T> rowsFetchSpec;

    SimpleRowsFetchSpec(RowsFetchSpec<T> rowsFetchSpec) {
        this.rowsFetchSpec = rowsFetchSpec;
    }

    @Override
    public Mono<T> one() {
        return rowsFetchSpec.one();
    }

    @Override
    public Mono<T> first() {
        return rowsFetchSpec.first();
    }

    @Override
    public Flux<T> all() {
        return rowsFetchSpec.all();
    }
}
