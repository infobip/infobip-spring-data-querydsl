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
package com.infobip.spring.data.common;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QSort;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * // @see org.springframework.data.jpa.repository.support.Querydsl
 */
public class Querydsl {

	private final SQLQueryFactory sqlQueryFactory;
	private final RelationalPathBase<?> pathBase;

	public Querydsl(SQLQueryFactory sqlQueryFactory, RelationalPathBase<?> pathBase) {
		this.sqlQueryFactory = sqlQueryFactory;
		this.pathBase = pathBase;
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

		return addOrderByFrom(normalizeSort(sort), query);
	}

	private <T> SQLQuery<T> addOrderByFrom(QSort qsort, SQLQuery<T> query) {

		List<OrderSpecifier<?>> orderSpecifiers = qsort.getOrderSpecifiers();

		return query.orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]));
	}

	private QSort normalizeSort(Sort sort) {
		if (sort instanceof QSort) {
			return (QSort) sort;
		}

		Map<String, Path<?>> pathByName = pathBase.getColumns()
				.stream()
				.collect(Collectors.toMap(it -> it.getMetadata().getName(), it -> it));

		List<OrderSpecifier<?>> orderSpecifiers = sort.stream()
				.map(order -> {
					Path<?> path = pathByName.get(order.getProperty());

					if (path != null) {
						ComparableExpression<?> comparablePath = (ComparableExpression<?>) path;
						return order.isAscending() ? comparablePath.asc() : comparablePath.desc();
					}

					return null;
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toList());

		return new QSort(orderSpecifiers);
	}
}
