package com.infobip.spring.data.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.CaseFormat;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.RelationalPathBase;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.mapping.PreferredConstructor;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.util.ReflectionUtils;

public class QuerydslExpressionFactory {

    private final Class<?> repositoryTargetType;

    public QuerydslExpressionFactory(Class<?> repositoryTargetType) {
        this.repositoryTargetType = repositoryTargetType;
    }

    public ConstructorExpression<?> getConstructorExpression(Class<?> type, RelationalPath<?> pathBase) {
        var constructor = getConstructor(type);

        if (constructor == null) {
            throw new IllegalArgumentException(
                "Could not discover preferred constructor for " + type);
        }

        Map<String, Expression<?>> columnNameToExpression = pathBase.getColumns()
                                                                    .stream()
                                                                    .collect(Collectors.toMap(
                                                                        column -> column.getMetadata().getName(),
                                                                        Function.identity()));
        var parameters = constructor.getParameters();

        var embeddedConstructorParameterNameToPath = getEmbeddedConstructorParameterNameToPath(
            type,
            pathBase,
            columnNameToExpression,
            parameters);

        var pairs = Stream.of(constructor.getParameters())
                          .map(parameter -> getExpression(type,
                                                                                       columnNameToExpression,
                                                                                       embeddedConstructorParameterNameToPath,
                                                                                       parameter))
                          .collect(Collectors.toList());

        Class<?>[] paramTypes = pairs.stream().map(ParameterAndExpressionPair::parameterType).toArray(Class[]::new);
        Expression<?>[] expressions = pairs.stream().map(ParameterAndExpressionPair::expression).toArray(Expression[]::new);
        ;
        return Projections.constructor(type, paramTypes, expressions);
    }

    private ParameterAndExpressionPair getExpression(Class<?> type,
                                                     Map<String, Expression<?>> columnNameToPath,
                                                     Map<String, Expression<?>> embeddedConstructorParameterNameToPath,
                                                     Parameter parameter) {
        var path = columnNameToPath.get(parameter.getName());

        if (Objects.isNull(path)) {
            return resolveNonColumnParameter(type, embeddedConstructorParameterNameToPath, parameter);
        }

        return new ParameterAndExpressionPair(parameter.getType(), path);
    }

    private ParameterAndExpressionPair resolveNonColumnParameter(Class<?> type,
                                                                 Map<String, Expression<?>> embeddedConstructorParameterNameToPath,
                                                                 Parameter parameter) {

        var name = parameter.getName();

        if (embeddedConstructorParameterNameToPath.containsKey(name)) {
            return new ParameterAndExpressionPair(parameter.getType(), embeddedConstructorParameterNameToPath.get(name));
        }

        var field = ReflectionUtils.findField(type, name);

        if (Objects.nonNull(field) && Objects.nonNull(AnnotationUtils.getAnnotation(field, MappedCollection.class))) {
            return resolveMappedCollectionParameter(parameter);
        }

        throw new IllegalArgumentException("Failed to match parameter " + name + " to QClass column for " + type);
    }

    private ParameterAndExpressionPair resolveMappedCollectionParameter(Parameter parameter) {
        var collectionType = parameter.getType();

        if (Set.class.isAssignableFrom(collectionType)) {
            var resolvableType = ResolvableType.forType(parameter.getParameterizedType()).as(Set.class).getGeneric(0);
            var target = Objects.requireNonNull(resolvableType.resolve());
            Expression<?> qClass = getRelationalPathBaseFromQueryClass(getQueryClass(target));
            return new ParameterAndExpressionPair(collectionType, new QSet(qClass));
        }

        throw new IllegalArgumentException("Unsupported collection type " + collectionType);
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

    private Constructor<?> getConstructor(Class<?> type) {
        PreferredConstructor<?, ?> preferredConstructor = PreferredConstructorDiscoverer.discover(type);

        if (preferredConstructor == null) {
            return null;
        }

        return preferredConstructor.getConstructor();
    }

    public RelationalPathBase<?> getRelationalPathBaseFromQueryRepositoryClass(Class<?> repositoryInterface) {

        var entityType = ResolvableType.forClass(repositoryInterface)
                                       .as(repositoryTargetType)
                                       .getGeneric(0)
                                       .resolve();
        if (entityType == null) {
            throw new IllegalArgumentException("Could not resolve query class for " + repositoryInterface);
        }

        return getRelationalPathBaseFromQueryClass(getQueryClass(entityType));
    }

    private Class<?> getQueryClass(Class<?> entityType) {
        var fullName = entityType.getPackage().getName() + ".Q" + entityType.getSimpleName();
        try {
            return entityType.getClassLoader().loadClass(fullName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Unable to load class " + fullName);
        }
    }

    private RelationalPathBase<?> getRelationalPathBaseFromQueryClass(Class<?> queryClass) {
        var fieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, queryClass.getSimpleName().substring(1));
        var field = ReflectionUtils.findField(queryClass, fieldName);

        if (field == null) {
            throw new IllegalArgumentException("Did not find a static field of the same type in " + queryClass);
        }

        return (RelationalPathBase<?>) ReflectionUtils.getField(field, null);
    }

}
