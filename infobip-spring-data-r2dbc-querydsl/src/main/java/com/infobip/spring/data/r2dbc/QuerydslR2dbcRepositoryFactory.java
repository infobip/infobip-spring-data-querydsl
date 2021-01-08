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
import com.querydsl.core.types.*;
import com.querydsl.sql.*;
import org.springframework.core.ResolvableType;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.repository.support.R2dbcRepositoryFactory;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.relational.repository.query.RelationalEntityInformation;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QuerydslR2dbcRepositoryFactory extends R2dbcRepositoryFactory {

    private final R2dbcEntityOperations operations;
    private final SQLQueryFactory sqlQueryFactory;
    private final Class<?> repositoryBaseClass;

    public QuerydslR2dbcRepositoryFactory(R2dbcEntityOperations operations,
                                          SQLQueryFactory sqlQueryFactory, Class<?> repositoryBaseClass) {
        super(operations);
        this.operations = operations;
        this.sqlQueryFactory = sqlQueryFactory;
        this.repositoryBaseClass = repositoryBaseClass;
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata repositoryMetadata) {
        return repositoryBaseClass;
    }

    @Override
    protected Object getTargetRepository(RepositoryInformation repositoryInformation) {

        Class<?> type = repositoryInformation.getDomainType();
        RelationalPath<?> relationalPathBase = getRelationalPathBase(repositoryInformation);
        ConstructorExpression<?> constructor = getConstructorExpression(type, relationalPathBase);

        RelationalEntityInformation<?, ?> entityInformation = getEntityInformation(repositoryInformation.getDomainType(),
                                                                                   repositoryInformation);

        return new SimpleQuerydslR2dbcRepository(sqlQueryFactory, constructor, relationalPathBase, operations, entityInformation, operations.getConverter());
    }

    private <T, ID> RelationalEntityInformation<T, ID> getEntityInformation(Class<T> domainClass,
                                                                            @Nullable RepositoryInformation information) {

        RelationalPersistentEntity<?> entity = this.operations.getConverter().getMappingContext().getRequiredPersistentEntity(domainClass);

        return new MappingRelationalEntityInformation<>((RelationalPersistentEntity<T>) entity);
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

    private RelationalPathBase<?> getRelationalPathBase(RepositoryInformation repositoryInformation) {

        Class<?> entityType = ResolvableType.forClass(repositoryInformation.getRepositoryInterface())
                                            .as(QuerydslR2dbcRepository.class)
                                            .getGeneric(0)
                                            .resolve();
        if (entityType == null) {
            throw new IllegalArgumentException("Could not resolve query class for " + repositoryInformation);
        }

        return getRelationalPathBase(getQueryClass(entityType));
    }

    private Class<?> getQueryClass(Class<?> entityType) {
        String fullName = entityType.getPackage().getName() + ".Q" + entityType.getSimpleName();
        try {
            return entityType.getClassLoader().loadClass(fullName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Unable to load class " + fullName);
        }
    }

    private RelationalPathBase<?> getRelationalPathBase(Class<?> queryClass) {
        String fieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, queryClass.getSimpleName().substring(1));
        Field field = ReflectionUtils.findField(queryClass, fieldName);

        if (field == null) {
            throw new IllegalArgumentException("Did not find a static field of the same type in " + queryClass);
        }

        return (RelationalPathBase<?>) ReflectionUtils.getField(field, null);
    }
}
