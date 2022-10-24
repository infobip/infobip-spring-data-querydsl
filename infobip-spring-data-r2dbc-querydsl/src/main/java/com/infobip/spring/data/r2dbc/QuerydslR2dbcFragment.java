package com.infobip.spring.data.r2dbc;

import java.util.function.Function;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.dml.SQLUpdateClause;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Mono;

public interface QuerydslR2dbcFragment<T> {

    <O> RowsFetchSpec<O> query(Function<SQLQuery<?>, SQLQuery<O>> builder);

    Mono<Long> update(Function<SQLUpdateClause, SQLUpdateClause> update);

    /**
     * Deletes all entities matching the given {@link Predicate}.
     *
     * @param predicate to match
     * @return amount of affected rows
     */
    Mono<Long> deleteWhere(Predicate predicate);

    /**
     * Returns entity projection used for mapping {@code QT} to {@code T}.
     *
     * @return entity projection
     */
    Expression<T> entityProjection();
}
