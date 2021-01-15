package com.infobip.spring.data.r2dbc;

import org.springframework.data.r2dbc.repository.config.R2dbcRepositoryConfigurationExtension;

public class QuerydslR2dbcRepositoryConfigurationExtension extends R2dbcRepositoryConfigurationExtension {

    @Override
    public String getRepositoryFactoryBeanClassName() {
        return QuerydslR2dbcRepositoryFactoryBean.class.getName();
    }
}
