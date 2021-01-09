package com.infobip.spring.data.jpa;

import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.lang.annotation.*;

/**
 * Annotation to enable {@link ExtendedQuerydslRepositoryJpa} support.
 *
 * @see EnableJpaRepositories
 */
@Import(ExtendedQuerydslJpaConfiguration.class)
@EnableJpaRepositories(repositoryFactoryBeanClass = ExtendedQuerydslJpaRepositoryFactoryBean.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableExtendedJpaRepositories {

}
