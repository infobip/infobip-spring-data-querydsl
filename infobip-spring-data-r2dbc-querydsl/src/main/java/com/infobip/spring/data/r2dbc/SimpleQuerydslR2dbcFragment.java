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

import com.querydsl.core.QueryMetadata;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.PathImpl;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.PredicateOperation;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimpleOperation;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLBindings;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.SQLUpdateClause;
import org.springframework.data.r2dbc.convert.EntityRowMapper;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.util.TypeInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

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

        applyConverter(result.getMetadata());

        var sql = result.getSQL().getSQL();
        var mapper = new EntityRowMapper<O>(result.getType(), converter);
        return new SimpleRowsFetchSpec<>(databaseClient.sql(sql)
                                                       .map(mapper));
    }

    private void applyConverter(QueryMetadata queryMetadata) {
        applyConverterToSubQuery(queryMetadata);
        applyConverterToWhere(queryMetadata);
    }

    private void applyConverterToSubQuery(QueryMetadata queryMetadata) {
        if (queryMetadata.getProjection() instanceof ConstructorExpression<?> projection) {
            for (Expression<?> arg : projection.getArgs()) applyToSubQueryExpression(arg);
        }

        if (queryMetadata.getWhere() instanceof PredicateOperation where) {
            for (Expression<?> arg : where.getArgs()) applyToSubQueryExpression(arg);
        }

        if (queryMetadata.getHaving() instanceof PredicateOperation having) {
            for (Expression<?> arg : having.getArgs()) applyToSubQueryExpression(arg);
        }
    }

    private void applyToSubQueryExpression(Expression<?> expression) {
        if (expression instanceof SQLQuery<?> subQuery) {
            applyConverter(subQuery.getMetadata());
        } else if (expression instanceof SimpleOperation<?> operation) {
            for (Expression<?> arg : operation.getArgs()) {
                applyToSubQueryExpression(arg);
            }
        }
    }

    private void applyConverterToWhere(QueryMetadata queryMetadata) {
        if (queryMetadata.getWhere() != null) {
            var where = (Predicate) doApplyConverter(queryMetadata.getWhere());

            queryMetadata.clearWhere();
            queryMetadata.addWhere(where);
        }
    }

    private Expression<?> doApplyConverter(Expression<?> expression) {
        if (expression instanceof PredicateOperation predicateOperation && predicateOperation.getArgs().size() == 2) {
            var left = predicateOperation.getArg(0);
            var right = predicateOperation.getArg(1);

            if (left instanceof PathImpl<?> && right instanceof ConstantImpl<?> c) {
                right = Expressions.constant(
                    requireNonNull(converter.writeValue(c.getConstant(), TypeInformation.of(left.getType())))
                );
            } else {
                left = doApplyConverter(left);
                right = doApplyConverter(right);
            }
            return ExpressionUtils.predicate(predicateOperation.getOperator(), left, right);
        }
        return expression;
    }
}
