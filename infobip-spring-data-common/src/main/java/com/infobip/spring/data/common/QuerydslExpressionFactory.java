package com.infobip.spring.data.common;

import com.google.common.base.CaseFormat;
import com.querydsl.core.types.*;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.RelationalPathBase;
import org.springframework.core.ResolvableType;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QuerydslExpressionFactory {

    private final Class<?> repositoryTargetType;

    public QuerydslExpressionFactory(Class<?> repositoryTargetType) {
        this.repositoryTargetType = repositoryTargetType;
    }

    public ConstructorExpression<?> getConstructorExpression(Class<?> type, RelationalPath<?> pathBase) {
        Constructor<?> constructor = getConstructor(type);

        if (constructor == null) {
            throw new IllegalArgumentException(
                    "Could not discover preferred constructor for " + type);
        }

        Map<String, Path<?>> columnNameToColumn = pathBase.getColumns()
                                                          .stream()
                                                          .collect(Collectors.toMap(
                                                                  column -> column.getMetadata().getName(),
                                                                  Function.identity()));

        Path<?>[] paths = Stream.of(constructor.getParameters())
                                .map(Parameter::getName)
                                .map(columnNameToColumn::get)
                                .toArray(Path[]::new);

        return Projections.constructor(type, paths);
    }

    private Constructor<?> getConstructor(Class<?> type) {
        Constructor<?>[] declaredConstructors = type.getDeclaredConstructors();
        Constructor<?> persistenceConstructor = Arrays.stream(declaredConstructors)
                                                      .filter(constructor -> constructor.isAnnotationPresent(
                                                              PersistenceConstructor.class))
                                                      .findAny()
                                                      .orElse(null);

        if (Objects.nonNull(persistenceConstructor)) {
            return persistenceConstructor;
        }

        return Arrays.stream(declaredConstructors)
                     .max(Comparator.comparingInt(Constructor::getParameterCount))
                     .orElse(null);
    }

    public RelationalPathBase<?> getRelationalPathBaseFromQueryRepositoryClass(Class<?> repositoryInterface) {

        Class<?> entityType = ResolvableType.forClass(repositoryInterface)
                                            .as(repositoryTargetType)
                                            .getGeneric(0)
                                            .resolve();
        if (entityType == null) {
            throw new IllegalArgumentException("Could not resolve query class for " + repositoryInterface);
        }

        return getRelationalPathBaseFromQueryClass(getQueryClass(entityType));
    }

    private Class<?> getQueryClass(Class<?> entityType) {
        String fullName = entityType.getPackage().getName() + ".Q" + entityType.getSimpleName();
        try {
            return entityType.getClassLoader().loadClass(fullName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Unable to load class " + fullName);
        }
    }

    private RelationalPathBase<?> getRelationalPathBaseFromQueryClass(Class<?> queryClass) {
        String fieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, queryClass.getSimpleName().substring(1));
        Field field = ReflectionUtils.findField(queryClass, fieldName);

        if (field == null) {
            throw new IllegalArgumentException("Did not find a static field of the same type in " + queryClass);
        }

        return (RelationalPathBase<?>) ReflectionUtils.getField(field, null);
    }
}
