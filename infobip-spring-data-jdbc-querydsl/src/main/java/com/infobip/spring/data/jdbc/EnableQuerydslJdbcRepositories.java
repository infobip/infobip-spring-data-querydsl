package com.infobip.spring.data.jdbc;

import com.infobip.spring.data.common.QuerydslSqlQueryConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

import java.lang.annotation.*;

/**
 * Annotation to enable {@link QuerydslJdbcRepository} support.
 *
 * @see EnableJdbcRepositories
 */
@Import(QuerydslSqlQueryConfiguration.class)
@EnableJdbcRepositories(repositoryFactoryBeanClass = QuerydslJdbcRepositoryFactoryBean.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableQuerydslJdbcRepositories {
}
