package com.infobip.spring.data.jpa;

import org.springframework.data.jpa.repository.config.JpaRepositoryConfigExtension;

public class QuerydslJpaRepositoryConfigExtension extends JpaRepositoryConfigExtension {

    @Override
    public String getRepositoryFactoryBeanClassName() {
        return ExtendedQuerydslJpaRepositoryFactoryBean.class.getName();
    }
}
