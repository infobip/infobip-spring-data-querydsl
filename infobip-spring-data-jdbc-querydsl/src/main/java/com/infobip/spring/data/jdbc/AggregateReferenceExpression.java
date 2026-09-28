package com.infobip.spring.data.jdbc;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionBase;
import com.querydsl.core.types.Visitor;

/**
 * Adapts an identifier {@code expression} (such as a {@link com.querydsl.core.types.dsl.StringPath} or a
 * {@link com.querydsl.core.types.dsl.NumberPath}) to the type of an {@code AggregateReference} constructor parameter.
 * <p>
 * Serialization is delegated to the wrapped expression, so the identifier column is still selected, while the expression
 * type is the constructor parameter type which enables querydsl to convert the read value using the registered
 * {@link org.springframework.data.jdbc.core.mapping.AggregateReference} {@code Type}.
 */
class AggregateReferenceExpression<T> extends ExpressionBase<T> {

    private final Expression<?> expression;

    @SuppressWarnings("unchecked")
    AggregateReferenceExpression(Expression<?> expression, Class<?> type) {
        super((Class<? extends T>) type);
        this.expression = expression;
    }

    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
        return expression.accept(visitor, context);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof AggregateReferenceExpression<?> other)) {
            return false;
        }

        return expression.equals(other.expression) && getType().equals(other.getType());
    }
}
