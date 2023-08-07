package com.infobip.spring.data.r2dbc;

import com.infobip.spring.data.common.Querydsl;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.sql.*;
import org.reactivestreams.Publisher;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QSort;
import org.springframework.data.querydsl.ReactiveQuerydslPredicateExecutor;
import org.springframework.data.r2dbc.convert.EntityRowMapper;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.lang.Nullable;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Transactional(readOnly = true)
public class ReactiveQuerydslR2dbcPredicateExecutor<T> implements ReactiveQuerydslPredicateExecutor<T> {

    private final ConstructorExpression<T> constructorExpression;
    private final RelationalPath<T> path;
    private final SQLQueryFactory sqlQueryFactory;
    private final Querydsl querydsl;
    private final DatabaseClient databaseClient;
    private final R2dbcConverter converter;
    private final QuerydslParameterBinder querydslParameterBinder;

    public ReactiveQuerydslR2dbcPredicateExecutor(ConstructorExpression<T> constructorExpression,
                                                  RelationalPath<T> path,
                                                  SQLQueryFactory sqlQueryFactory,
                                                  Querydsl querydsl,
                                                  DatabaseClient databaseClient,
                                                  R2dbcConverter converter,
                                                  QuerydslParameterBinder querydslParameterBinder) {
        this.constructorExpression = constructorExpression;
        this.path = path;
        this.sqlQueryFactory = sqlQueryFactory;
        this.querydsl = querydsl;
        this.databaseClient = databaseClient;
        this.converter = converter;
        this.querydslParameterBinder = querydslParameterBinder;
    }

    @Override
    public Mono<T> findOne(Predicate predicate) {
        var sqlQuery = sqlQueryFactory.query()
                                      .select(constructorExpression)
                                      .where(predicate)
                                      .from(path);
        return query(sqlQuery).one();
    }

    @Override
    public Flux<T> findAll(Predicate predicate) {
        var query = sqlQueryFactory.query().select(constructorExpression).from(path).where(predicate);
        return query(query).all();
    }

    @Override
    public Flux<T> findAll(Predicate predicate, OrderSpecifier<?>... orders) {

        Assert.notNull(predicate, "Predicate must not be null!");
        Assert.notNull(orders, "Order specifiers must not be null!");

        return executeSorted(createQuery(predicate).select(constructorExpression), orders);
    }

    @Override
    public Flux<T> findAll(Predicate predicate, Sort sort) {

        Assert.notNull(predicate, "Predicate must not be null!");
        Assert.notNull(sort, "Sort must not be null!");

        return executeSorted(createQuery(predicate).select(constructorExpression), sort);
    }

    @Override
    public Flux<T> findAll(OrderSpecifier<?>... orders) {

        Assert.notNull(orders, "Order specifiers must not be null!");

        return executeSorted(createQuery(new Predicate[0]).select(constructorExpression), orders);
    }

    @Override
    public Mono<Long> count(Predicate predicate) {
        var count = ((SimpleExpression<?>) constructorExpression.getArgs().get(0)).count();
        var sqlQuery = sqlQueryFactory.query()
                                      .select(count)
                                      .where(predicate)
                                      .from(path);
        return query(sqlQuery).one();
    }

    @Override
    public Mono<Boolean> exists(Predicate predicate) {
        return count(predicate).map(result -> result > 0);
    }

    @Override
    public <S extends T, R, P extends Publisher<R>> P findBy(Predicate predicate, Function<FluentQuery.ReactiveFluentQuery<S>, P> queryFunction) {
        throw new UnsupportedOperationException();
    }

    protected SQLQuery<?> createQuery(Predicate... predicate) {

        Assert.notNull(predicate, "Predicate must not be null!");

        return doCreateQuery(predicate);
    }

    private SQLQuery<?> doCreateQuery(@Nullable Predicate... predicate) {

        var query = querydsl.createQuery(path);

        if (predicate != null) {
            query = query.where(predicate);
        }

        return query;
    }

    private Flux<T> executeSorted(SQLQuery<T> query, OrderSpecifier<?>... orders) {
        return executeSorted(query, new QSort(orders));
    }

    private Flux<T> executeSorted(SQLQuery<T> query, Sort sort) {
        var sqlQuery = querydsl.applySorting(sort, query);
        return query(sqlQuery).all();
    }

    private <O> RowsFetchSpec<O> query(SQLQuery<O> query) {
        var mapper = new EntityRowMapper<>(query.getType(), converter);
        var sql = query.getSQL();
        return querydslParameterBinder.bind(databaseClient, sql.getNullFriendlyBindings(), sql.getSQL()).map(mapper);
    }
}
