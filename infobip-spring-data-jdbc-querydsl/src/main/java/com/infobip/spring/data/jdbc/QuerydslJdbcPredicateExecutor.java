package com.infobip.spring.data.jdbc;

import com.infobip.spring.data.common.Querydsl;
import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.types.*;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLQuery;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.data.domain.*;
import org.springframework.data.jdbc.core.convert.EntityRowMapper;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.querydsl.QSort;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLStateSQLExceptionTranslator;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class QuerydslJdbcPredicateExecutor<T> implements QuerydslPredicateExecutor<T> {

    private final RelationalPersistentEntity<T> entity;
    private final JdbcConverter converter;
    private final ConstructorExpression<T> constructorExpression;
    private final RelationalPath<T> path;
    private final Querydsl querydsl;

    public QuerydslJdbcPredicateExecutor(RelationalPersistentEntity<T> entity,
                                         JdbcConverter converter,
                                         ConstructorExpression<T> constructorExpression,
                                         RelationalPath<T> path,
                                         Querydsl querydsl) {
        this.entity = entity;
        this.converter = converter;
        this.constructorExpression = constructorExpression;
        this.path = path;
        this.querydsl = querydsl;
    }

    @Override
    public Optional<T> findOne(Predicate predicate) {
        Assert.notNull(predicate, "Predicate must not be null!");

        try {
            return Optional.ofNullable(query(predicate));
        } catch (NonUniqueResultException ex) {
            throw new IncorrectResultSizeDataAccessException(ex.getMessage(), 1, ex);
        }
    }

    @Override
    public List<T> findAll(Predicate predicate) {

        Assert.notNull(predicate, "Predicate must not be null!");

        return queryMany(createQuery(predicate).select(constructorExpression));
    }

    @Override
    public List<T> findAll(Predicate predicate, OrderSpecifier<?>... orders) {

        Assert.notNull(predicate, "Predicate must not be null!");
        Assert.notNull(orders, "Order specifiers must not be null!");

        return executeSorted(createQuery(predicate).select(constructorExpression), orders);
    }

    @Override
    public List<T> findAll(Predicate predicate, Sort sort) {

        Assert.notNull(predicate, "Predicate must not be null!");
        Assert.notNull(sort, "Sort must not be null!");

        return executeSorted(createQuery(predicate).select(constructorExpression), sort);
    }

    @Override
    public List<T> findAll(OrderSpecifier<?>... orders) {

        Assert.notNull(orders, "Order specifiers must not be null!");

        return executeSorted(createQuery(new Predicate[0]).select(constructorExpression), orders);
    }

    @Override
    public Page<T> findAll(Predicate predicate, Pageable pageable) {

        Assert.notNull(predicate, "Predicate must not be null!");
        Assert.notNull(pageable, "Pageable must not be null!");

        final SQLQuery<?> countQuery = createCountQuery(predicate);

        List<T> content = queryMany(
                querydsl.applyPagination(pageable, createQuery(predicate).select(constructorExpression)));
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount);
    }

    @Override
    public long count(Predicate predicate) {
        return createQuery(predicate).fetchCount();
    }

    @Override
    public boolean exists(Predicate predicate) {
        return createQuery(predicate).fetchCount() > 0;
    }

    protected SQLQuery<?> createQuery(Predicate... predicate) {

        Assert.notNull(predicate, "Predicate must not be null!");

        return doCreateQuery(predicate);
    }

    protected SQLQuery<?> createCountQuery(@Nullable Predicate... predicate) {
        return doCreateQuery(predicate);
    }

    private SQLQuery<?> doCreateQuery(@Nullable Predicate... predicate) {

        SQLQuery<?> query = querydsl.createQuery(path);

        if (predicate != null) {
            query = query.where(predicate);
        }

        return query;
    }

    private List<T> executeSorted(SQLQuery<T> query, OrderSpecifier<?>... orders) {
        return executeSorted(query, new QSort(orders));
    }

    private List<T> executeSorted(SQLQuery<T> query, Sort sort) {
        return queryMany(querydsl.applySorting(sort, query));
    }

    @Nullable
    private T query(Predicate predicate) {
        SQLQuery<T> query = createQuery(predicate).select(constructorExpression);
        return queryOne(query);
    }

    @Nullable
    T queryOne(SQLQuery<T> query) {
        List<T> results = queryMany(query);
        return DataAccessUtils.nullableSingleResult(results);
    }

    List<T> queryMany(SQLQuery<T> query) {
        RowMapper<T> rowMapper = new EntityRowMapper<>(entity, converter);
        RowMapperResultSetExtractor<T> rowMapperResultSetExtractor = new RowMapperResultSetExtractor<>(rowMapper);
        List<T> result = query(query, rowMapperResultSetExtractor);

        if (Objects.isNull(result)) {
            return Collections.emptyList();
        }

        return result;
    }

    @Nullable
    private List<T> query(SQLQuery<T> query,
                  RowMapperResultSetExtractor<T> rowMapperResultSetExtractor) {
        ResultSet resultSet = query.getResults();
        try {
            return rowMapperResultSetExtractor.extractData(resultSet);
        } catch (SQLException e) {
            throw translateException(e);
        } finally {
            JdbcUtils.closeResultSet(resultSet);
        }
    }

    private DataAccessException translateException(SQLException ex) {
        String task = "";
        String sql = null;
        DataAccessException dae = new SQLStateSQLExceptionTranslator().translate("", sql, ex);
        return (dae != null ? dae : new UncategorizedSQLException(task, sql, ex));
    }
}
