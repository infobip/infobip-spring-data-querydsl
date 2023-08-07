package com.infobip.spring.data.r2dbc;

import io.r2dbc.spi.Parameters;
import io.r2dbc.spi.R2dbcType;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.binding.BindMarkersFactory;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class QuerydslParameterBinder {

    private final BindMarkersFactory bindMarkersFactory;
    private final Map<Class<?>, R2dbcType> classToR2dbcType;

    QuerydslParameterBinder(BindMarkersFactory bindMarkersFactory) {
        this.bindMarkersFactory = bindMarkersFactory;
        var duplicateR2dbcTypes = Set.of(String.class, ByteBuffer.class, BigDecimal.class, Double.class);
        this.classToR2dbcType = Stream.of(R2dbcType.values())
                                      .filter(r2dbcType -> !duplicateR2dbcTypes.contains(r2dbcType.getJavaType()))
                                      .collect(Collectors.toMap(R2dbcType::getJavaType, Function.identity()));
        this.classToR2dbcType.put(String.class, R2dbcType.NVARCHAR);
        this.classToR2dbcType.put(ByteBuffer.class, R2dbcType.VARBINARY);
        this.classToR2dbcType.put(BigDecimal.class, R2dbcType.DECIMAL);
        this.classToR2dbcType.put(Double.class, R2dbcType.DOUBLE);
    }

    DatabaseClient.GenericExecuteSpec bind(DatabaseClient databaseClient, List<Object> bindings, String sql) {
        var parameterNameToParameterValue = parameterNameToParameterValue(bindings);
        var sqlWithParameterNames = getSqlWithParameterNames(parameterNameToParameterValue, sql);
        var spec = databaseClient.sql(sqlWithParameterNames);
        for (Map.Entry<String, Object> entry : parameterNameToParameterValue.entrySet()) {
            spec = spec.bind(entry.getKey(),
                             Parameters.in(classToR2dbcType.get(entry.getValue().getClass()), entry.getValue()));
        }
        return spec;
    }

    private LinkedHashMap<String, Object> parameterNameToParameterValue(List<Object> bindings) {
        var bindMarkers = bindMarkersFactory.create();
        var parameterNameToParameterValue = new LinkedHashMap<String, Object>();
        for (int i = 0; i < bindings.size(); i++) {
            var marker = bindMarkers.next(String.valueOf(i));
            parameterNameToParameterValue.put(marker.getPlaceholder(), bindings.get(i));
        }
        return parameterNameToParameterValue;
    }

    private String getSqlWithParameterNames(LinkedHashMap<String, Object> parameterNameToParameterValue, String sql) {
        var sqlWithParameterNames = sql;

        for (String parameterName : parameterNameToParameterValue.keySet()) {
            sqlWithParameterNames = sqlWithParameterNames.replaceFirst("\\?", parameterName);
        }
        return sqlWithParameterNames;
    }
}
