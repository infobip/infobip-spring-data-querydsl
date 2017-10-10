package com.infobip.spring.data;

import com.infobip.flyway.SqlServerFlywayTestMigrationStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfiguration {

    @Autowired
    private DataSourceProperties dataSourceProperties;

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {

        return new SqlServerFlywayTestMigrationStrategy(dataSourceProperties);
    }
}
