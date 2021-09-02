package com.infobip.spring.data.jdbc.annotation.processor;

import com.querydsl.apt.ExtendedTypeFactory;
import com.querydsl.codegen.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.function.Function;

class CustomExtendedTypeFactory extends ExtendedTypeFactory {

    public CustomExtendedTypeFactory(
            ProcessingEnvironment env,
            Set<Class<? extends Annotation>> annotations,
            TypeMappings typeMappings,
            QueryTypeFactory queryTypeFactory,
            Function<EntityType, String> variableNameFunction) {
        super(env, annotations, typeMappings, queryTypeFactory, variableNameFunction);
    }

    @Override
    public boolean isSimpleTypeEntity(TypeElement typeElement, Class<? extends Annotation> entityAnn) {
        return typeElement.getAnnotation(entityAnn) != null
               || typeElement.getEnclosedElements()
                             .stream()
                             .anyMatch(element -> element.getAnnotation(entityAnn) != null);
    }
}
