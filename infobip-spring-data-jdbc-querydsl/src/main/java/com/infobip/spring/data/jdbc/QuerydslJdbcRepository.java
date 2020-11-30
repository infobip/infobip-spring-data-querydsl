package com.infobip.spring.data.jdbc;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.dml.SQLUpdateClause;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@NoRepositoryBean
public interface QuerydslJdbcRepository<T, ID> extends CrudRepository<T, ID> {

    List<T> save(T... iterable);

    @Override
    <S extends T> List<S> saveAll(Iterable<S> entities);

    Optional<T> findOne(Predicate predicate);

    @Override
    List<T> findAll();

    List<T> findAll(Predicate predicate);

    <O> O query(Function<SQLQuery<?>, O> query);

    void update(Consumer<SQLUpdateClause> update);

    /**
     * Deletes all entities matching the given {@link Predicate}.
     *
     * @param predicate to match
     * @return amount of affected rows
     */
    long deleteWhere(Predicate predicate);

    /**
     * Returns entity projection used for mapping {@code QT} to {@code T}.
     *
     * @return entity projection
     */
    Expression<T> entityProjection();
}