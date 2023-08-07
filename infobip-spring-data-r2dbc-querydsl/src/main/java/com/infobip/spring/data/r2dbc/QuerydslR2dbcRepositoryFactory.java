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
import com.querydsl.sql.*;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.repository.support.R2dbcRepositoryFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition;
import org.springframework.data.repository.core.support.RepositoryFragment;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.binding.BindMarkersFactoryResolver;

public class QuerydslR2dbcRepositoryFactory extends R2dbcRepositoryFactory {

    private final Class<?> REPOSITORY_TARGET_TYPE = QuerydslR2dbcFragment.class;

    private final SQLQueryFactory sqlQueryFactory;
    private final DatabaseClient databaseClient;
    private final R2dbcConverter converter;
    private final QuerydslExpressionFactory querydslExpressionFactory = new QuerydslExpressionFactory(
            REPOSITORY_TARGET_TYPE);
    private final QuerydslParameterBinder querydslParameterBinder;

    public QuerydslR2dbcRepositoryFactory(R2dbcEntityOperations operations,
                                          SQLQueryFactory sqlQueryFactory,
                                          DatabaseClient databaseClient) {
        super(operations);
        this.sqlQueryFactory = sqlQueryFactory;
        this.converter = operations.getConverter();
        this.databaseClient = databaseClient;
        this.querydslParameterBinder = new QuerydslParameterBinder(
                BindMarkersFactoryResolver.resolve(databaseClient.getConnectionFactory()));
    }

    @Override
    protected RepositoryComposition.RepositoryFragments getRepositoryFragments(RepositoryMetadata metadata) {

        var fragments = super.getRepositoryFragments(metadata);

        var repositoryInterface = metadata.getRepositoryInterface();

        if (!REPOSITORY_TARGET_TYPE.isAssignableFrom(repositoryInterface)) {
            return fragments;
        }

        var path = querydslExpressionFactory.getRelationalPathBaseFromQueryRepositoryClass(
                repositoryInterface);
        var type = metadata.getDomainType();
        var constructorExpression = querydslExpressionFactory.getConstructorExpression(type, path);
        var simpleQuerydslJdbcFragment = createSimpleQuerydslR2dbcFragment(path,
                                                                           constructorExpression);
        var querydslJdbcPredicateExecutor = createQuerydslJdbcPredicateExecutor(
                constructorExpression, path);
        return fragments.append(simpleQuerydslJdbcFragment).append(querydslJdbcPredicateExecutor);
    }

    private RepositoryFragment<Object> createSimpleQuerydslR2dbcFragment(RelationalPath<?> path,
                                                                         ConstructorExpression<?> constructor) {
        var simpleJPAQuerydslFragment = getTargetRepositoryViaReflection(SimpleQuerydslR2dbcFragment.class,
                                                                         sqlQueryFactory,
                                                                         constructor,
                                                                         path,
                                                                         databaseClient,
                                                                         converter,
                                                                         querydslParameterBinder);
        return RepositoryFragment.implemented(simpleJPAQuerydslFragment);
    }

    private RepositoryFragment<Object> createQuerydslJdbcPredicateExecutor(ConstructorExpression<?> constructorExpression,
                                                                           RelationalPathBase<?> path) {

        var context = converter.getMappingContext();
        @SuppressWarnings("unchecked")
        var entity = context.getRequiredPersistentEntity(constructorExpression.getType());

        var querydsl = new Querydsl(sqlQueryFactory, entity);
        var querydslJdbcPredicateExecutor = getTargetRepositoryViaReflection(
                ReactiveQuerydslR2dbcPredicateExecutor.class,
                constructorExpression,
                path,
                sqlQueryFactory,
                querydsl,
                databaseClient,
                converter,
                querydslParameterBinder);
        return RepositoryFragment.implemented(querydslJdbcPredicateExecutor);
    }
}
