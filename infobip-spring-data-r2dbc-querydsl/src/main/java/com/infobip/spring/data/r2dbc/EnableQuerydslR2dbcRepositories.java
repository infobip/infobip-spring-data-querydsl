package com.infobip.spring.data.r2dbc;

import com.infobip.spring.data.common.QuerydslSqlQueryConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import java.lang.annotation.*;

/**
 * Annotation to enable {@link QuerydslR2dbcRepository} support.
 *
 * @see EnableR2dbcRepositories
 */
@Import(QuerydslSqlQueryConfiguration.class)
@EnableR2dbcRepositories(repositoryFactoryBeanClass = QuerydslR2dbcRepositoryFactoryBean.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableQuerydslR2dbcRepositories {
}
