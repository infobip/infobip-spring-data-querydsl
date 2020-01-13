package com.infobip.spring.data.jpa;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.lang.annotation.*;

/**
 * Annotation to enable {@link ExtendedQuerydslJpaRepository} support.
 *
 * @see EnableJpaRepositories
 */
@EnableJpaRepositories(repositoryBaseClass = SimpleExtendedQuerydslJpaRepository.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableExtendedJpaRepositories {
}
