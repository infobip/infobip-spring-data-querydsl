package com.infobip.spring.data.jdbc.annotation.processor;

import com.google.auto.service.AutoService;
import com.querydsl.apt.*;
import com.querydsl.codegen.*;
import org.springframework.data.annotation.Id;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.*;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("org.springframework.data.annotation.Id")
@AutoService(Processor.class)
public class SpringDataJdbcAnnotationProcessor extends AbstractQuerydslProcessor {

    private RoundEnvironment roundEnv;
    private CustomExtendedTypeFactory typeFactory;
    private Configuration conf;

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
                                                                                                  null, Ignored.class,
                                                                                                  typeMappings,
                                                                                                  codegenModule);
        this.conf = springDataJdbcConfiguration;
        return springDataJdbcConfiguration;
    }

    @Override
    protected TypeElementHandler createElementHandler(TypeMappings typeMappings, QueryTypeFactory queryTypeFactory) {
        return new CustomElementHandler(conf, typeFactory, typeMappings, queryTypeFactory, processingEnv.getElementUtils());
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

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    private @interface Ignored {
    }
}