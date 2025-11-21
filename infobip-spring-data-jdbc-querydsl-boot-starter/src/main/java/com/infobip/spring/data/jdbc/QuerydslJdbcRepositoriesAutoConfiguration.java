package com.infobip.spring.data.jdbc;

import com.infobip.spring.data.common.QuerydslSqlQueryConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.data.jdbc.autoconfigure.DataJdbcRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@AutoConfigureBefore(DataJdbcRepositoriesAutoConfiguration.class)
@Import({QuerydslSqlQueryConfiguration.class, QuerydslJdbcRepositoryConfigExtension.class, QuerydslJdbcRepositoriesRegistrar.class})
@Configuration
public class QuerydslJdbcRepositoriesAutoConfiguration {
}
