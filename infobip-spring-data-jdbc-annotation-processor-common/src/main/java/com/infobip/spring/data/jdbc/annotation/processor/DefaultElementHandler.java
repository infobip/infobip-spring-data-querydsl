package com.infobip.spring.data.jdbc.annotation.processor;

import com.google.common.base.CaseFormat;
import com.querydsl.apt.*;
import com.querydsl.codegen.*;
import com.querydsl.codegen.utils.model.Type;
import com.querydsl.codegen.utils.model.TypeCategory;
import com.querydsl.sql.ColumnMetadata;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultElementHandler extends TypeElementHandler {

    public static final String IS_EMBEDDED_DATA_KEY = "isEmbedded";

    private final Elements elements;
    private final String defaultSchema;
    private final CaseFormat tableCaseFormat;
    private final CaseFormat columnCaseFormat;
    private final Configuration configuration;

    public DefaultElementHandler(Configuration configuration,
                                 ExtendedTypeFactory typeFactory,
                                 TypeMappings typeMappings,
                                 QueryTypeFactory queryTypeFactory,
                                 Elements elements,
                                 RoundEnvironment roundEnv,
                                 CaseFormat tableCaseFormat,
                                 CaseFormat columnCaseFormat) {
        super(configuration, typeFactory, typeMappings, queryTypeFactory);
        this.elements = elements;
        this.defaultSchema = getDefaultSchema(roundEnv);
        this.tableCaseFormat = tableCaseFormat;
        this.columnCaseFormat = columnCaseFormat;
        this.configuration = configuration;
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

    @Override
    public EntityType handleEntityType(TypeElement element) {
        EntityType entityType = super.handleEntityType(element);

        Set<Property> embeddedlessProperties =
                entityType.getProperties()
                          .stream()
                          .flatMap(property -> {
                              if (Embeddeds.isEmbedded(configuration, element, property)) {
                                  return flattenEmbeddedProperty(property);
                              }

                              return Stream.of(property);
                          })
                          .collect(Collectors.toSet());
        entityType.getProperties().clear();
        entityType.getProperties().addAll(embeddedlessProperties);

        updateModel(element, entityType);
        return entityType;
    }

    private Stream<Property> flattenEmbeddedProperty(Property property) {
        Type type = property.getType();

        if (!type.getCategory().equals(TypeCategory.ENTITY)) {
            return Stream.of(property);
        }

        return ((EntityType) type).getProperties().stream();
    }

    private void updateModel(TypeElement element, EntityType type) {
        Map<Object, Object> data = type.getData();
        data.put("table", getTableName(type));
        getSchema(element).ifPresent(schema -> data.put("schema", schema));

        Map<String, Integer> fieldNameToIndex = getFieldNameToIndex(type);

        type.getProperties()
            .forEach(property -> addMetaData(element, fieldNameToIndex, property));
    }

    private Object addMetaData(TypeElement element, Map<String, Integer> fieldNameToIndex,
                               Property property) {

        Map<Object, Object> data = property.getData();
        if (Embeddeds.isEmbedded(configuration, element, property)) {
            data.put(IS_EMBEDDED_DATA_KEY, true);
        }

        int index = Optional.ofNullable(fieldNameToIndex.get(property.getName())).orElse(0);
        return data.put("COLUMN", ColumnMetadata.named(getColumnName(property))
                                                .withIndex(index));
    }

    protected Optional<String> getSchema(TypeElement element) {
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

    protected String getColumnName(Property property) {
        TypeElement parentType = elements.getTypeElement(property.getDeclaringType().getFullName());
        return parentType.getEnclosedElements()
                         .stream()
                         .filter(element -> element instanceof VariableElement)
                         .map(element -> (VariableElement) element)
                         .filter(element -> element.getSimpleName().toString().equals(property.getName()))
                         .filter(element -> element.getAnnotation(Column.class) != null)
                         .map(element -> element.getAnnotation(Column.class).value())
                         .findAny()
                         .orElseGet(() -> CaseFormat.LOWER_CAMEL.to(columnCaseFormat, property.getName()));
    }

    private Map<String, Integer> getFieldNameToIndex(EntityType model) {
        String name = model.getFullName();
        TypeElement typeElement = elements.getTypeElement(name);
        List<? extends Element> fields = getFields(typeElement).flatMap(field -> {
            if (Embeddeds.isEmbedded(configuration, field)) {
                TypeMirror typeMirror = field.asType();
                return getFields(elements.getTypeElement(typeMirror.toString()));
            }

            return Stream.of(field);
        }).collect(Collectors.toList());

        Map<String, Integer> fieldNameToIndex = new HashMap<>();

        for (int index = 0; index < fields.size(); index++) {
            fieldNameToIndex.put(fields.get(index).getSimpleName().toString(), index + 1);
        }
        return fieldNameToIndex;
    }

    private Stream<? extends Element> getFields(Element element) {
        return element.getEnclosedElements()
                      .stream()
                      .filter(enclosedElement -> enclosedElement.getKind().equals(ElementKind.FIELD));
    }
}
