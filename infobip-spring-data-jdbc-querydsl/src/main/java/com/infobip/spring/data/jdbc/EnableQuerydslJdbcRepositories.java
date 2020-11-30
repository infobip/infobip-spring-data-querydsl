package com.infobip.spring.data.jdbc;

import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

import java.lang.annotation.*;

/**
 * Annotation to enable {@link QuerydslJdbcRepository} support.
 *
 * @see EnableJdbcRepositories
 */
@Import(QuerydslJdbcConfiguration.class)
@EnableJdbcRepositories(repositoryFactoryBeanClass = QuerydslJdbcRepositoryFactoryBean.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableQuerydslJdbcRepositories {
}
