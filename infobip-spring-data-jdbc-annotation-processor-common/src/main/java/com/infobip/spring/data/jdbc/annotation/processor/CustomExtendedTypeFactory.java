package com.infobip.spring.data.jdbc.annotation.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.CaseFormat;
import com.querydsl.apt.Configuration;
import com.querydsl.apt.ExtendedTypeFactory;
import com.querydsl.codegen.EntityType;
import com.querydsl.codegen.Property;
import com.querydsl.codegen.QueryTypeFactory;
import com.querydsl.codegen.TypeMappings;
import com.querydsl.codegen.utils.model.Type;
import com.querydsl.codegen.utils.model.TypeCategory;
import org.springframework.data.relational.core.mapping.Table;

class CustomExtendedTypeFactory extends ExtendedTypeFactory {

    public static final String IS_EMBEDDED_DATA_KEY = "isEmbedded";

    private final Configuration configuration;
    private final Elements elements;
    private final String defaultSchema;
    private final CaseFormat tableCaseFormat;
    private final Types types;

    public CustomExtendedTypeFactory(RoundEnvironment roundEnv,
                                     ProcessingEnvironment env,
                                     Set<Class<? extends Annotation>> annotations,
                                     TypeMappings typeMappings,
                                     QueryTypeFactory queryTypeFactory,
                                     Configuration configuration,
                                     Elements elements,
                                     CaseFormat tableCaseFormat) {
        super(env, annotations, typeMappings, queryTypeFactory, configuration.getVariableNameFunction());
        this.types = env.getTypeUtils();
        this.configuration = configuration;
        this.elements = elements;
        this.defaultSchema = getDefaultSchema(roundEnv);
        this.tableCaseFormat = tableCaseFormat;
    }

    @Override
    public boolean isSimpleTypeEntity(TypeElement typeElement, Class<? extends Annotation> entityAnn) {
        return typeElement.getAnnotation(entityAnn) != null
               || typeElement.getEnclosedElements()
                             .stream()
                             .anyMatch(element -> element.getAnnotation(entityAnn) != null);
    }

    @Override
    public EntityType getEntityType(TypeMirror typeMirror, boolean deep) {
        Element element = types.asElement(typeMirror);
        EntityType entityType = super.getEntityType(typeMirror, deep);
        List<Property> embeddedlessProperties =
                entityType.getProperties()
                          .stream()
                          .flatMap(property -> {
                              if (Embeddeds.isEmbedded(configuration, element, property)) {
                                  return flattenEmbeddedProperty(property);
                              }

                              return Stream.of(property);
                          })
                          .collect(Collectors.toList());
        entityType.getProperties().clear();
        entityType.getProperties().addAll(embeddedlessProperties);
        entityType.getPropertyNames().clear();
        entityType.getPropertyNames()
                  .addAll(embeddedlessProperties.stream().map(Property::getName).collect(Collectors.toList()));
        updateModel(element, entityType);
        return entityType;
    }

    protected String getDefaultSchema(RoundEnvironment roundEnv) {
        Set<? extends Element> defaultSchemaElements = roundEnv.getElementsAnnotatedWith(DefaultSchema.class);

        if (defaultSchemaElements.isEmpty()) {
            return null;
        }

        if (defaultSchemaElements.size() > 1) {
            throw new IllegalArgumentException("found multiple elements with DefaultSchema " + defaultSchemaElements);
        }

        return defaultSchemaElements.iterator().next().getAnnotation(DefaultSchema.class).value();
    }

    private Stream<Property> flattenEmbeddedProperty(Property property) {
        Type type = property.getType();

        if (!type.getCategory().equals(TypeCategory.ENTITY)) {
            return Stream.of(property);
        }

        return ((EntityType) type).getProperties().stream();
    }

    private void updateModel(Element element, EntityType type) {
        Map<Object, Object> data = type.getData();
        data.put("table", getTableName(type));
        getSchema(element).ifPresent(schema -> data.put("schema", schema));

        AtomicInteger counter = new AtomicInteger();
        type.getProperties()
            .forEach(property -> addMetaData(element, counter, property));
    }

    private void addMetaData(Element element, AtomicInteger counter,
                               Property property) {

        if (Embeddeds.isEmbedded(configuration, element, property)) {
            property.getData().put(IS_EMBEDDED_DATA_KEY, true);
        }
    }

    protected Optional<String> getSchema(Element element) {
        Schema elementSchema = element.getAnnotation(Schema.class);

        if (Objects.isNull(elementSchema)) {
            return Optional.ofNullable(defaultSchema);
        }

        return Optional.of(elementSchema.value());
    }

    protected String getTableName(EntityType model) {
        String simpleName = model.getSimpleName();
        String className = model.getPackageName() + "." + simpleName;
        String tableName = CaseFormat.UPPER_CAMEL.to(tableCaseFormat, simpleName);
        return Optional.ofNullable(elements.getTypeElement(className)
                                           .getAnnotation(Table.class))
                       .map(Table::value)
                       .orElse(tableName);
    }

    private Stream<? extends Element> getFields(Element element) {
        return element.getEnclosedElements()
                      .stream()
                      .filter(enclosedElement -> enclosedElement.getKind().equals(ElementKind.FIELD));
    }
}
