package com.infobip.spring.data.r2dbc;

import com.querydsl.sql.SQLQuery;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.r2dbc.core.RowsFetchSpec;

import java.util.function.Function;

public class QueryBuilder<T> {

    private final R2dbcEntityOperations entityOperations;
    private final SQLQuery<T> query;

    QueryBuilder(R2dbcEntityOperations entityOperations, SQLQuery<T> query) {
        this.entityOperations = entityOperations;
        this.query = query;
    }

    public <O> RowsFetchSpec<O> query(Function<SQLQuery<?>, SQLQuery<O>> query) {
        SQLQuery<O> result = query.apply(this.query);
        result.setUseLiterals(true);
        String sql = result.getSQL().getSQL();
        return entityOperations.getDatabaseClient()
                               .sql(sql)
                               .map(entityOperations.getDataAccessStrategy().getRowMapper(result.getType()));
    }

    RowsFetchSpec<T> query() {
        query.setUseLiterals(true);
        String sql = query.getSQL().getSQL();
        return entityOperations.getDatabaseClient()
                               .sql(sql)
                               .map(entityOperations.getDataAccessStrategy().getRowMapper(query.getType()));
    }
}
