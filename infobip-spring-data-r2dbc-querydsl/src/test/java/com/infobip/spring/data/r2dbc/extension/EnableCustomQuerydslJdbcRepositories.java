package com.infobip.spring.data.r2dbc.extension;

import com.infobip.spring.data.common.QuerydslSqlQueryConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import java.lang.annotation.*;

@Import(QuerydslSqlQueryConfiguration.class)
@EnableR2dbcRepositories(repositoryFactoryBeanClass = CustomQuerydslR2dbcRepositoryFactoryBean.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableCustomQuerydslJdbcRepositories {
}
