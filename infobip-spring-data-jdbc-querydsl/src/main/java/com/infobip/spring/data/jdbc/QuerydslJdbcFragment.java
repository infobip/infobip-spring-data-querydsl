package com.infobip.spring.data.jdbc;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.dml.SQLUpdateClause;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface QuerydslJdbcFragment<T> {

    /**
     * @deprecated use {@link #queryOne(Function)} or {@link #queryMany(Function)} instead
     */
    @Deprecated
    <O> O query(Function<SQLQuery<?>, O> query);

    Optional<T> queryOne(Function<SQLQuery<?>, SQLQuery<T>> query);

    List<T> queryMany(Function<SQLQuery<?>, SQLQuery<T>> query);

    /**
     * @return amount of affected rows
     */
    long update(Function<SQLUpdateClause, Long> update);

    /**
     * Deletes all entities matching the given {@link Predicate}.
     *
     * @param predicate to match
     * @return amount of affected rows
     */
    long deleteWhere(Predicate predicate);

    /**
     * Returns entity projection used for mapping {@code QT} to {@code T}.
     *
     * @return entity projection
     */
    Expression<T> entityProjection();
}
