package com.infobip.spring.data.r2dbc;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.data.r2dbc.autoconfigure.DataR2dbcAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({R2dbcConfiguration.class, QuerydslR2dbcRepositoriesAutoConfigureRegistrar.class})
@AutoConfigureBefore(DataR2dbcAutoConfiguration.class)
@Configuration
public class QuerydslR2dbcRepositoriesAutoConfiguration {
}
