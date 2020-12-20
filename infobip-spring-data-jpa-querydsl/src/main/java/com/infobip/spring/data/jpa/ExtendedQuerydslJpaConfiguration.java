package com.infobip.spring.data.jpa;

import com.querydsl.jpa.sql.JPASQLQuery;
import com.querydsl.sql.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.sql.*;
import java.util.function.Supplier;

@Configuration
public class ExtendedQuerydslJpaConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public SQLTemplates sqlTemplates(DataSource dataSource) throws SQLException {
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
    public Supplier<JPASQLQuery<?>> jpaSqlFactory(EntityManager entityManager, SQLTemplates sqlTemplates) {
        return () -> new JPASQLQuery<>(entityManager, sqlTemplates);
    }
}
