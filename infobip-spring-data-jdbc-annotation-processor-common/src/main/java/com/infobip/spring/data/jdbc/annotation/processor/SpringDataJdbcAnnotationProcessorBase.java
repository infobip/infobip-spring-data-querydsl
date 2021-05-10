package com.infobip.spring.data.jdbc.annotation.processor;

import com.google.common.base.CaseFormat;
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
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class SpringDataJdbcAnnotationProcessorBase extends AbstractQuerydslProcessor {

    private RoundEnvironment roundEnv;
    private CustomExtendedTypeFactory typeFactory;
    private Configuration conf;
    private final NamingStrategy namingStrategy;
    private final TypeElementHandlerFactory typeElementHandlerFactory;
    private CaseFormat projectTableCaseFormat;
    private CaseFormat projectColumnCaseFormat;

    public SpringDataJdbcAnnotationProcessorBase(Class<? extends NamingStrategy> namingStrategyClass) {
        try {
            this.namingStrategy = namingStrategyClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException("Failed to create new instance of " + namingStrategyClass, e);
        }
        this.typeElementHandlerFactory = new DefaultTypeElementHandlerFactory();
    }

    public SpringDataJdbcAnnotationProcessorBase(NamingStrategy namingStrategy,
                                                 TypeElementHandlerFactory typeElementHandlerFactory,
                                                 CaseFormat projectTableCaseFormat,
                                                 CaseFormat projectColumnCaseFormat) {
        this.namingStrategy = namingStrategy;
        this.typeElementHandlerFactory = typeElementHandlerFactory;
        this.projectTableCaseFormat = projectTableCaseFormat;
        this.projectColumnCaseFormat = projectColumnCaseFormat;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        this.projectTableCaseFormat = getProjectCaseFormat(roundEnv, ProjectTableCaseFormat.class,
                                                           ProjectTableCaseFormat::value);
        this.projectColumnCaseFormat = getProjectCaseFormat(roundEnv, ProjectColumnCaseFormat.class,
                                                            ProjectColumnCaseFormat::value);

        return super.process(annotations, roundEnv);
    }

    private <A extends Annotation> CaseFormat getProjectCaseFormat(RoundEnvironment roundEnv,
                                                                   Class<A> annotation,
                                                                   Function<A, CaseFormat> valueExtractor) {
        return Optional.ofNullable(roundEnv.getElementsAnnotatedWith(annotation))
                       .filter(elements -> elements.size() == 1)
                       .map(elements -> elements.iterator().next())
                       .map(element -> element.getAnnotation(annotation))
                       .map(valueExtractor)
                       .orElse(CaseFormat.UPPER_CAMEL);
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
                                                                                                  namingStrategy);
        this.conf = springDataJdbcConfiguration;
        return springDataJdbcConfiguration;
    }

    @Override
    protected TypeElementHandler createElementHandler(TypeMappings typeMappings, QueryTypeFactory queryTypeFactory) {
        return typeElementHandlerFactory.createElementHandler(conf, typeFactory, typeMappings, queryTypeFactory,
                                                              processingEnv.getElementUtils(), roundEnv,
                                                              projectTableCaseFormat, projectColumnCaseFormat);
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
