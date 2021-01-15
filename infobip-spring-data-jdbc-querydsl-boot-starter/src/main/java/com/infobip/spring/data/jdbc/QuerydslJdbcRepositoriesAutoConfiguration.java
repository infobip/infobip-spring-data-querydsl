package com.infobip.spring.data.jdbc;

import com.infobip.spring.data.common.QuerydslSqlQueryConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@AutoConfigureBefore(JdbcRepositoriesAutoConfiguration.class)
@Import({QuerydslSqlQueryConfiguration.class, QuerydslJdbcRepositoryConfigExtension.class, QuerydslJdbcRepositoriesRegistrar.class})
@Configuration
public class QuerydslJdbcRepositoriesAutoConfiguration {
}
