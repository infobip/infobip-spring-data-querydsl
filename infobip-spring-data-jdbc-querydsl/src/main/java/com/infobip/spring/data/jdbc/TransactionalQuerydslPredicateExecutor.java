package com.infobip.spring.data.jdbc;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface TransactionalQuerydslPredicateExecutor<T> extends QuerydslPredicateExecutor<T> {

}
