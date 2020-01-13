package com.infobip.spring.data.jdbc;

import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

import java.lang.annotation.*;

/**
 * Annotation to enable {@link QuerydslJdbcRepository} support.
 *
 * @see EnableJdbcRepositories
 */
@EnableJdbcRepositories(repositoryFactoryBeanClass = QuerydslJdbcRepositoryFactoryBean.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableQuerydslJdbcRepositories {
}
