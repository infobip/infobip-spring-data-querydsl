package com.infobip.spring.data.common;

import com.querydsl.core.types.Expression;

public class ParameterAndExpressionPair {
    final Class<?> parameterType;
    final Expression<?> expression;

    public ParameterAndExpressionPair(Class<?> parameterType, Expression<?> expression) {
        this.parameterType = parameterType;
        this.expression = expression;
    }

    public Class<?> getParameterType() {
        return parameterType;
    }

    public Expression<?> getExpression() {
        return expression;
    }

}
