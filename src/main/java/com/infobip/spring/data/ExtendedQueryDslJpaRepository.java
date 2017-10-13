package com.infobip.spring.data;

import com.querydsl.core.types.*;
import com.querydsl.jpa.JPQLQueryFactory;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAUpdateClause;
import com.querydsl.jpa.sql.JPASQLQuery;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@NoRepositoryBean
public interface ExtendedQueryDslJpaRepository<T, ID extends Serializable> extends JpaRepository<T, ID>, QuerydslPredicateExecutor<T> {

    List<T> save(T... iterable);

    /**
     * @deprecated use {@link CrudRepository#findById(Object)} instead.
     */
    @Deprecated
    Optional<T> findOneById(ID id);

    /**
     * @deprecated use {@link QuerydslPredicateExecutor#findOne(Predicate)})} instead.
     */
    @Deprecated
    Optional<T> findOneByPredicate(Predicate predicate);

    @Override
    List<T> findAll(Predicate predicate);

    @Override
    List<T> findAll(Predicate predicate, Sort sort);

    @Override
    List<T> findAll(Predicate predicate, OrderSpecifier<?>... orderSpecifiers);

    @Override
    List<T> findAll(OrderSpecifier<?>... orderSpecifiers);

    /**
     * @see JPQLQueryFactory#query()
     */
    <O> O query(Function<JPAQuery<?>, O> query);

    /**
     * @see JPQLQueryFactory#update(EntityPath)
     */
    void update(Consumer<JPAUpdateClause> update);

    /**
     * Deletes all entities matching the given {@link Predicate}.
     *
     * @param predicate to match
     * @return amount of affected rows
     */
    long deleteWhere(Predicate predicate);

    <O> O jpaSqlQuery(Function<JPASQLQuery<T>, O> query);

    SubQueryExpression<T> jpaSqlSubQuery(Function<JPASQLQuery<T>, SubQueryExpression<T>> query);

    <O> O executeStoredProcedure(String name, Function<StoredProcedureQueryBuilder, O> query);
}