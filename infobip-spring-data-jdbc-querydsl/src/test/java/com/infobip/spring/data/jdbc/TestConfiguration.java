package com.infobip.spring.data.jdbc;

import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLServer2012Templates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class TestConfiguration {

    @Bean
    public SQLQueryFactory sqlQueryFactory(DataSource dataSource) {
        com.querydsl.sql.Configuration  configuration = new com.querydsl.sql.Configuration(new SQLServer2012Templates());
        return new SQLQueryFactory(configuration, dataSource);
    }
}