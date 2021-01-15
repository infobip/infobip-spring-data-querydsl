package com.infobip.spring.data.jdbc;

import org.springframework.data.jdbc.repository.config.JdbcRepositoryConfigExtension;

class QuerydslJdbcRepositoryConfigExtension extends JdbcRepositoryConfigExtension {

    @Override
    public String getRepositoryFactoryBeanClassName() {
        return QuerydslJdbcRepositoryFactoryBean.class.getName();
    }
}
