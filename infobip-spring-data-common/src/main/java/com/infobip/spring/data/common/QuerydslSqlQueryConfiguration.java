package com.infobip.spring.data.common;

import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.spring.SpringConnectionProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.*;
import org.springframework.data.relational.core.mapping.NamingStrategy;

import javax.sql.DataSource;

@Import(InfobipSpringDataCommonConfiguration.class)
@Configuration
public class QuerydslSqlQueryConfiguration {

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
        return new SQLQueryFactory(querydslSqlConfiguration, new SpringConnectionProvider(dataSource));
    }
}
