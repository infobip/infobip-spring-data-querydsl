package com.infobip.spring.data.jpa;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.querydsl.core.types.Path;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;

public class StoredProcedureQueryBuilder {

    private final String name;
    private final List<Parameter> inParameters;
    private final EntityManager entityManager;
    private Class<?>[] resultClasses;

    StoredProcedureQueryBuilder(String name,
                                EntityManager entityManager) {
        this.name = name;
        this.entityManager = entityManager;
        this.inParameters = new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getResultList() {
        return build().getResultList();
    }

    public StoredProcedureQuery build() {
        var storedProcedureQuery = createStoredProcedureQuery();
        inParameters.forEach(parameter -> {
            storedProcedureQuery.registerStoredProcedureParameter(parameter.name, parameter.type, ParameterMode.IN);
            storedProcedureQuery.setParameter(parameter.name, parameter.value);
        });
        return storedProcedureQuery;
    }

    private StoredProcedureQuery createStoredProcedureQuery() {

        if(resultClasses == null) {
            return entityManager.createStoredProcedureQuery(name);
        }

        return entityManager.createStoredProcedureQuery(name, resultClasses);
    }

    public <T> StoredProcedureQueryBuilder addInParameter(Path<T> parameter, T value) {
        Class<?> type = parameter.getType();
        inParameters.add(new Parameter(type, parameter.getMetadata().getName(), value));
        return this;
    }

    public StoredProcedureQueryBuilder addInParameter(String name, Object value) {
        inParameters.add(new Parameter(value.getClass(), name, value));
        return this;
    }

    public StoredProcedureQueryBuilder setResultClasses(Class<?>... resultClasses) {
        this.resultClasses = resultClasses;
        return this;
    }

    private record Parameter(
        Class<?> type,
        String name,
        Object value
    ) {

        @Override
            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || getClass() != o.getClass()) {
                    return false;
                }
                var parameter = (Parameter) o;
                return Objects.equals(type, parameter.type) &&
                    Objects.equals(name, parameter.name) &&
                    Objects.equals(value, parameter.value);
            }

        @Override
            public String toString() {
                return "Parameter{" +
                    "type=" + type +
                    ", name='" + name + '\'' +
                    ", value=" + value +
                    '}';
            }

    }
}
