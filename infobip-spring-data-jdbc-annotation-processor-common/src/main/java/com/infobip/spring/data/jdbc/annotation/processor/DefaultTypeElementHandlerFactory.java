package com.infobip.spring.data.jdbc.annotation.processor;

import com.google.common.base.CaseFormat;
import com.querydsl.apt.*;
import com.querydsl.codegen.QueryTypeFactory;
import com.querydsl.codegen.TypeMappings;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.util.Elements;

public class DefaultTypeElementHandlerFactory implements TypeElementHandlerFactory {

    @Override
    public TypeElementHandler createElementHandler(Configuration configuration,
                                                   ExtendedTypeFactory typeFactory,
                                                   TypeMappings typeMappings,
                                                   QueryTypeFactory queryTypeFactory,
                                                   Elements elements,
                                                   RoundEnvironment roundEnv,
                                                   CaseFormat projectTableCaseFormat,
                                                   CaseFormat projectColumnCaseFormat) {
        return new DefaultElementHandler(configuration, typeFactory, typeMappings, queryTypeFactory, elements,
                                         roundEnv, projectTableCaseFormat, projectColumnCaseFormat);
    }
}
