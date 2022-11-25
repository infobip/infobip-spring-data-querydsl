package com.infobip.spring.data.jpa;

import java.lang.annotation.Annotation;
import java.util.Locale;

import org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.config.BootstrapMode;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.util.StringUtils;

class QuerydslJpaRepositoriesRegistrar extends AbstractRepositoryConfigurationSourceSupport {

    private BootstrapMode bootstrapMode = null;

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return EnableJpaRepositories.class;
    }

    @Override
    protected Class<?> getConfiguration() {
        return EnableJpaRepositoriesConfiguration.class;
    }

    @Override
    protected RepositoryConfigurationExtension getRepositoryConfigurationExtension() {
        return new QuerydslJpaRepositoryConfigExtension();
    }

    @Override
    protected BootstrapMode getBootstrapMode() {
        return (this.bootstrapMode == null) ? BootstrapMode.DEFAULT : this.bootstrapMode;
    }

    @Override
    public void setEnvironment(Environment environment) {
        super.setEnvironment(environment);
        configureBootstrapMode(environment);
    }

    private void configureBootstrapMode(Environment environment) {
        var property = environment.getProperty("spring.data.jpa.repositories.bootstrap-mode");
        if (StringUtils.hasText(property)) {
            this.bootstrapMode = BootstrapMode.valueOf(property.toUpperCase(Locale.ENGLISH));
        }
    }

    @EnableExtendedJpaRepositories
    private static class EnableJpaRepositoriesConfiguration {

    }

}
