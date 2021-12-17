package com.infobip.spring.data.jdbc;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@NoRepositoryBean
public interface QuerydslJdbcRepository<T, ID>
        extends PagingAndSortingRepository<T, ID>, QuerydslPredicateExecutor<T>, QuerydslJdbcFragment<T> {

    default List<T> save(T... entities) {
        return this.saveAll(Arrays.asList(entities));
    }

    @Override
    <S extends T> List<S> saveAll(Iterable<S> entities);

    default Stream<T> streamAll() {
        return streamAllBy();
    }

    /**
     * @deprecated use {@link QuerydslJdbcRepository#streamAll} instead
     */
    @Deprecated
    Stream<T> streamAllBy();

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
