package com.infobip.spring.data.jdbc.annotation.processor;

import com.google.common.base.CaseFormat;
import com.querydsl.apt.*;
import com.querydsl.codegen.*;
import com.querydsl.sql.ColumnMetadata;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import java.util.*;
import java.util.stream.Collectors;

class CustomElementHandler extends TypeElementHandler {

    private final Elements elements;
    private final String defaultSchema;

    public CustomElementHandler(Configuration configuration,
                                ExtendedTypeFactory typeFactory,
                                TypeMappings typeMappings,
                                QueryTypeFactory queryTypeFactory,
                                Elements elements,
                                RoundEnvironment roundEnv) {
        super(configuration, typeFactory, typeMappings, queryTypeFactory);
        this.elements = elements;
        this.defaultSchema = getDefaultSchema(roundEnv);
    }

    private String getDefaultSchema(RoundEnvironment roundEnv) {
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
        updateModel(element, entityType);
        return entityType;
    }

    private void updateModel(TypeElement element, EntityType type) {
        Map<Object, Object> data = type.getData();
        data.put("table", getTableName(type));
        getSchema(element).ifPresent(schema -> data.put("schema", schema));

        Map<String, Integer> fieldNameToIndex = getFieldNameToIndex(type);

        type.getProperties()
            .forEach(property -> property.getData().put("COLUMN", ColumnMetadata.named(getColumnName(property))
                                                                                .withIndex(fieldNameToIndex.get(
                                                                                        property.getName()))));
    }

    private Optional<String> getSchema(TypeElement element) {
        Schema elementSchema = element.getAnnotation(Schema.class);

        if (Objects.isNull(elementSchema)) {
            return Optional.ofNullable(defaultSchema);
        }

        return Optional.of(elementSchema.value());
    }

    private String getTableName(EntityType model) {
        String simpleName = simpleNameWithoutPrefix(model.getSimpleName());
        String className = model.getPackageName() + "." + simpleName;
        return Optional.ofNullable(elements.getTypeElement(className)
                                           .getAnnotation(Table.class))
                       .map(Table::value)
                       .orElse(simpleName);
    }

    private String getColumnName(Property property) {
        String name = nameWithoutPrefix(property.getDeclaringType().getPackageName(),
                                        property.getDeclaringType().getSimpleName());
        TypeElement parentType = elements.getTypeElement(name);
        return parentType.getEnclosedElements()
                         .stream()
                         .filter(element -> element instanceof VariableElement)
                         .map(element -> (VariableElement) element)
                         .filter(element -> element.getSimpleName().toString().equals(property.getName()))
                         .filter(element -> element.getAnnotation(Column.class) != null)
                         .map(element -> element.getAnnotation(Column.class).value())
                         .findAny()
                         .orElseGet(() -> CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, property.getName()));
    }

    private Map<String, Integer> getFieldNameToIndex(EntityType model) {
        String name = nameWithoutPrefix(model.getPackageName(), model.getSimpleName());
        TypeElement typeElement = elements.getTypeElement(name);
        List<? extends Element> fields = typeElement.getEnclosedElements()
                                                    .stream()
                                                    .filter(element -> element.getKind().equals(ElementKind.FIELD))
                                                    .collect(Collectors.toList());

        Map<String, Integer> fieldNameToIndex = new HashMap<>();

        for (int index = 0; index < fields.size(); index++) {
            fieldNameToIndex.put(fields.get(index).getSimpleName().toString(), index + 1);
        }
        return fieldNameToIndex;
    }

    private String simpleNameWithoutPrefix(String simpleName) {
        return simpleName.substring(1);
    }

    private String nameWithoutPrefix(String packageName, String simpleName) {
        return name(packageName, simpleNameWithoutPrefix(simpleName));
    }

    private String name(String packageName, String simpleName) {
        return packageName + "." + simpleName;
    }
}
