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
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jdbc.core.convert.*;
import org.springframework.data.jdbc.repository.QueryMappingConfiguration;
import org.springframework.data.jdbc.repository.support.JdbcRepositoryFactoryBean;
import org.springframework.data.mapping.callback.EntityCallbacks;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.util.Assert;

import java.io.Serializable;

public class QuerydslJdbcRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable>
        extends JdbcRepositoryFactoryBean<T, S, ID> {

    private ApplicationEventPublisher publisher;
    private BeanFactory beanFactory;
    private RelationalMappingContext mappingContext;
    private JdbcConverter converter;
    private DataAccessStrategy dataAccessStrategy;
    private QueryMappingConfiguration queryMappingConfiguration = QueryMappingConfiguration.EMPTY;
    private NamedParameterJdbcOperations operations;
    private EntityCallbacks entityCallbacks;
    private SQLQueryFactory sqlQueryFactory;
    private Dialect dialect;

    protected QuerydslJdbcRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {

        super.setApplicationEventPublisher(publisher);
        this.publisher = publisher;
    }

    @Override
    protected RepositoryFactorySupport doCreateRepositoryFactory() {

        QuerydslJdbcRepositoryFactory jdbcRepositoryFactory = new QuerydslJdbcRepositoryFactory(dataAccessStrategy,
                                                                                                mappingContext,
                                                                                                converter,
                                                                                                dialect,
                                                                                                publisher,
                                                                                                operations,
                                                                                                sqlQueryFactory);
        jdbcRepositoryFactory.setQueryMappingConfiguration(queryMappingConfiguration);
        jdbcRepositoryFactory.setEntityCallbacks(entityCallbacks);

        return jdbcRepositoryFactory;
    }

    @Autowired
    protected void setMappingContext(RelationalMappingContext mappingContext) {

        super.setMappingContext(mappingContext);
        this.mappingContext = mappingContext;
    }

    @Autowired
    protected void setDialect(Dialect dialect) {
        this.dialect = dialect;
    }

    public void setDataAccessStrategy(DataAccessStrategy dataAccessStrategy) {
        super.setDataAccessStrategy(dataAccessStrategy);
        this.dataAccessStrategy = dataAccessStrategy;
    }

    /**
     * @param queryMappingConfiguration can be {@literal null}. {@link #afterPropertiesSet()} defaults to
     *                                  {@link QueryMappingConfiguration#EMPTY} if {@literal null}.
     */
    @Autowired(required = false)
    public void setQueryMappingConfiguration(QueryMappingConfiguration queryMappingConfiguration) {
        super.setQueryMappingConfiguration(queryMappingConfiguration);
        this.queryMappingConfiguration = queryMappingConfiguration;
    }

    @Autowired
    public void setSQLQueryFactory(SQLQueryFactory sqlQueryFactory) {
        this.sqlQueryFactory = sqlQueryFactory;
    }

    public void setJdbcOperations(NamedParameterJdbcOperations operations) {
        this.operations = operations;
    }

    @Autowired
    public void setConverter(JdbcConverter converter) {
        super.setConverter(converter);
        this.converter = converter;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        super.setBeanFactory(beanFactory);
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterPropertiesSet() {

        Assert.state(this.mappingContext != null, "MappingContext is required and must not be null!");
        Assert.state(this.converter != null, "RelationalConverter is required and must not be null!");

        if (this.operations == null) {

            Assert.state(beanFactory != null, "If no JdbcOperations are set a BeanFactory must be available.");

            this.operations = beanFactory.getBean(NamedParameterJdbcOperations.class);
        }

        if (this.dataAccessStrategy == null) {

            Assert.state(beanFactory != null, "If no DataAccessStrategy is set a BeanFactory must be available.");

            this.dataAccessStrategy = this.beanFactory.getBeanProvider(DataAccessStrategy.class) //
                                                      .getIfAvailable(() -> {

                                                          Assert.state(this.dialect != null, "Dialect is required and must not be null!");

                                                          SqlGeneratorSource sqlGeneratorSource = new SqlGeneratorSource(this.mappingContext, this.converter,
                                                                                                                         this.dialect);
                                                          return new DefaultDataAccessStrategy(sqlGeneratorSource, this.mappingContext, this.converter,
                                                                                               this.operations);
                                                      });
        }

        if (this.queryMappingConfiguration == null) {
            this.queryMappingConfiguration = QueryMappingConfiguration.EMPTY;
        }

        if (beanFactory != null) {
            entityCallbacks = EntityCallbacks.create(beanFactory);
        }

        super.afterPropertiesSet();
    }
}
