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

import com.google.common.base.CaseFormat;
import com.infobip.spring.data.common.Querydsl;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.sql.*;
import org.springframework.core.ResolvableType;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.repository.support.R2dbcRepositoryFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition;
import org.springframework.data.repository.core.support.RepositoryFragment;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QuerydslR2dbcRepositoryFactory extends R2dbcRepositoryFactory {

    private final SQLQueryFactory sqlQueryFactory;
    private final ReactiveTransactionManager reactiveTransactionManager;
    private final DatabaseClient databaseClient;
    private final R2dbcConverter converter;

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
        RelationalPathBase<?> path = getRelationalPathBaseFromQueryRepositoryClass(metadata.getRepositoryInterface());
        Class<?> type = metadata.getDomainType();
        ConstructorExpression<?> constructorExpression = getConstructorExpression(type, path);
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

    private ConstructorExpression<?> getConstructorExpression(Class<?> type, RelationalPath<?> pathBase) {
        Constructor<?> constructor = Arrays.stream(type.getDeclaredConstructors())
                                           .max(Comparator.comparingInt(Constructor::getParameterCount))
                                           .orElse(null);

        if (constructor == null) {
            throw new IllegalArgumentException(
                    "Could not discover preferred constructor for " + type);
        }

        Map<String, Path<?>> columnNameToColumn = pathBase.getColumns()
                                                          .stream()
                                                          .collect(Collectors.toMap(
                                                                  column -> column.getMetadata().getName(),
                                                                  Function.identity()));

        Path<?>[] paths = Stream.of(constructor.getParameters())
                                .map(Parameter::getName)
                                .map(columnNameToColumn::get)
                                .toArray(Path[]::new);

        return Projections.constructor(type, paths);
    }

    private RelationalPathBase<?> getRelationalPathBaseFromQueryRepositoryClass(Class<?> repositoryInterface) {

        Class<?> entityType = ResolvableType.forClass(repositoryInterface)
                                            .as(QuerydslR2dbcFragment.class)
                                            .getGeneric(0)
                                            .resolve();
        if (entityType == null) {
            throw new IllegalArgumentException("Could not resolve query class for " + repositoryInterface);
        }

        return getRelationalPathBaseFromQueryClass(getQueryClass(entityType));
    }

    private Class<?> getQueryClass(Class<?> entityType) {
        String fullName = entityType.getPackage().getName() + ".Q" + entityType.getSimpleName();
        try {
            return entityType.getClassLoader().loadClass(fullName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Unable to load class " + fullName);
        }
    }

    private RelationalPathBase<?> getRelationalPathBaseFromQueryClass(Class<?> queryClass) {
        String fieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, queryClass.getSimpleName().substring(1));
        Field field = ReflectionUtils.findField(queryClass, fieldName);

        if (field == null) {
            throw new IllegalArgumentException("Did not find a static field of the same type in " + queryClass);
        }

        return (RelationalPathBase<?>) ReflectionUtils.getField(field, null);
    }
}
