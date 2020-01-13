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
import com.querydsl.sql.dml.SQLUpdateClause;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.jdbc.repository.support.SimpleJdbcRepository;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Transactional(readOnly = true)
public class SimpleQuerydslJdbcRepository<T, QT extends RelationalPathBase<QT>, ID> extends SimpleJdbcRepository<T, ID>
        implements QuerydslJdbcRepository<T, QT, ID> {

    private final SQLQueryFactory sqlQueryFactory;
    private final ConstructorExpression<T> constructorExpression;
    private final RelationalPath<?> path;

    public SimpleQuerydslJdbcRepository(JdbcAggregateOperations entityOperations,
                                        PersistentEntity<T, ?> entity,
                                        SQLQueryFactory sqlQueryFactory,
                                        ConstructorExpression<T> constructorExpression,
                                        RelationalPath<?> path) {
        super(entityOperations, entity);
        this.sqlQueryFactory = sqlQueryFactory;
        this.constructorExpression = constructorExpression;
        this.path = path;
    }

    @SafeVarargs
    @Override
    public final List<T> save(T... iterable) {
        return Stream.of(iterable)
                     .map(this::save)
                     .collect(Collectors.toList());
    }

    @Override
    public List<T> findAll(Predicate predicate) {
        return sqlQueryFactory.query().select(constructorExpression).from(path).where(predicate).fetch();
    }

    @Override
    public <O> O query(Function<SQLQuery<?>, O> query) {
        return query.apply(sqlQueryFactory.query());
    }

    @Override
    public void update(Consumer<SQLUpdateClause> update) {
        update.accept(sqlQueryFactory.update(path));
    }

    @Override
    public long deleteWhere(Predicate predicate) {
        return sqlQueryFactory.delete(path).where(predicate).execute();
    }

    @Override
    public Expression<T> entityProjection() {
        return constructorExpression;
    }
}