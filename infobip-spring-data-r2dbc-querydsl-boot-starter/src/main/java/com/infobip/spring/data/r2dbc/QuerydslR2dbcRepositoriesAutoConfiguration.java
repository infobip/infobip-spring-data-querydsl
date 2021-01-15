package com.infobip.spring.data.r2dbc;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.r2dbc.R2dbcRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({R2dbcConfiguration.class, QuerydslR2dbcRepositoriesAutoConfigureRegistrar.class})
@AutoConfigureBefore(R2dbcRepositoriesAutoConfiguration.class)
@Configuration
public class QuerydslR2dbcRepositoriesAutoConfiguration {
}
