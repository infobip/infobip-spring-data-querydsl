package com.infobip.spring.data.jdbc.annotation.processor;

import com.google.common.base.CaseFormat;
import com.querydsl.apt.*;
import com.querydsl.codegen.QueryTypeFactory;
import com.querydsl.codegen.TypeMappings;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.util.Elements;

@FunctionalInterface
public interface TypeElementHandlerFactory {

    TypeElementHandler createElementHandler(Configuration configuration,
                                            ExtendedTypeFactory typeFactory,
                                            TypeMappings typeMappings,
                                            QueryTypeFactory queryTypeFactory,
                                            Elements elements,
                                            RoundEnvironment roundEnv,
                                            CaseFormat projectTableCaseFormat,
                                            CaseFormat projectColumnCaseFormat);
}
