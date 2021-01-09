package com.infobip.spring.data.jpa;

import com.querydsl.core.types.*;
import com.querydsl.jpa.JPQLQueryFactory;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAUpdateClause;
import com.querydsl.jpa.sql.JPASQLQuery;

import java.util.function.Function;

public interface JPAQuerydslFragment<T> {

    /**
     * @see JPQLQueryFactory#query()
     */
    <O> O query(Function<JPAQuery<?>, O> query);

    /**
     * @see JPQLQueryFactory#update(EntityPath)
     */
    long update(Function<JPAUpdateClause, Long> update);

    /**
     * Deletes all entities matching the given {@link Predicate}.
     *
     * @param predicate to match
     * @return amount of affected rows
     */
    long deleteWhere(Predicate predicate);

    <O> O jpaSqlQuery(Function<JPASQLQuery<T>, O> query);

    SubQueryExpression<T> jpaSqlSubQuery(Function<JPASQLQuery<T>, SubQueryExpression<T>> query);

    <O> O executeStoredProcedure(String name, Function<StoredProcedureQueryBuilder, O> query);
}
