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

import static org.springframework.data.repository.core.support.RepositoryFragment.implemented;

import com.infobip.spring.data.common.Querydsl;
import com.infobip.spring.data.common.QuerydslExpressionFactory;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLQueryFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jdbc.core.convert.DataAccessStrategy;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.repository.support.JdbcRepositoryFactory;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

public class QuerydslJdbcRepositoryFactory extends JdbcRepositoryFactory {

    private final Class<?> REPOSITORY_TARGET_TYPE = QuerydslJdbcFragment.class;

    private final RelationalMappingContext context;
    private final JdbcConverter converter;
    private final SQLQueryFactory sqlQueryFactory;
    private final QuerydslExpressionFactory querydslExpressionFactory = new QuerydslExpressionFactory(
            REPOSITORY_TARGET_TYPE);

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

        var fragments = super.getRepositoryFragments(metadata);

        var repositoryInterface = metadata.getRepositoryInterface();

        if (!REPOSITORY_TARGET_TYPE.isAssignableFrom(repositoryInterface)) {
            return fragments;
        }

        var path = querydslExpressionFactory.getRelationalPathBaseFromQueryRepositoryClass(
                repositoryInterface);
        var type = metadata.getDomainType();
        var constructorExpression = querydslExpressionFactory.getConstructorExpression(type, path);
        QuerydslPredicateExecutor<?> querydslJdbcPredicateExecutor = createQuerydslJdbcPredicateExecutor(metadata,
                                                                                                         constructorExpression,
                                                                                                         path);
        var simpleQuerydslJdbcFragment = createSimpleQuerydslJdbcFragment(
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
        var entity = context.getRequiredPersistentEntity(metadata.getDomainType());
        var querydsl = new Querydsl(sqlQueryFactory, entity);
        return instantiateClass(QuerydslJdbcPredicateExecutor.class,
                                entity,
                                converter,
                                constructorExpression,
                                path,
                                querydsl);
    }

    private SimpleQuerydslJdbcFragment<?> createSimpleQuerydslJdbcFragment(ConstructorExpression<?> constructor,
                                                                           RelationalPath<?> path,
                                                                           QuerydslPredicateExecutor<?> executor) {
        return instantiateClass(SimpleQuerydslJdbcFragment.class,
                                executor,
                                sqlQueryFactory,
                                constructor,
                                path);
    }
}
