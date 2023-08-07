package com.infobip.spring.data.r2dbc;

import com.infobip.spring.data.common.PascalCaseNamingStrategy;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.*;
import org.springframework.data.relational.core.mapping.NamingStrategy;

import javax.sql.DataSource;

@Import(R2dbcSQLTemplatesConfiguration.class)
@Configuration
public class R2dbcConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public NamingStrategy pascalCaseNamingStrategy() {
        return new PascalCaseNamingStrategy();
    }

    @ConditionalOnMissingBean
    @Bean
    public com.querydsl.sql.Configuration querydslSqlConfiguration(SQLTemplates sqlTemplates) {
        var configuration = new com.querydsl.sql.Configuration(sqlTemplates);
        return configuration;
    }

    @ConditionalOnMissingBean
    @Bean
    public SQLQueryFactory sqlQueryFactory(com.querydsl.sql.Configuration querydslSqlConfiguration) {
        DataSource dataSource = null;
        return new SQLQueryFactory(querydslSqlConfiguration, dataSource);
    }
}
