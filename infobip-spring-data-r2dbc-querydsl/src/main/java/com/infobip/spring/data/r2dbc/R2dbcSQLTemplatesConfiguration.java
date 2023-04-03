package com.infobip.spring.data.r2dbc;

import java.sql.SQLException;

import com.querydsl.sql.SQLServer2012Templates;
import com.querydsl.sql.SQLServerTemplates;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.SQLTemplatesRegistry;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.internal.jdbc.JdbcConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnClass(Flyway.class)
@Configuration
public class R2dbcSQLTemplatesConfiguration {

    @ConditionalOnBean(Flyway.class)
    @Bean
    public SQLTemplates sqlTemplates(Flyway flyway) throws SQLException {
        var jdbcConnectionFactory = new JdbcConnectionFactory(flyway.getConfiguration().getDataSource(),
                                                              flyway.getConfiguration(),
                                                              null);
        var sqlTemplatesRegistry = new SQLTemplatesRegistry();
        var metaData = jdbcConnectionFactory.openConnection().getMetaData();

        var templates = sqlTemplatesRegistry.getTemplates(metaData);

        if (templates instanceof SQLServerTemplates && metaData.getDatabaseMajorVersion() > 11) {
            return new SQLServer2012Templates();
        }

        return templates;
    }

}
