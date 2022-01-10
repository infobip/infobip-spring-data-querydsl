package com.infobip.spring.data.common;

import javax.sql.DataSource;
import java.util.Optional;

import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.spring.SpringConnectionProvider;
import com.querydsl.sql.spring.SpringExceptionTranslator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.jdbc.support.SQLExceptionTranslator;

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
    public SpringExceptionTranslator springExceptionTranslator(Optional<SQLExceptionTranslator> sqlExceptionTranslator) {
        return sqlExceptionTranslator.map(SpringExceptionTranslator::new).orElseGet(SpringExceptionTranslator::new);
    }

    @ConditionalOnMissingBean
    @Bean
    public com.querydsl.sql.Configuration querydslSqlConfiguration(SQLTemplates sqlTemplates) {
        com.querydsl.sql.Configuration configuration = new com.querydsl.sql.Configuration(sqlTemplates);
        configuration.setExceptionTranslator(new SpringExceptionTranslator());
        return configuration;
    }

    @ConditionalOnMissingBean
    @Bean
    public SQLQueryFactory sqlQueryFactory(com.querydsl.sql.Configuration querydslSqlConfiguration,
                                           DataSource dataSource) {
        return new SQLQueryFactory(querydslSqlConfiguration, new SpringConnectionProvider(dataSource));
    }

}
