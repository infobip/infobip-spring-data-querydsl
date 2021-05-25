package com.infobip.spring.data.r2dbc;

import com.querydsl.sql.*;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.internal.jdbc.JdbcConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

@ConditionalOnClass(Flyway.class)
@Configuration
public class R2dbcSQLTemplatesConfiguration {

    @ConditionalOnBean(Flyway.class)
    @Bean
    public SQLTemplates sqlTemplates(Flyway flyway) throws SQLException {
        org.flywaydb.core.api.configuration.Configuration configuration = flyway.getConfiguration();
        JdbcConnectionFactory jdbcConnectionFactory = new JdbcConnectionFactory(configuration.getDataSource(),
                                                                                configuration.getConnectRetries(),
                                                                                null,
                                                                                null);
        SQLTemplatesRegistry sqlTemplatesRegistry = new SQLTemplatesRegistry();
        DatabaseMetaData metaData = jdbcConnectionFactory.openConnection().getMetaData();

        SQLTemplates templates = sqlTemplatesRegistry.getTemplates(metaData);

        if (templates instanceof SQLServerTemplates || metaData.getDatabaseMajorVersion() > 11) {
            return new SQLServer2012Templates();
        }

        return templates;
    }
}
