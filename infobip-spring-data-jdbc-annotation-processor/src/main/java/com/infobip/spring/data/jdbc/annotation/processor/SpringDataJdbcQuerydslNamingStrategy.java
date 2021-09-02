package com.infobip.spring.data.jdbc.annotation.processor;

import com.google.common.base.CaseFormat;
import com.querydsl.codegen.EntityType;
import com.querydsl.sql.SchemaAndTable;
import com.querydsl.sql.codegen.NamingStrategy;
import com.querydsl.sql.codegen.support.ForeignKeyData;

public class SpringDataJdbcQuerydslNamingStrategy implements NamingStrategy {

    @Override
    public String appendSchema(String packageName, String schema) {
        return null;
    }

    @Override
    public String getClassName(String tableName) {
        return null;
    }

    @Override
    public String getClassName(SchemaAndTable schemaAndTable) {
        return null;
    }

    @Override
    public String getDefaultAlias(EntityType entityType) {
        return entityType.getSimpleName();
    }

    @Override
    public String getDefaultVariableName(EntityType entityType) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, getDefaultAlias(entityType));
    }

    @Override
    public String getForeignKeysClassName() {
        return null;
    }

    @Override
    public String getForeignKeysVariable(EntityType entityType) {
        return null;
    }

    @Override
    public String getPrimaryKeysClassName() {
        return null;
    }

    @Override
    public String getPrimaryKeysVariable(EntityType entityType) {
        return null;
    }

    @Override
    public String getPropertyName(String columnName, EntityType entityType) {
        return null;
    }

    @Override
    public String getPropertyNameForForeignKey(String foreignKeyName, EntityType entityType) {
        return null;
    }

    @Override
    public String getPropertyNameForInverseForeignKey(String name, EntityType model) {
        return null;
    }

    @Override
    public String getPropertyNameForPrimaryKey(String name, EntityType model) {
        return null;
    }

    @Override
    public String normalizeColumnName(String columnName) {
        return null;
    }

    @Override
    public String normalizeTableName(String tableName) {
        return null;
    }

    @Override
    public String normalizeSchemaName(String schemaName) {
        return null;
    }

    @Override
    public boolean shouldGenerateClass(SchemaAndTable schemaAndTable) {
        return false;
    }

    @Override
    public boolean shouldGenerateForeignKey(SchemaAndTable schemaAndTable, ForeignKeyData foreignKeyData) {
        return false;
    }

    @Override
    public String getPackage(String basePackage, SchemaAndTable schemaAndTable) {
        return null;
    }
}
