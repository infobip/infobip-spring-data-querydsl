package com.infobip.spring.data.jpa;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Arrays;
import java.util.List;

@NoRepositoryBean
public interface ExtendedQuerydslJpaRepository<T, ID>
        extends JpaRepository<T, ID>, QuerydslPredicateExecutor<T>, QuerydslJpaFragment<T> {

    default List<T> save(T... entities) {
        return this.saveAll(Arrays.asList(entities));
    }

    @Override
    List<T> findAll(Predicate predicate);

    @Override
    List<T> findAll(Predicate predicate, Sort sort);

    @Override
    List<T> findAll(Predicate predicate, OrderSpecifier<?>... orderSpecifiers);

    @Override
    List<T> findAll(OrderSpecifier<?>... orderSpecifiers);
}
