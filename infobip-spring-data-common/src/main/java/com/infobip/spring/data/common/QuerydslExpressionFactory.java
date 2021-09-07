package com.infobip.spring.data.common;

import com.google.common.base.CaseFormat;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.RelationalPathBase;
import org.springframework.core.ResolvableType;
import org.springframework.data.mapping.PreferredConstructor;
import org.springframework.data.mapping.model.PreferredConstructorDiscoverer;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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

        Map<String, Expression<?>> columnNameToExpression = pathBase.getColumns()
                                                                    .stream()
                                                                    .collect(Collectors.toMap(
                                                                            column -> column.getMetadata().getName(),
                                                                            Function.identity()));
        Parameter[] parameters = constructor.getParameters();

        Map<String, Expression<?>> embeddedConstructorParameterNameToPath = getEmbeddedConstructorParameterNameToPath(
                type,
                pathBase,
                columnNameToExpression,
                parameters);

        Expression<?>[] expressions = Stream.of(constructor.getParameters())
                                            .map(Parameter::getName)
                                            .map(name -> getExpression(columnNameToExpression,
                                                                       embeddedConstructorParameterNameToPath, name))
                                            .toArray(Expression[]::new);

        return Projections.constructor(type, expressions);
    }

    private Expression<?> getExpression(Map<String, Expression<?>> columnNameToPath,
                                        Map<String, Expression<?>> embeddedConstructorParameterNameToPath,
                                        String name) {
        Expression<?> path = columnNameToPath.get(name);

        if (Objects.isNull(path)) {
            return embeddedConstructorParameterNameToPath.get(name);
        }

        return path;
    }

    private Map<String, Expression<?>> getEmbeddedConstructorParameterNameToPath(Class<?> type,
                                                                                 RelationalPath<?> pathBase,
                                                                                 Map<String, Expression<?>> columnNameToColumn,
                                                                                 Parameter[] parameters) {
        Map<String, Expression<?>> embeddedConstructorParameterNameToPath = new HashMap<>();
        Stream.of(parameters)
              .filter(parameter -> !columnNameToColumn.containsKey(parameter.getName()))
              .forEach(parameter -> {
                  getEmbeddedType(type, parameter).map(
                                                          embeddedType -> getConstructorExpression(embeddedType, pathBase))
                                                  .ifPresent(path -> embeddedConstructorParameterNameToPath.put(
                                                          parameter.getName(), path));
              });
        return embeddedConstructorParameterNameToPath;
    }

    private Optional<Class<?>> getEmbeddedType(Class<?> type, Parameter parameter) {
        return Stream.of(type.getDeclaredFields())
                     .filter(field -> field.getName().equals(parameter.getName()))
                     .filter(field -> field.isAnnotationPresent(Embedded.class))
                     .<Class<?>>map(Field::getType)
                     .findAny();
    }

    @Nullable
    private Constructor<?> getConstructor(Class<?> type) {
        PreferredConstructor<?, ?> preferredConstructor = PreferredConstructorDiscoverer.discover(type);

        if (preferredConstructor == null) {
            return null;
        }

        return preferredConstructor.getConstructor();
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
