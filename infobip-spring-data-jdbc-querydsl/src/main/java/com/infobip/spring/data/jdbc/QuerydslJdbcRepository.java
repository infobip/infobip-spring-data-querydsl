package com.infobip.spring.data.jdbc;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.dml.SQLUpdateClause;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@NoRepositoryBean
public interface QuerydslJdbcRepository<T, ID> extends PagingAndSortingRepository<T, ID> {

    List<T> save(T... entities);

    @Override
    <S extends T> List<S> saveAll(Iterable<S> entities);

    Optional<T> findOne(Predicate predicate);

    @Override
    List<T> findAll();

    List<T> findAll(Predicate predicate);

    <O> O query(Function<SQLQuery<?>, O> query);

    /**
     * @return amount of affected rows
     */
    long update(Function<SQLUpdateClause, Long> update);

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
