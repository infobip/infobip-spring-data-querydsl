package com.infobip.spring.data.jdbc;

import com.infobip.spring.data.common.Querydsl;
import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.types.*;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLQuery;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.*;
import org.springframework.data.querydsl.QSort;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

public class QuerydslJdbcPredicateExecutor<T> implements QuerydslPredicateExecutor<T> {

    private final ConstructorExpression<T> constructorExpression;
    private final RelationalPath<T> path;
    private final Querydsl querydsl;

    public QuerydslJdbcPredicateExecutor(ConstructorExpression<T> constructorExpression,
                                         RelationalPath<T> path,
                                         Querydsl querydsl) {
        this.constructorExpression = constructorExpression;
        this.path = path;
        this.querydsl = querydsl;
    }

    @Override
    public Optional<T> findOne(Predicate predicate) {
        Assert.notNull(predicate, "Predicate must not be null!");

        try {
            return Optional.ofNullable(createQuery(predicate).select(constructorExpression).fetchOne());
        } catch (NonUniqueResultException ex) {
            throw new IncorrectResultSizeDataAccessException(ex.getMessage(), 1, ex);
        }
    }

    @Override
    public List<T> findAll(Predicate predicate) {

        Assert.notNull(predicate, "Predicate must not be null!");

        return createQuery(predicate).select(constructorExpression).fetch();
    }

    @Override
    public List<T> findAll(Predicate predicate, OrderSpecifier<?>... orders) {

        Assert.notNull(predicate, "Predicate must not be null!");
        Assert.notNull(orders, "Order specifiers must not be null!");

        return executeSorted(createQuery(predicate).select(constructorExpression), orders);
    }

    @Override
    public List<T> findAll(Predicate predicate, Sort sort) {

        Assert.notNull(predicate, "Predicate must not be null!");
        Assert.notNull(sort, "Sort must not be null!");

        return executeSorted(createQuery(predicate).select(constructorExpression), sort);
    }

    @Override
    public List<T> findAll(OrderSpecifier<?>... orders) {

        Assert.notNull(orders, "Order specifiers must not be null!");

        return executeSorted(createQuery(new Predicate[0]).select(constructorExpression), orders);
    }

    @Override
    public Page<T> findAll(Predicate predicate, Pageable pageable) {

        Assert.notNull(predicate, "Predicate must not be null!");
        Assert.notNull(pageable, "Pageable must not be null!");

        final SQLQuery<?> countQuery = createCountQuery(predicate);
        SQLQuery<T> query = querydsl.applyPagination(pageable, createQuery(predicate).select(constructorExpression));

        return PageableExecutionUtils.getPage(query.fetch(), pageable, countQuery::fetchCount);
    }

    @Override
    public long count(Predicate predicate) {
        return createQuery(predicate).fetchCount();
    }

    @Override
    public boolean exists(Predicate predicate) {
        return createQuery(predicate).fetchCount() > 0;
    }

    protected SQLQuery<?> createQuery(Predicate... predicate) {

        Assert.notNull(predicate, "Predicate must not be null!");

        return doCreateQuery(predicate);
    }

    protected SQLQuery<?> createCountQuery(@Nullable Predicate... predicate) {
        return doCreateQuery(predicate);
    }

    private SQLQuery<?> doCreateQuery(@Nullable Predicate... predicate) {

        SQLQuery<?> query = querydsl.createQuery(path);

        if (predicate != null) {
            query = query.where(predicate);
        }

        return query;
    }

    private List<T> executeSorted(SQLQuery<T> query, OrderSpecifier<?>... orders) {
        return executeSorted(query, new QSort(orders));
    }

    private List<T> executeSorted(SQLQuery<T> query, Sort sort) {
        return querydsl.applySorting(sort, query).fetch();
    }
}
