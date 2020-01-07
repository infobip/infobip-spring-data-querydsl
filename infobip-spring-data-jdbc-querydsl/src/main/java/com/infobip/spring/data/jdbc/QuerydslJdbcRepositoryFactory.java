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

import com.querydsl.sql.SQLQueryFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.jdbc.core.convert.DataAccessStrategy;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.mapping.callback.EntityCallbacks;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

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
    protected Object getTargetRepository(RepositoryInformation repositoryInformation) {

        JdbcAggregateTemplate template = new JdbcAggregateTemplate(publisher, context, converter, accessStrategy);

        SimpleQuerydslJdbcRepository<?, Object> repository = new SimpleQuerydslJdbcRepository<>(template,
                                                                                                context.getRequiredPersistentEntity(
                                                                                            repositoryInformation.getDomainType()),
                                                                                                sqlQueryFactory);

        if (entityCallbacks != null) {
            template.setEntityCallbacks(entityCallbacks);
        }

        return repository;
    }
}
