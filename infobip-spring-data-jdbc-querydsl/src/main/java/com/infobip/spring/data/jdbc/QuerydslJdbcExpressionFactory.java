package com.infobip.spring.data.jdbc;

import java.lang.reflect.Parameter;

import com.infobip.spring.data.common.QuerydslExpressionFactory;
import com.querydsl.core.types.Expression;

public class QuerydslJdbcExpressionFactory extends QuerydslExpressionFactory {

    private static final String AGGREGATE_REFERENCE_TYPE_NAME =
            "org.springframework.data.jdbc.core.mapping.AggregateReference";

    public QuerydslJdbcExpressionFactory(Class<?> repositoryTargetType) {
        super(repositoryTargetType);
    }

    @Override
    protected Expression<?> toExpression(Parameter parameter, Expression<?> expression) {
        if (AGGREGATE_REFERENCE_TYPE_NAME.equals(parameter.getType().getName())
            && !parameter.getType().isAssignableFrom(expression.getType())) {
            return new AggregateReferenceExpression<>(expression, parameter.getType());
        }

        return expression;
    }
}
