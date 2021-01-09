package com.infobip.spring.data.jdbc;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Arrays;
import java.util.List;

@NoRepositoryBean
public interface QuerydslJdbcRepository<T, ID>
        extends PagingAndSortingRepository<T, ID>, QuerydslPredicateExecutor<T>, QuerydslJdbcFragment<T> {

    default List<T> save(T... entities) {
        return this.saveAll(Arrays.asList(entities));
    }

    @Override
    <S extends T> List<S> saveAll(Iterable<S> entities);

    @Override
    List<T> findAll();

    List<T> findAll(Predicate predicate);

    @Override
    List<T> findAll(Predicate predicate, Sort sort);

    @Override
    List<T> findAll(Predicate predicate, OrderSpecifier<?>... orderSpecifiers);

    @Override
    List<T> findAll(OrderSpecifier<?>... orderSpecifiers);
}
