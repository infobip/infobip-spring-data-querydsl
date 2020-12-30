package com.infobip.spring.data.jdbc.extension;

import com.infobip.spring.data.jdbc.SimpleQuerydslJdbcRepository;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLQueryFactory;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.mapping.PersistentEntity;

public class SimpleCustomQuerydslJdbcRepository<T, ID> extends SimpleQuerydslJdbcRepository<T, ID> {

    public SimpleCustomQuerydslJdbcRepository(JdbcAggregateOperations entityOperations,
                                              PersistentEntity<T, ?> entity,
                                              SQLQueryFactory sqlQueryFactory,
                                              ConstructorExpression<T> constructorExpression,
                                              RelationalPath<?> path) {
        super(entityOperations, entity, sqlQueryFactory, constructorExpression, path);
    }
}
