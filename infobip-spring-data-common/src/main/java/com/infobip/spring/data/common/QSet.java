/*
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.infobip.spring.data.common;

import java.util.*;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.FactoryExpressionBase;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.util.CollectionUtils;
import org.jetbrains.annotations.Nullable;

public class QSet extends FactoryExpressionBase<Set<?>> {

    private final List<Expression<?>> args;

    @SuppressWarnings("unchecked")
    protected QSet(Expression<?>... args) {
        super((Class) Set.class);
        this.args = CollectionUtils.unmodifiableList(Arrays.asList(args));
    }

    @SuppressWarnings("unchecked")
    protected QSet(List<Expression<?>> args) {
        super((Class) Set.class);
        this.args = CollectionUtils.unmodifiableList(args);
    }

    @SuppressWarnings("unchecked")
    protected QSet(Expression<?>[]... args) {
        super((Class) Set.class);
        List<Expression<?>> builder = new ArrayList<>();
        for (Expression<?>[] exprs: args) {
            Collections.addAll(builder, exprs);
        }
        this.args = CollectionUtils.unmodifiableList(builder);
    }

    @Override
    @Nullable
    public <R, C> R accept(Visitor<R, C> v, C context) {
        return v.visit(this, context);
    }

    @Override
    public List<Expression<?>> getArgs() {
        return args;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof FactoryExpression) {
            FactoryExpression<?> c = (FactoryExpression<?>) obj;
            return args.equals(c.getArgs()) && getType().equals(c.getType());
        } else {
            return false;
        }
    }

    @Override
    @Nullable
    public Set<?> newInstance(Object... args) {
        return Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(args)));
    }

}
