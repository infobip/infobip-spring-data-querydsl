package com.infobip.spring.data.r2dbc.extension;

import com.infobip.spring.data.r2dbc.SimpleQuerydslR2dbcRepository;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLQueryFactory;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.relational.repository.query.RelationalEntityInformation;

public class SimpleCustomQuerydslJdbcRepository<T, ID> extends SimpleQuerydslR2dbcRepository<T, ID> {

    public SimpleCustomQuerydslJdbcRepository(SQLQueryFactory sqlQueryFactory,
                                              ConstructorExpression<T> constructorExpression,
                                              RelationalPath<?> path,
                                              R2dbcEntityOperations entityOperations,
                                              RelationalEntityInformation<T, ID> entity,
                                              R2dbcConverter converter) {
        super(sqlQueryFactory, constructorExpression, path, entityOperations, entity, converter);
    }
}
