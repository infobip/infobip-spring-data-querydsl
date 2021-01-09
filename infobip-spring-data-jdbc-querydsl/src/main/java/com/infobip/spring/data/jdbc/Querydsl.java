/*
 * Copyright 2012-2020 the original author or authors.
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
import com.querydsl.core.types.OrderSpecifier.NullHandling;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.querydsl.QSort;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @see org.springframework.data.jpa.repository.support.Querydsl
 */
class Querydsl {

	private final SQLQueryFactory sqlQueryFactory;
	private final PathBuilder<?> builder;

	Querydsl(SQLQueryFactory sqlQueryFactory, PathBuilder<?> builder) {
		this.sqlQueryFactory = sqlQueryFactory;
		this.builder = builder;
	}

	public SQLQuery<?> createQuery() {

		return sqlQueryFactory.query();
	}

	public SQLQuery<?> createQuery(EntityPath<?>... paths) {

		Assert.notNull(paths, "Paths must not be null!");

		return createQuery().from(paths);
	}

	public <T> SQLQuery<T> applyPagination(Pageable pageable, SQLQuery<T> query) {

		Assert.notNull(pageable, "Pageable must not be null!");
		Assert.notNull(query, "SQLQuery must not be null!");

		if (pageable.isUnpaged()) {
			return query;
		}

		query.offset(pageable.getOffset());
		query.limit(pageable.getPageSize());

		return applySorting(pageable.getSort(), query);
	}

	public <T> SQLQuery<T> applySorting(Sort sort, SQLQuery<T> query) {

		Assert.notNull(sort, "Sort must not be null!");
		Assert.notNull(query, "Query must not be null!");

		if (sort.isUnsorted()) {
			return query;
		}

		if (sort instanceof QSort) {
			return addOrderByFrom((QSort) sort, query);
		}

		return addOrderByFrom(sort, query);
	}

	private <T> SQLQuery<T> addOrderByFrom(QSort qsort, SQLQuery<T> query) {

		List<OrderSpecifier<?>> orderSpecifiers = qsort.getOrderSpecifiers();

		return query.orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]));
	}

	private <T> SQLQuery<T> addOrderByFrom(Sort sort, SQLQuery<T> query) {

		Assert.notNull(sort, "Sort must not be null!");
		Assert.notNull(query, "Query must not be null!");

		for (Order order : sort) {
			query.orderBy(toOrderSpecifier(order));
		}

		return query;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private OrderSpecifier<?> toOrderSpecifier(Order order) {

		return new OrderSpecifier(
				order.isAscending() ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC,
				buildOrderPropertyPathFrom(order), toQueryDslNullHandling(order.getNullHandling()));
	}

	private NullHandling toQueryDslNullHandling(Sort.NullHandling nullHandling) {

		Assert.notNull(nullHandling, "NullHandling must not be null!");

		switch (nullHandling) {

			case NULLS_FIRST:
				return NullHandling.NullsFirst;

			case NULLS_LAST:
				return NullHandling.NullsLast;

			case NATIVE:
			default:
				return NullHandling.Default;
		}
	}

	private Expression<?> buildOrderPropertyPathFrom(Order order) {

		Assert.notNull(order, "Order must not be null!");

		PropertyPath path = PropertyPath.from(order.getProperty(), builder.getType());
		Expression<?> sortPropertyExpression = builder;

		while (path != null) {

			sortPropertyExpression = !path.hasNext() && order.isIgnoreCase() && String.class.equals(path.getType()) //
					? Expressions.stringPath((Path<?>) sortPropertyExpression, path.getSegment()).lower() //
					: Expressions.path(path.getType(), (Path<?>) sortPropertyExpression, path.getSegment());

			path = path.next();
		}

		return sortPropertyExpression;
	}
}
