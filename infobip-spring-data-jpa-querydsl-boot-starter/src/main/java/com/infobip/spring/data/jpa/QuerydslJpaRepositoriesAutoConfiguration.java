package com.infobip.spring.data.jpa;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({ExtendedQuerydslJpaConfiguration.class, QuerydslJpaRepositoryConfigExtension.class, QuerydslJpaRepositoriesRegistrar.class})
@AutoConfigureBefore(DataJpaRepositoriesAutoConfiguration.class)
@Configuration
public class QuerydslJpaRepositoriesAutoConfiguration {
}
