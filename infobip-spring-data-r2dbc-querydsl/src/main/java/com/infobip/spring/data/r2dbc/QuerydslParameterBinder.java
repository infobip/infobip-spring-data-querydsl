package com.infobip.spring.data.r2dbc;

import com.querydsl.sql.MySQLTemplates;
import com.querydsl.sql.SQLTemplates;
import io.r2dbc.spi.Parameters;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.binding.BindMarkersFactory;

import java.util.*;

class QuerydslParameterBinder {

    private final BindMarkersFactory bindMarkersFactory;
    private final boolean useNumberedBindParameters;

    QuerydslParameterBinder(BindMarkersFactory bindMarkersFactory, SQLTemplates sqlTemplates) {
        this.bindMarkersFactory = bindMarkersFactory;
        this.useNumberedBindParameters = resolve(sqlTemplates);
    }

    private boolean resolve(SQLTemplates sqlTemplates) {

        if(sqlTemplates instanceof MySQLTemplates) {
            return true;
        }

        return false;
    }

    DatabaseClient.GenericExecuteSpec bind(DatabaseClient databaseClient, List<Object> bindings, String sql) {

        if(useNumberedBindParameters) {
            var spec = databaseClient.sql(sql);
            var index = 0;
            for (Object binding : bindings) {
                spec = spec.bind(index++, binding);
            }
            return spec;
        }

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
