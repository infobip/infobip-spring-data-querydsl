/*
 * Copyright 2017-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.infobip.spring.data.r2dbc;

import java.util.function.Function;
import java.util.stream.Collectors;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLBindings;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.SQLUpdateClause;
import org.springframework.data.r2dbc.convert.EntityRowMapper;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

public class SimpleQuerydslR2dbcFragment<T> implements QuerydslR2dbcFragment<T> {

    private final SQLQueryFactory sqlQueryFactory;
    private final ConstructorExpression<T> constructorExpression;
    private final RelationalPath<T> path;
    private final ReactiveTransactionManager reactiveTransactionManager;
    private final DatabaseClient databaseClient;
    private final R2dbcConverter converter;

    @SuppressWarnings("unchecked")
    public SimpleQuerydslR2dbcFragment(SQLQueryFactory sqlQueryFactory,
                                       ConstructorExpression<T> constructorExpression,
                                       RelationalPath<?> path,
                                       ReactiveTransactionManager reactiveTransactionManager,
                                       DatabaseClient databaseClient,
                                       R2dbcConverter converter) {
        this.sqlQueryFactory = sqlQueryFactory;
        this.constructorExpression = constructorExpression;
        this.path = (RelationalPath<T>) path;
        this.reactiveTransactionManager = reactiveTransactionManager;
        this.databaseClient = databaseClient;
        this.converter = converter;
    }

    @Override
    public <O> RowsFetchSpec<O> query(Function<SQLQuery<?>, SQLQuery<O>> query) {
        return createQuery(query);
    }

    @Override
    @Transactional
    public Mono<Long> update(Function<SQLUpdateClause, SQLUpdateClause> update) {
        var clause = sqlQueryFactory.update(path);
        clause.setUseLiterals(true);
        var sql = update.apply(clause).getSQL()
                        .stream()
                        .map(SQLBindings::getSQL)
                        .collect(Collectors.joining("\n"));
        return databaseClient.sql(sql)
                             .fetch()
                             .rowsUpdated();
    }

    @Override
    @Transactional
    public Mono<Long> deleteWhere(Predicate predicate) {
        var clause = sqlQueryFactory.delete(path)
                                    .where(predicate);
        clause.setUseLiterals(true);
        var sql = clause.getSQL()
                        .stream()
                        .map(SQLBindings::getSQL)
                        .collect(Collectors.joining("\n"));
        return databaseClient.sql(sql)
                             .fetch()
                             .rowsUpdated();
    }

    @Override
    public Expression<T> entityProjection() {
        return constructorExpression;
    }

    private <O> RowsFetchSpec<O> createQuery(Function<SQLQuery<?>, SQLQuery<O>> query) {
        var result = query.apply(sqlQueryFactory.query());
        result.setUseLiterals(true);
        var sql = result.getSQL().getSQL();
        var mapper = new EntityRowMapper<O>(result.getType(), converter);
        return new SimpleRowsFetchSpec<>(databaseClient.sql(sql)
                                                       .map(mapper));
    }
}
