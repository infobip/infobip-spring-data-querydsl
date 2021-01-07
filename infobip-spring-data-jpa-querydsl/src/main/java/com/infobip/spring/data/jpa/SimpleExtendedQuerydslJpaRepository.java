package com.infobip.spring.data.jpa;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.*;
import com.querydsl.jpa.HQLTemplates;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.*;
import com.querydsl.jpa.sql.JPASQLQuery;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.QuerydslJpaRepository;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.function.*;

@Transactional(readOnly = true)
public class SimpleExtendedQuerydslJpaRepository<T, ID extends Serializable> extends QuerydslJpaRepository<T, ID>
        implements ExtendedQuerydslJpaRepository<T, ID> {

    private final EntityPath<T> path;
    private final JPAQueryFactory jpaQueryFactory;
    private final Supplier<JPASQLQuery<T>> jpaSqlFactory;
    private final EntityManager entityManager;

    public SimpleExtendedQuerydslJpaRepository(JpaEntityInformation<T, ID> entityInformation,
                                               EntityManager entityManager,
                                               Supplier<JPASQLQuery<T>> jpaSqlFactory) {
        super(entityInformation, entityManager, SimpleEntityPathResolver.INSTANCE);
        this.jpaQueryFactory = new JPAQueryFactory(HQLTemplates.DEFAULT, entityManager);
        this.path = SimpleEntityPathResolver.INSTANCE.createPath(entityInformation.getJavaType());
        this.entityManager = entityManager;
        this.jpaSqlFactory = jpaSqlFactory;
    }

    @SafeVarargs
    @Override
    public final List<T> save(T... entities) {
        return saveAll(Arrays.asList(entities));
    }

    @Override
    public <O> O query(Function<JPAQuery<?>, O> query) {
        return query.apply(jpaQueryFactory.query());
    }

    @Transactional
    @Override
    public void update(Consumer<JPAUpdateClause> update) {

        update.accept(jpaQueryFactory.update(path));
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

    @SuppressWarnings("unchecked")
    @Override
    protected JPQLQuery<T> createQuery(Predicate... predicate) {
        return (JPQLQuery<T>) super.createQuery(predicate);
    }

    @Transactional
    @Override
    public <O> O executeStoredProcedure(String name, Function<StoredProcedureQueryBuilder, O> query) {
        return query.apply(new StoredProcedureQueryBuilder(name, entityManager));
    }
}
