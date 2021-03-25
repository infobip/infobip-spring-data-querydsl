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

import com.infobip.spring.data.common.Querydsl;
import com.infobip.spring.data.common.QuerydslExpressionFactory;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.sql.*;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.repository.support.R2dbcRepositoryFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition;
import org.springframework.data.repository.core.support.RepositoryFragment;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.ReactiveTransactionManager;

public class QuerydslR2dbcRepositoryFactory extends R2dbcRepositoryFactory {

    private final SQLQueryFactory sqlQueryFactory;
    private final ReactiveTransactionManager reactiveTransactionManager;
    private final DatabaseClient databaseClient;
    private final R2dbcConverter converter;
    private final QuerydslExpressionFactory querydslExpressionFactory = new QuerydslExpressionFactory(
            QuerydslR2dbcFragment.class);

    public QuerydslR2dbcRepositoryFactory(R2dbcEntityOperations operations,
                                          SQLQueryFactory sqlQueryFactory,
                                          ReactiveTransactionManager reactiveTransactionManager,
                                          DatabaseClient databaseClient) {
        super(operations);
        this.sqlQueryFactory = sqlQueryFactory;
        this.converter = operations.getConverter();
        this.reactiveTransactionManager = reactiveTransactionManager;
        this.databaseClient = databaseClient;
    }

    @Override
    protected RepositoryComposition.RepositoryFragments getRepositoryFragments(RepositoryMetadata metadata) {

        RepositoryComposition.RepositoryFragments fragments = super.getRepositoryFragments(metadata);
        RelationalPathBase<?> path = querydslExpressionFactory.getRelationalPathBaseFromQueryRepositoryClass(
                metadata.getRepositoryInterface());
        Class<?> type = metadata.getDomainType();
        ConstructorExpression<?> constructorExpression = querydslExpressionFactory.getConstructorExpression(type, path);
        RepositoryFragment<Object> simpleQuerydslJdbcFragment = createSimpleQuerydslR2dbcFragment(path,
                                                                                                  constructorExpression);
        RepositoryFragment<Object> querydslJdbcPredicateExecutor = createQuerydslJdbcPredicateExecutor(
                constructorExpression, path);
        return fragments.append(simpleQuerydslJdbcFragment).append(querydslJdbcPredicateExecutor);
    }

    private RepositoryFragment<Object> createSimpleQuerydslR2dbcFragment(RelationalPath<?> path,
                                                                         ConstructorExpression<?> constructor) {
        Object simpleJPAQuerydslFragment = getTargetRepositoryViaReflection(SimpleQuerydslR2dbcFragment.class,
                                                                            sqlQueryFactory,
                                                                            constructor,
                                                                            path,
                                                                            reactiveTransactionManager,
                                                                            databaseClient,
                                                                            converter);
        return RepositoryFragment.implemented(simpleJPAQuerydslFragment);
    }

    private RepositoryFragment<Object> createQuerydslJdbcPredicateExecutor(ConstructorExpression<?> constructorExpression,
                                                                           RelationalPathBase<?> path) {
        Querydsl querydsl = new Querydsl(sqlQueryFactory, new PathBuilder<>(path.getType(), path.getMetadata()));
        Object querydslJdbcPredicateExecutor = getTargetRepositoryViaReflection(
                ReactiveQuerydslR2dbcPredicateExecutor.class,
                constructorExpression,
                path,
                sqlQueryFactory,
                querydsl,
                databaseClient,
                converter);
        return RepositoryFragment.implemented(querydslJdbcPredicateExecutor);
    }
}
