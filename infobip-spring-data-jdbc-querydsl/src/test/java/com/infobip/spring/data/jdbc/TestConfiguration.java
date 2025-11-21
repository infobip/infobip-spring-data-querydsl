package com.infobip.spring.data.jdbc;

import org.flywaydb.core.Flyway;
import org.springframework.boot.flyway.autoconfigure.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfiguration {

    @Bean
    public FlywayMigrationStrategy clean() {
        return flyway -> {
            // Rebuild Flyway with clean enabled for tests
            Flyway cleanFlyway = Flyway.configure()
                    .configuration(flyway.getConfiguration())
                    .cleanDisabled(false)
                    .load();

            cleanFlyway.clean();
            flyway.migrate();
        };
    }
}
