package com.infobip.spring.data.r2dbc;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.sql.dml.SQLUpdateClause;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@NoRepositoryBean
public interface QuerydslR2dbcRepository<T, ID> extends ReactiveSortingRepository<T, ID> {

    Flux<T> save(T... entities);

    Mono<T> findOne(Predicate predicate);

    Flux<T> findAll(Predicate predicate);

    <O> O query(Function<QueryBuilder<?>, O> builder);

    Mono<Integer> update(Function<SQLUpdateClause, SQLUpdateClause> update);

    /**
     * Deletes all entities matching the given {@link Predicate}.
     *
     * @param predicate to match
     * @return amount of affected rows
     */
    Mono<Integer> deleteWhere(Predicate predicate);

    /**
     * Returns entity projection used for mapping {@code QT} to {@code T}.
     *
     * @return entity projection
     */
    Expression<T> entityProjection();
}
