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

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.types.*;
import com.querydsl.sql.*;
import com.querydsl.sql.dml.SQLUpdateClause;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.jdbc.repository.support.SimpleJdbcRepository;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.*;

@Transactional(readOnly = true)
public class SimpleQuerydslJdbcRepository<T, ID> implements QuerydslJdbcRepository<T, ID> {

    private final SQLQueryFactory sqlQueryFactory;
    private final ConstructorExpression<T> constructorExpression;
    private final RelationalPath<T> path;
    private final SimpleJdbcRepository<T, ID> repository;

    @SuppressWarnings("unchecked")
    public SimpleQuerydslJdbcRepository(JdbcAggregateOperations entityOperations,
                                        PersistentEntity<T, ?> entity,
                                        SQLQueryFactory sqlQueryFactory,
                                        ConstructorExpression<T> constructorExpression,
                                        RelationalPath<?> path) {
        this.sqlQueryFactory = sqlQueryFactory;
        this.constructorExpression = constructorExpression;
        this.path = (RelationalPath<T>) path;
        this.repository = new SimpleJdbcRepository<>(entityOperations, entity);
    }

    @Override
    @Transactional
    public List<T> save(T... iterable) {
        return Stream.of(iterable)
                     .map(this::save)
                     .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        return StreamSupport.stream(entities.spliterator(), false)
                            .map(this::save)
                            .collect(Collectors.toList());
    }

    @Override
    public Optional<T> findOne(Predicate predicate) {
        try {
            return Optional.ofNullable(sqlQueryFactory.query()
                                                      .select(entityProjection())
                                                      .where(predicate)
                                                      .from(path)
                                                      .fetchOne());
        } catch (NonUniqueResultException ex) {
            throw new IncorrectResultSizeDataAccessException(ex.getMessage(), 1, ex);
        }
    }

    @Override
    public List<T> findAll() {
        return sqlQueryFactory.query()
                              .select(entityProjection())
                              .from(path)
                              .fetch();
    }

    @Override
    public Iterable<T> findAllById(Iterable<ID> ids) {
        return repository.findAllById(ids);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    @Transactional
    public void deleteById(ID id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public void delete(T instance) {
        repository.delete(instance);
    }

    @Override
    @Transactional
    public void deleteAll(Iterable<? extends T> entities) {
        repository.deleteAll(entities);
    }

    @Override
    @Transactional
    public void deleteAll() {
        repository.deleteAll();
    }

    @Override
    public List<T> findAll(Predicate predicate) {
        return sqlQueryFactory.query().select(entityProjection()).from(path).where(predicate).fetch();
    }

    @Override
    public <O> O query(Function<SQLQuery<?>, O> query) {
        return query.apply(sqlQueryFactory.query());
    }

    @Override
    @Transactional
    public void update(Consumer<SQLUpdateClause> update) {
        update.accept(sqlQueryFactory.update(path));
    }

    @Override
    @Transactional
    public long deleteWhere(Predicate predicate) {
        return sqlQueryFactory.delete(path).where(predicate).execute();
    }

    @Override
    public Expression<T> entityProjection() {
        return constructorExpression;
    }

    @Override
    @Transactional
    public <S extends T> S save(S instance) {
        return repository.save(instance);
    }

    @Override
    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }

    @Override
    public boolean existsById(ID id) {
        return repository.existsById(id);
    }
}