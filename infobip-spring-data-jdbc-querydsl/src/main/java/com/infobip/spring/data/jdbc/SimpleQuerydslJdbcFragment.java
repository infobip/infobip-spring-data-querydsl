/*
 * Copyright 2017-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.infobip.spring.data.jdbc;

import com.querydsl.core.types.*;
import com.querydsl.sql.*;
import com.querydsl.sql.dml.SQLUpdateClause;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;

@Transactional(readOnly = true)
public class SimpleQuerydslJdbcFragment<T> implements QuerydslJdbcFragment<T> {

    private final SQLQueryFactory sqlQueryFactory;
    private final ConstructorExpression<T> constructorExpression;
    private final RelationalPath<T> path;

    @SuppressWarnings("unchecked")
    public SimpleQuerydslJdbcFragment(SQLQueryFactory sqlQueryFactory,
                                      ConstructorExpression<T> constructorExpression,
                                      RelationalPath<?> path) {
        this.sqlQueryFactory = sqlQueryFactory;
        this.constructorExpression = constructorExpression;
        this.path = (RelationalPath<T>) path;
    }

    @Override
    public <O> O query(Function<SQLQuery<?>, O> query) {
        return query.apply(sqlQueryFactory.query());
    }

    @Override
    @Transactional
    public long update(Function<SQLUpdateClause, Long> update) {
        return update.apply(sqlQueryFactory.update(path));
    }

    @Override
    @Transactional
    public long deleteWhere(Predicate predicate) {
        return sqlQueryFactory.delete(path).where(predicate).execute();
    }

    @Override
    public Expression<T> entityProjection() {
        return constructorExpression;
    }
}
