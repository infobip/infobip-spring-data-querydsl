package com.infobip.spring.data.jpa.extension;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.lang.annotation.*;

@Import(ExtendedQuerydslJpaConfiguration.class)
@EnableJpaRepositories(repositoryFactoryBeanClass = CustomExtendedQuerydslJpaRepositoryFactoryBean.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableCustomExtendedJpaRepositories {
}
