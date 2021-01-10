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
import com.querydsl.sql.dml.SQLDeleteClause;
import com.querydsl.sql.dml.SQLUpdateClause;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.r2dbc.core.RowsFetchSpec;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
public class SimpleQuerydslR2dbcFragment<T> implements QuerydslR2dbcFragment<T> {

    private final SQLQueryFactory sqlQueryFactory;
    private final ConstructorExpression<T> constructorExpression;
    private final RelationalPath<T> path;
    private final R2dbcEntityOperations entityOperations;

    @SuppressWarnings("unchecked")
    public SimpleQuerydslR2dbcFragment(SQLQueryFactory sqlQueryFactory,
                                         ConstructorExpression<T> constructorExpression,
                                         RelationalPath<?> path,
                                         R2dbcEntityOperations entityOperations) {
        this.sqlQueryFactory = sqlQueryFactory;
        this.constructorExpression = constructorExpression;
        this.path = (RelationalPath<T>) path;
        this.entityOperations = entityOperations;
    }

    @Override
    public <O> RowsFetchSpec<O> query(Function<SQLQuery<?>, SQLQuery<O>> query) {
        return createQuery(query);
    }

    @Override
    @Transactional
    public Mono<Integer> update(Function<SQLUpdateClause, SQLUpdateClause> update) {
        SQLUpdateClause clause = sqlQueryFactory.update(path);
        clause.setUseLiterals(true);
        String sql = update.apply(clause).getSQL()
                           .stream()
                           .map(SQLBindings::getSQL)
                           .collect(Collectors.joining("\n"));
        return entityOperations.getDatabaseClient()
                               .sql(sql)
                               .fetch()
                               .rowsUpdated();
    }

    @Override
    @Transactional
    public Mono<Integer> deleteWhere(Predicate predicate) {
        SQLDeleteClause clause = sqlQueryFactory.delete(path)
                                                .where(predicate);
        clause.setUseLiterals(true);
        String sql = clause.getSQL()
                           .stream()
                           .map(SQLBindings::getSQL)
                           .collect(Collectors.joining("\n"));
        return entityOperations.getDatabaseClient()
                               .sql(sql)
                               .fetch()
                               .rowsUpdated();
    }

    @Override
    public Expression<T> entityProjection() {
        return constructorExpression;
    }

    private <O> RowsFetchSpec<O> createQuery(Function<SQLQuery<?>, SQLQuery<O>> query) {
        SQLQuery<O> result = query.apply(sqlQueryFactory.query());
        result.setUseLiterals(true);
        String sql = result.getSQL().getSQL();
        return entityOperations.getDatabaseClient()
                               .sql(sql)
                               .map(entityOperations.getDataAccessStrategy().getRowMapper(result.getType()));
    }
}
