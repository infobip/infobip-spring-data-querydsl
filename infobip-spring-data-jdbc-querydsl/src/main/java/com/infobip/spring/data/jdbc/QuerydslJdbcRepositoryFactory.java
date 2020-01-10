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

import com.querydsl.core.types.*;
import com.querydsl.sql.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.ResolvableType;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.jdbc.core.convert.DataAccessStrategy;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.mapping.callback.EntityCallbacks;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.*;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class QuerydslJdbcRepositoryFactory extends org.springframework.data.jdbc.repository.support.JdbcRepositoryFactory {

    private final RelationalMappingContext context;
    private final JdbcConverter converter;
    private final ApplicationEventPublisher publisher;
    private final DataAccessStrategy accessStrategy;
    private final SQLQueryFactory sqlQueryFactory;
    private EntityCallbacks entityCallbacks;

    public QuerydslJdbcRepositoryFactory(DataAccessStrategy dataAccessStrategy,
                                         RelationalMappingContext context,
                                         JdbcConverter converter,
                                         ApplicationEventPublisher publisher,
                                         NamedParameterJdbcOperations operations,
                                         SQLQueryFactory sqlQueryFactory) {
        super(dataAccessStrategy, context, converter, publisher, operations);
        this.publisher = publisher;
        this.context = context;
        this.converter = converter;
        this.accessStrategy = dataAccessStrategy;
        this.sqlQueryFactory = sqlQueryFactory;
    }

    public void setEntityCallbacks(EntityCallbacks entityCallbacks) {
        super.setEntityCallbacks(entityCallbacks);
        this.entityCallbacks = entityCallbacks;
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata repositoryMetadata) {
        return SimpleQuerydslJdbcRepository.class;
    }

    @Override
    protected Object getTargetRepository(RepositoryInformation repositoryInformation) {

        JdbcAggregateTemplate template = new JdbcAggregateTemplate(publisher, context, converter, accessStrategy);

        Class<?> type = repositoryInformation.getDomainType();
        RelationalPath<?> relationalPathBase = getRelationalPathBase(repositoryInformation);
        ConstructorExpression<?> constructor = getConstructorExpression(type, relationalPathBase);
        SimpleQuerydslJdbcRepository<?, ?, ?> repository = new SimpleQuerydslJdbcRepository(template,
                                                                                            context.getRequiredPersistentEntity(
                                                                                                    type),
                                                                                            sqlQueryFactory,
                                                                                            constructor,
                                                                                            relationalPathBase);

        if (entityCallbacks != null) {
            template.setEntityCallbacks(entityCallbacks);
        }

        return repository;
    }

    private ConstructorExpression<?> getConstructorExpression(Class<?> type, RelationalPath<?> pathBase) {
        Constructor<?>[] constructors = type.getConstructors();

        if (constructors.length != 1) {
            throw new IllegalArgumentException(
                    "Spring Data JDBC Querydsl supports only entities with only one constructor");
        }

        Constructor<?> constructor = constructors[0];

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

        ResolvableType queryType = ResolvableType.forClass(repositoryInformation.getRepositoryInterface())
                                                 .as(QuerydslJdbcRepository.class)
                                                 .getGeneric(1);
        if (queryType.getRawClass() == null) {
            throw new IllegalArgumentException("Could not resolve query class for " + repositoryInformation);
        }

        return getRelationalPathBase(queryType.getRawClass());
    }

    private RelationalPathBase<?> getRelationalPathBase(Class<?> queryType) {
        Field field = ReflectionUtils.findField(queryType, queryType.getSimpleName().substring(1));

        if (field == null) {
            throw new IllegalArgumentException("Did not find a static field of the same type in " + queryType);
        }

        return (RelationalPathBase<?>) ReflectionUtils.getField(field, null);
    }
}
