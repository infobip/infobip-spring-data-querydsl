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

import com.querydsl.core.types.*;
import com.querydsl.sql.*;
import com.querydsl.sql.dml.SQLDeleteClause;
import com.querydsl.sql.dml.SQLUpdateClause;
import org.reactivestreams.Publisher;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.repository.query.RelationalEntityInformation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Transactional(readOnly = true)
public class SimpleQuerydslR2dbcRepository<T, ID> implements QuerydslR2dbcRepository<T, ID> {

    private final SQLQueryFactory sqlQueryFactory;
    private final ConstructorExpression<T> constructorExpression;
    private final RelationalPath<T> path;
    private final SimpleR2dbcRepository<T, ID> repository;
    private final R2dbcEntityOperations entityOperations;
    private final RelationalEntityInformation<T, ID> entity;

    @SuppressWarnings("unchecked")
    public SimpleQuerydslR2dbcRepository(SQLQueryFactory sqlQueryFactory,
                                         ConstructorExpression<T> constructorExpression,
                                         RelationalPath<?> path,
                                         R2dbcEntityOperations entityOperations,
                                         RelationalEntityInformation<T, ID> entity,
                                         R2dbcConverter converter) {
        this.sqlQueryFactory = sqlQueryFactory;
        this.constructorExpression = constructorExpression;
        this.path = (RelationalPath<T>) path;
        this.entityOperations = entityOperations;
        this.entity = entity;
        this.repository = new SimpleR2dbcRepository<>(entity, entityOperations, converter);
    }

    @Override
    @Transactional
    public Flux<T> save(T... entities) {
        return saveAll(Stream.of(entities).collect(Collectors.toList()));
    }

    @Override
    public <S extends T> Flux<S> saveAll(Iterable<S> entities) {
        return repository.saveAll(entities);
    }

    @Override
    public Mono<T> findOne(Predicate predicate) {
        SQLQuery<T> sqlQuery = sqlQueryFactory.query()
                                              .select(entityProjection())
                                              .where(predicate)
                                              .from(path);
        return new QueryBuilder<>(entityOperations, sqlQuery).query().one();
    }

    @Override
    public Flux<T> findAll() {
        return repository.findAll();
    }

    @Override
    public Flux<T> findAllById(Iterable<ID> ids) {
        return repository.findAllById(ids);
    }

    @Override
    public Mono<Long> count() {
        return repository.count();
    }

    @Override
    @Transactional
    public Mono<Void> deleteById(ID id) {
        return repository.deleteById(id);
    }

    @Override
    @Transactional
    public Mono<Void> delete(T instance) {
        return repository.delete(instance);
    }

    @Override
    @Transactional
    public Mono<Void> deleteAll(Iterable<? extends T> entities) {
        return repository.deleteAll(entities);
    }

    @Override
    @Transactional
    public Mono<Void> deleteAll() {
        return repository.deleteAll();
    }

    @Override
    public Flux<T> findAll(Predicate predicate) {
        SQLQuery<T> query = sqlQueryFactory.query().select(entityProjection()).from(path).where(predicate);
        return new QueryBuilder<>(entityOperations, query).query().all();
    }

    @Override
    public <O> O query(Function<QueryBuilder<?>, O> builder) {
        return builder.apply(
                new QueryBuilder<>(entityOperations, sqlQueryFactory.query()));
    }

    @Override
    @Transactional
    public Mono<Integer> update(Function<SQLUpdateClause, SQLUpdateClause> update) {
        SQLUpdateClause clause = sqlQueryFactory.update(path);
        clause.setUseLiterals(true);
        String sql = update.apply(clause).getSQL()
                           .stream()
                           .map(SQLBindings::getSQL)
                           .collect(Collectors.joining("\n"));
        return entityOperations.getDatabaseClient()
                               .sql(sql)
                               .fetch()
                               .rowsUpdated();
    }

    @Override
    @Transactional
    public Mono<Integer> deleteWhere(Predicate predicate) {
        SQLDeleteClause clause = sqlQueryFactory.delete(path)
                                                .where(predicate);
        clause.setUseLiterals(true);
        String sql = clause.getSQL()
                           .stream()
                           .map(SQLBindings::getSQL)
                           .collect(Collectors.joining("\n"));
        return entityOperations.getDatabaseClient()
                               .sql(sql)
                               .fetch()
                               .rowsUpdated();
    }

    @Override
    public Expression<T> entityProjection() {
        return constructorExpression;
    }

    @Override
    @Transactional
    public <S extends T> Mono<S> save(S instance) {
        return repository.save(instance);
    }

    @Override
    public Mono<T> findById(ID id) {
        return repository.findById(id);
    }

    @Override
    public Mono<Boolean> existsById(ID id) {
        return repository.existsById(id);
    }

    @Override
    public Flux<T> findAll(Sort sort) {
        return repository.findAll(sort);
    }

    @Override
    public <S extends T> Flux<S> saveAll(Publisher<S> entityStream) {
        return repository.saveAll(entityStream);
    }

    @Override
    public Mono<T> findById(Publisher<ID> id) {
        return repository.findById(id);
    }

    @Override
    public Mono<Boolean> existsById(Publisher<ID> id) {
        return repository.existsById(id);
    }

    @Override
    public Flux<T> findAllById(Publisher<ID> idStream) {
        return repository.findAllById(idStream);
    }

    @Override
    public Mono<Void> deleteById(Publisher<ID> id) {
        return repository.deleteById(id);
    }

    @Override
    public Mono<Void> deleteAll(Publisher<? extends T> entityStream) {
        return repository.deleteAll(entityStream);
    }
}
