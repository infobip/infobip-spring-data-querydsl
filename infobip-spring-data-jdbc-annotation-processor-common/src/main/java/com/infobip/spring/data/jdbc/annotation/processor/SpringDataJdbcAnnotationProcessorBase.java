package com.infobip.spring.data.jdbc.annotation.processor;

import com.google.common.collect.ImmutableSet;
import com.querydsl.apt.*;
import com.querydsl.codegen.*;
import com.querydsl.sql.codegen.NamingStrategy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class SpringDataJdbcAnnotationProcessorBase extends AbstractQuerydslProcessor {

    private RoundEnvironment roundEnv;
    private CustomExtendedTypeFactory typeFactory;
    private Configuration conf;
    private final Class<? extends NamingStrategy> namingStrategyClass;

    public SpringDataJdbcAnnotationProcessorBase(Class<? extends NamingStrategy> namingStrategyClass) {
        this.namingStrategyClass = namingStrategyClass;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(Id.class.getName());
    }

    @Override
    protected Configuration createConfiguration(RoundEnvironment roundEnv) {
        Class<? extends Annotation> entity = Id.class;
        this.roundEnv = roundEnv;
        CodegenModule codegenModule = new CodegenModule();
        JavaTypeMappings typeMappings = new JavaTypeMappings();
        codegenModule.bind(TypeMappings.class, typeMappings);
        codegenModule.bind(QueryTypeFactory.class, new QueryTypeFactoryImpl("", "", ""));
        SpringDataJdbcConfiguration springDataJdbcConfiguration = new SpringDataJdbcConfiguration(roundEnv,
                                                                                                  processingEnv,
                                                                                                  entity, null, null,
                                                                                                  null, Transient.class,
                                                                                                  typeMappings,
                                                                                                  codegenModule,
                                                                                                  namingStrategyClass);
        this.conf = springDataJdbcConfiguration;
        return springDataJdbcConfiguration;
    }

    @Override
    protected TypeElementHandler createElementHandler(TypeMappings typeMappings, QueryTypeFactory queryTypeFactory) {
        return new CustomElementHandler(conf, typeFactory, typeMappings, queryTypeFactory, processingEnv.getElementUtils(), roundEnv);
    }

    @Override
    protected CustomExtendedTypeFactory createTypeFactory(Set<Class<? extends Annotation>> entityAnnotations,
                                                          TypeMappings typeMappings,
                                                          QueryTypeFactory queryTypeFactory) {
        CustomExtendedTypeFactory customExtendedTypeFactory = new CustomExtendedTypeFactory(processingEnv,
                                                                                            entityAnnotations,
                                                                                            typeMappings,
                                                                                            queryTypeFactory,
                                                                                            conf.getVariableNameFunction());
        this.typeFactory = customExtendedTypeFactory;
        return customExtendedTypeFactory;
    }

    protected Set<TypeElement> collectElements() {
        return roundEnv.getElementsAnnotatedWith(conf.getEntityAnnotation())
                       .stream()
                       .map(Element::getEnclosingElement)
                       .filter(element -> element instanceof TypeElement)
                       .map(element -> (TypeElement) element)
                       .collect(Collectors.toSet());
    }

    @Override
    protected String getClassName(EntityType model) {
        return model.getFullName();
    }
}
