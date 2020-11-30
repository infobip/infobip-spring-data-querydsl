package com.infobip.spring.data.jdbc;

import com.querydsl.sql.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.relational.core.mapping.NamingStrategy;

import javax.sql.DataSource;
import java.sql.*;

@Configuration
public class QuerydslJdbcConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public NamingStrategy pascalCaseNamingStrategy() {
        return new PascalCaseNamingStrategy();
    }

    @ConditionalOnMissingBean
    @Bean
    public SQLTemplates sqlServer2012Templates(DataSource dataSource) throws SQLException {
        SQLTemplatesRegistry sqlTemplatesRegistry = new SQLTemplatesRegistry();
        DatabaseMetaData metaData;
        try (Connection connection = dataSource.getConnection()) {
            metaData = connection.getMetaData();
        }

        SQLTemplates templates = sqlTemplatesRegistry.getTemplates(metaData);

        if (templates instanceof SQLServerTemplates || metaData.getDatabaseMajorVersion() > 11) {
            return new SQLServer2012Templates();
        }

        return templates;
    }

    @ConditionalOnMissingBean
    @Bean
    public com.querydsl.sql.Configuration querydslSqlConfiguration(SQLTemplates sqlTemplates) {
        return new com.querydsl.sql.Configuration(sqlTemplates);
    }

    @ConditionalOnMissingBean
    @Bean
    public SQLQueryFactory sqlQueryFactory(com.querydsl.sql.Configuration querydslSqlConfiguration,
                                           DataSource dataSource) {
        return new SQLQueryFactory(querydslSqlConfiguration, dataSource);
    }
}