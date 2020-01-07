package com.infobip.spring.data.jdbc;

import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.stereotype.Component;

@Component
public class CustomNamingStrategy implements NamingStrategy {

    @Override
    public String getTableName(Class<?> type) {
        return type.getSimpleName();
    }

    @Override
    public String getColumnName(RelationalPersistentProperty property) {
        return property.getName().substring(0, 1).toUpperCase() + property.getName().substring(1);
    }
}