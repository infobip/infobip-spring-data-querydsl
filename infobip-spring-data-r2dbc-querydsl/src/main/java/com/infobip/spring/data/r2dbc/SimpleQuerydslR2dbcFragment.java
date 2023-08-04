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

import com.querydsl.core.types.*;
import com.querydsl.sql.*;
import com.querydsl.sql.dml.SQLUpdateClause;
import org.springframework.data.r2dbc.convert.EntityRowMapper;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SimpleQuerydslR2dbcFragment<T> implements QuerydslR2dbcFragment<T> {

    private final SQLQueryFactory sqlQueryFactory;
    private final ConstructorExpression<T> constructorExpression;
    private final RelationalPath<T> path;
    private final DatabaseClient databaseClient;
    private final R2dbcConverter converter;
    private final QuerydslParameterBinder querydslParameterBinder;

    @SuppressWarnings("unchecked")
    public SimpleQuerydslR2dbcFragment(SQLQueryFactory sqlQueryFactory,
                                       ConstructorExpression<T> constructorExpression,
                                       RelationalPath<?> path,
                                       DatabaseClient databaseClient,
                                       R2dbcConverter converter, QuerydslParameterBinder querydslParameterBinder) {
        this.sqlQueryFactory = sqlQueryFactory;
        this.constructorExpression = constructorExpression;
        this.path = (RelationalPath<T>) path;
        this.databaseClient = databaseClient;
        this.converter = converter;
        this.querydslParameterBinder = querydslParameterBinder;
    }

    @Override
    public <O> RowsFetchSpec<O> query(Function<SQLQuery<?>, SQLQuery<O>> query) {
        return createQuery(query);
    }

    @Override
    @Transactional
    public Mono<Long> update(Function<SQLUpdateClause, SQLUpdateClause> update) {
        var clause = sqlQueryFactory.update(path);
        var sqlBindings = update.apply(clause).getSQL();
        var bindings = getBindings(sqlBindings);
        var sql = sqlBindings.stream()
                             .map(SQLBindings::getSQL)
                             .collect(Collectors.joining("\n"));
        return querydslParameterBinder.bind(databaseClient, bindings, sql)
                                      .fetch()
                                      .rowsUpdated();
    }

    private List<Object> getBindings(List<SQLBindings> sqlBindings) {
        return sqlBindings.stream()
                          .flatMap(bindings -> bindings.getNullFriendlyBindings().stream())
                          .toList();
    }

    @Override
    @Transactional
    public Mono<Long> deleteWhere(Predicate predicate) {
        var clause = sqlQueryFactory.delete(path)
                                    .where(predicate);
        var sqlBindings = clause.getSQL();
        var bindings = getBindings(sqlBindings);
        var sql = sqlBindings.stream()
                             .map(SQLBindings::getSQL)
                             .collect(Collectors.joining("\n"));
        return querydslParameterBinder.bind(databaseClient, bindings, sql)
                                      .fetch()
                                      .rowsUpdated();
    }

    @Override
    public Expression<T> entityProjection() {
        return constructorExpression;
    }

    private <O> RowsFetchSpec<O> createQuery(Function<SQLQuery<?>, SQLQuery<O>> query) {
        var result = query.apply(sqlQueryFactory.query());
        var mapper = new EntityRowMapper<>(result.getType(), converter);
        var sql = result.getSQL();
        return new SimpleRowsFetchSpec<>(
                querydslParameterBinder.bind(databaseClient, sql.getNullFriendlyBindings(), sql.getSQL()).map(mapper));
    }
}
