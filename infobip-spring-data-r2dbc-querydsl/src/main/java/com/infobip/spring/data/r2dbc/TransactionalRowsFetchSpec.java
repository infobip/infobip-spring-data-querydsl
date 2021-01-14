package com.infobip.spring.data.r2dbc;

import org.springframework.r2dbc.core.RowsFetchSpec;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class TransactionalRowsFetchSpec<T> implements RowsFetchSpec<T> {

    private final RowsFetchSpec<T> rowsFetchSpec;
    private final TransactionalOperator transactionalOperator;

    TransactionalRowsFetchSpec(RowsFetchSpec<T> rowsFetchSpec,
                               TransactionalOperator transactionalOperator) {
        this.rowsFetchSpec = rowsFetchSpec;
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public Mono<T> one() {
        return rowsFetchSpec.one().as(transactionalOperator::transactional);
    }

    @Override
    public Mono<T> first() {
        return rowsFetchSpec.first().as(transactionalOperator::transactional);
    }

    @Override
    public Flux<T> all() {
        return rowsFetchSpec.all().as(transactionalOperator::transactional);
    }
}
