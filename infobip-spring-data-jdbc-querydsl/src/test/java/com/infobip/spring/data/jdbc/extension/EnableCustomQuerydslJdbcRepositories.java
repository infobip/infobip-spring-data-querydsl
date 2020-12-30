package com.infobip.spring.data.jdbc.extension;

import com.infobip.spring.data.jdbc.QuerydslJdbcConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

import java.lang.annotation.*;

@Import(QuerydslJdbcConfiguration.class)
@EnableJdbcRepositories(repositoryFactoryBeanClass = CustomQuerydslJdbcRepositoryFactoryBean.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableCustomQuerydslJdbcRepositories {
}
