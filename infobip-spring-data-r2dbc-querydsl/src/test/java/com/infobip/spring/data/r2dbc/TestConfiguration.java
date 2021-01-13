package com.infobip.spring.data.r2dbc;

import com.querydsl.sql.SQLServer2012Templates;
import lombok.AllArgsConstructor;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@AllArgsConstructor
@Configuration
public class TestConfiguration {

    private final Environment env;

    @Bean
    public SQLServer2012Templates sqlServer2012Templates() {
        return new SQLServer2012Templates();
    }

    @Bean(initMethod = "migrate")
    public Flyway flyway() {
        return new Flyway(Flyway.configure()
                                .baselineOnMigrate(true)
                                .dataSource(
                                        env.getRequiredProperty("spring.flyway.url"),
                                        env.getRequiredProperty("spring.flyway.username"),
                                        env.getRequiredProperty("spring.flyway.password"))
        );
    }
}
