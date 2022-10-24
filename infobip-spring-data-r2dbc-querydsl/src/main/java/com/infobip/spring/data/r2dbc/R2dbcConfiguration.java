package com.infobip.spring.data.r2dbc;

import com.infobip.spring.data.common.PascalCaseNamingStrategy;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;
import jakarta.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.relational.core.mapping.NamingStrategy;

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
        return new com.querydsl.sql.Configuration(sqlTemplates);
    }

    @ConditionalOnMissingBean
    @Bean
    public SQLQueryFactory sqlQueryFactory(com.querydsl.sql.Configuration querydslSqlConfiguration) {
        DataSource dataSource = null;
        return new SQLQueryFactory(querydslSqlConfiguration, dataSource);
    }
}
