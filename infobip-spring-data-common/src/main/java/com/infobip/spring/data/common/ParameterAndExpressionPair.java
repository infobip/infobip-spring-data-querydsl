package com.infobip.spring.data.common;

import com.querydsl.core.types.Expression;

public record ParameterAndExpressionPair(Class<?> parameterType, Expression<?> expression) {
}
