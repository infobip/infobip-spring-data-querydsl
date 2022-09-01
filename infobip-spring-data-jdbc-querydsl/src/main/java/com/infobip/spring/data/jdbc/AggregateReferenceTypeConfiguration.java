package com.infobip.spring.data.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AggregateReferenceTypeConfiguration {

    @Autowired
    public void registerAggregateReferenceType(com.querydsl.sql.Configuration configuration) {
        configuration.register(new AggregateReferenceType());
    }

}
