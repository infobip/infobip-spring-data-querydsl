package com.infobip.spring.data.jdbc.annotation.processor;

import com.querydsl.apt.DefaultConfiguration;
import com.querydsl.apt.VisitorConfig;
import com.querydsl.codegen.*;
import com.querydsl.core.annotations.QueryEntities;
import com.querydsl.sql.codegen.NamingStrategy;
import com.querydsl.sql.codegen.SQLCodegenModule;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.List;

import static com.querydsl.apt.VisitorConfig.FIELDS_ONLY;

class SpringDataJdbcConfiguration extends DefaultConfiguration {

    private final SQLCodegenModule sqlCodegenModule;

    public SpringDataJdbcConfiguration(RoundEnvironment roundEnv,
                                       ProcessingEnvironment processingEnv,
                                       Class<? extends Annotation> entityAnn,
                                       Class<? extends Annotation> superTypeAnn,
                                       Class<? extends Annotation> embeddableAnn,
                                       Class<? extends Annotation> embeddedAnn,
                                       Class<? extends Annotation> skipAnn,
                                       TypeMappings typeMappings,
                                       CodegenModule codegenModule,
                                       NamingStrategy namingStrategy) {
        super(processingEnv, roundEnv, Keywords.JPA, QueryEntities.class, entityAnn, superTypeAnn,
              embeddableAnn, embeddedAnn, skipAnn, codegenModule);
        setStrictMode(true);
        sqlCodegenModule = new SQLCodegenModule();
        sqlCodegenModule.bind(NamingStrategy.class, namingStrategy);
        sqlCodegenModule.bind(TypeMappings.class, typeMappings);
    }

    @Override
    public VisitorConfig getConfig(TypeElement e, List<? extends Element> elements) {
        return FIELDS_ONLY;
    }

    @Override
    public Serializer getEntitySerializer() {
        return sqlCodegenModule.get(Serializer.class);
    }

    @Override
    public SerializerConfig getSerializerConfig(EntityType entityType) {
        return SimpleSerializerConfig.DEFAULT;
    }
}
