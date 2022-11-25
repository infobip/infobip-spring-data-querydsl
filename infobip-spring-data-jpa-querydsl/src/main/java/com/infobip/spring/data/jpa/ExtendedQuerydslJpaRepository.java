package com.infobip.spring.data.jpa;

import static org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ExtendedQuerydslJpaRepository<T, ID>
        extends JpaRepository<T, ID>, QuerydslPredicateExecutor<T>, QuerydslJpaFragment<T> {

    default List<T> save(T... entities) {
        return this.saveAll(Arrays.asList(entities));
    }

    @QueryHints(@QueryHint(name = HINT_FETCH_SIZE, value = "1000"))
    @Query("select e from #{#entityName} e")
    Stream<T> streamAll();

    @Override
    List<T> findAll(Predicate predicate);

    @Override
    List<T> findAll(Predicate predicate, Sort sort);

    @Override
    List<T> findAll(Predicate predicate, OrderSpecifier<?>... orderSpecifiers);

    @Override
    List<T> findAll(OrderSpecifier<?>... orderSpecifiers);
}
