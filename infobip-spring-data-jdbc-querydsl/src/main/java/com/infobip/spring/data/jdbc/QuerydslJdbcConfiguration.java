package com.infobip.spring.data.jdbc;

import com.infobip.spring.data.common.InfobipSpringDataCommonConfiguration;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.*;
import org.springframework.data.relational.core.mapping.NamingStrategy;

import javax.sql.DataSource;

@Import(InfobipSpringDataCommonConfiguration.class)
@Configuration
public class QuerydslJdbcConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public NamingStrategy pascalCaseNamingStrategy() {
        return new PascalCaseNamingStrategy();
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
