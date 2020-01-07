package com.infobip.spring.data.jdbc;

import com.querydsl.sql.SQLQuery;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.function.Function;

@NoRepositoryBean
public interface QuerydslJdbcRepository<T, ID> extends CrudRepository<T, ID> {

//    List<T> save(T... iterable);

    <O> O query(Function<SQLQuery<?>, O> query);
}
