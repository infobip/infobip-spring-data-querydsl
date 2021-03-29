package com.infobip.spring.data.jdbc.annotation.processor;

import com.google.auto.service.AutoService;

import javax.annotation.processing.Processor;

@AutoService(Processor.class)
public class SpringDataJdbcAnnotationProcessor extends SpringDataJdbcAnnotationProcessorBase {

    public SpringDataJdbcAnnotationProcessor() {
        super(SpringDataJdbcQuerydslNamingStrategy.class);
    }
}
