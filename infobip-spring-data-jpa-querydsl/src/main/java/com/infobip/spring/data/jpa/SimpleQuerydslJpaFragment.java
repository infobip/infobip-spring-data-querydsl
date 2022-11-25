package com.infobip.spring.data.jpa;

import java.util.function.Function;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import com.querydsl.jpa.sql.JPASQLQuery;
import jakarta.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class SimpleQuerydslJpaFragment<T> implements QuerydslJpaFragment<T> {

    private final EntityPath<T> path;
    private final JPAQueryFactory jpaQueryFactory;
    private final JPASQLQueryFactory jpaSqlQueryFactory;
    private final EntityManager entityManager;

    public SimpleQuerydslJpaFragment(EntityPath<T> path,
                                     JPAQueryFactory jpaQueryFactory,
                                     JPASQLQueryFactory jpaSqlQueryFactory,
                                     EntityManager entityManager) {
        this.path = path;
        this.jpaQueryFactory = jpaQueryFactory;
        this.jpaSqlQueryFactory = jpaSqlQueryFactory;
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

    @SuppressWarnings("unchecked")
    @Override
    public <O> O jpaSqlQuery(Function<JPASQLQuery<T>, O> query) {
        return query.apply((JPASQLQuery<T>) jpaSqlQueryFactory.create());
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
