package com.infobip.spring.data;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.lang.annotation.*;

/**
 * Annotation to enable {@link ExtendedQueryDslJpaRepository} support.
 *
 * @see EnableJpaRepositories
 */
@EnableJpaRepositories(repositoryBaseClass = SimpleExtendedQueryDslJpaRepository.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableExtendedRepositories {
}
