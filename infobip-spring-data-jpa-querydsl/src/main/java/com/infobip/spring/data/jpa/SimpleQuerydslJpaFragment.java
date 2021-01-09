package com.infobip.spring.data.jpa;

import com.querydsl.core.types.*;
import com.querydsl.jpa.impl.*;
import com.querydsl.jpa.sql.JPASQLQuery;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.function.Function;
import java.util.function.Supplier;

public class SimpleQuerydslJpaFragment<T> implements QuerydslJpaFragment<T> {

    private final EntityPath<T> path;
    private final JPAQueryFactory jpaQueryFactory;
    private final Supplier<JPASQLQuery<T>> jpaSqlFactory;
    private final EntityManager entityManager;

    public SimpleQuerydslJpaFragment(EntityPath<T> path,
                                     JPAQueryFactory jpaQueryFactory,
                                     Supplier<JPASQLQuery<T>> jpaSqlFactory,
                                     EntityManager entityManager) {
        this.path = path;
        this.jpaQueryFactory = jpaQueryFactory;
        this.jpaSqlFactory = jpaSqlFactory;
        this.entityManager = entityManager;
    }

    @Override
    public <O> O query(Function<JPAQuery<?>, O> query) {
        return query.apply(jpaQueryFactory.query());
    }

    @Transactional
    @Override
    public long update(Function<JPAUpdateClause, Long> update) {

        return update.apply(jpaQueryFactory.update(path));
    }

    @Transactional
    @Override
    public long deleteWhere(Predicate predicate) {

        return jpaQueryFactory.delete(path).where(predicate).execute();
    }

    @Override
    public <O> O jpaSqlQuery(Function<JPASQLQuery<T>, O> query) {
        return query.apply(jpaSqlFactory.get());
    }

    @Override
    public SubQueryExpression<T> jpaSqlSubQuery(Function<JPASQLQuery<T>, SubQueryExpression<T>> query) {
        return jpaSqlQuery(query);
    }

    @Transactional
    @Override
    public <O> O executeStoredProcedure(String name, Function<StoredProcedureQueryBuilder, O> query) {
        return query.apply(new StoredProcedureQueryBuilder(name, entityManager));
    }
}
