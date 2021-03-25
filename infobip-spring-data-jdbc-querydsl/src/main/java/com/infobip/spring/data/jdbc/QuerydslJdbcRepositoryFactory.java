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
package com.infobip.spring.data.jdbc;

import com.google.common.base.CaseFormat;
import com.infobip.spring.data.common.Querydsl;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.sql.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.ResolvableType;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.jdbc.core.convert.DataAccessStrategy;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.repository.support.JdbcRepositoryFactory;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nullable;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.data.repository.core.support.RepositoryFragment.implemented;

public class QuerydslJdbcRepositoryFactory extends JdbcRepositoryFactory {

    private final RelationalMappingContext context;
    private final JdbcConverter converter;
    private final SQLQueryFactory sqlQueryFactory;

    public QuerydslJdbcRepositoryFactory(DataAccessStrategy dataAccessStrategy,
                                         RelationalMappingContext context,
                                         JdbcConverter converter,
                                         Dialect dialect,
                                         ApplicationEventPublisher publisher,
                                         NamedParameterJdbcOperations operations,
                                         SQLQueryFactory sqlQueryFactory) {
        super(dataAccessStrategy, context, converter, dialect, publisher, operations);
        this.context = context;
        this.converter = converter;
        this.sqlQueryFactory = sqlQueryFactory;
    }

    @Override
    protected RepositoryComposition.RepositoryFragments getRepositoryFragments(RepositoryMetadata metadata) {

        RepositoryComposition.RepositoryFragments fragments = super.getRepositoryFragments(metadata);
        RelationalPathBase<?> path = getRelationalPathBaseFromQueryRepositoryClass(metadata.getRepositoryInterface());
        Class<?> type = metadata.getDomainType();
        ConstructorExpression<?> constructorExpression = getConstructorExpression(type, path);
        QuerydslPredicateExecutor<?> querydslJdbcPredicateExecutor = createQuerydslJdbcPredicateExecutor(metadata,
                                                                                                         constructorExpression,
                                                                                                         path);
        SimpleQuerydslJdbcFragment<?> simpleQuerydslJdbcFragment = createSimpleQuerydslJdbcFragment(
                constructorExpression,
                path,
                querydslJdbcPredicateExecutor
        );
        return fragments.append(implemented(simpleQuerydslJdbcFragment))
                        .append(implemented(querydslJdbcPredicateExecutor));
    }

    private QuerydslJdbcPredicateExecutor<?> createQuerydslJdbcPredicateExecutor(RepositoryMetadata metadata,
                                                                                 ConstructorExpression<?> constructorExpression,
                                                                                 RelationalPathBase<?> path) {
        RelationalPersistentEntity<?> entity = context.getRequiredPersistentEntity(metadata.getDomainType());
        Querydsl querydsl = new Querydsl(sqlQueryFactory, new PathBuilder<>(path.getType(), path.getMetadata()));
        return getTargetRepositoryViaReflection(QuerydslJdbcPredicateExecutor.class,
                                                entity,
                                                converter,
                                                constructorExpression,
                                                path,
                                                querydsl);
    }

    private SimpleQuerydslJdbcFragment<?> createSimpleQuerydslJdbcFragment(ConstructorExpression<?> constructor,
                                                                           RelationalPath<?> path,
                                                                           QuerydslPredicateExecutor<?> executor) {
        return getTargetRepositoryViaReflection(SimpleQuerydslJdbcFragment.class,
                                                executor,
                                                sqlQueryFactory,
                                                constructor,
                                                path);
    }

    private ConstructorExpression<?> getConstructorExpression(Class<?> type, RelationalPath<?> pathBase) {
        Constructor<?> constructor = getConstructor(type);

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

    @Nullable
    private Constructor<?> getConstructor(Class<?> type) {
        Constructor<?>[] declaredConstructors = type.getDeclaredConstructors();
        Constructor<?> persistenceConstructor = Arrays.stream(declaredConstructors)
                                                      .filter(constructor -> constructor.isAnnotationPresent(
                                                              PersistenceConstructor.class))
                                                      .findAny()
                                                      .orElse(null);

        if (Objects.nonNull(persistenceConstructor)) {
            return persistenceConstructor;
        }

        return Arrays.stream(declaredConstructors)
                     .max(Comparator.comparingInt(Constructor::getParameterCount))
                     .orElse(null);
    }

    private RelationalPathBase<?> getRelationalPathBaseFromQueryRepositoryClass(Class<?> repositoryInterface) {

        Class<?> entityType = ResolvableType.forClass(repositoryInterface)
                                            .as(QuerydslJdbcFragment.class)
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
