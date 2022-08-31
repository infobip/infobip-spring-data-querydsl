package com.infobip.spring.data.jdbc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.infobip.spring.data.common.QuerydslSqlQueryConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

/**
 * Annotation to enable {@link QuerydslJdbcRepository} support.
 *
 * @see EnableJdbcRepositories
 */
@Import({QuerydslSqlQueryConfiguration.class, AggregateReferenceTypeConfiguration.class})
@EnableJdbcRepositories(repositoryFactoryBeanClass = QuerydslJdbcRepositoryFactoryBean.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableQuerydslJdbcRepositories {
}
