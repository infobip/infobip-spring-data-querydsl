package com.infobip.spring.data.jpa;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({ExtendedQuerydslJpaConfiguration.class, QuerydslJpaRepositoryConfigExtension.class, QuerydslJpaRepositoriesRegistrar.class})
@AutoConfigureBefore(JpaRepositoriesAutoConfiguration.class)
@Configuration
public class QuerydslJpaRepositoriesAutoConfiguration {
}
