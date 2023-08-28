package com.infobip.spring.data.r2dbc;

import io.r2dbc.spi.Parameters;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.binding.BindMarkersFactory;

import java.util.*;

class QuerydslParameterBinder {

    private final BindMarkersFactory bindMarkersFactory;

    QuerydslParameterBinder(BindMarkersFactory bindMarkersFactory) {
        this.bindMarkersFactory = bindMarkersFactory;
    }

    DatabaseClient.GenericExecuteSpec bind(DatabaseClient databaseClient, List<Object> bindings, String sql) {
        var parameterNameToParameterValue = parameterNameToParameterValue(bindings);
        var sqlWithParameterNames = getSqlWithParameterNames(parameterNameToParameterValue, sql);
        var spec = databaseClient.sql(sqlWithParameterNames);
        for (Map.Entry<String, Object> entry : parameterNameToParameterValue.entrySet()) {
            spec = spec.bind(entry.getKey(), Parameters.in(entry.getValue()));
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
            String paramName = escape(parameterName);
            sqlWithParameterNames = sqlWithParameterNames.replaceFirst("\\?", paramName);
        }
        return sqlWithParameterNames;
    }

    private String escape(String parameterName) {
        if (parameterName.startsWith("$")) {
            return parameterName.replace("$", "\\$");
        }
        return parameterName;
    }
}
