package com.infobip.spring.data.jdbc.annotation.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.base.CaseFormat;
import com.querydsl.codegen.EntityType;
import com.querydsl.codegen.Property;
import com.querydsl.codegen.SerializerConfig;
import com.querydsl.codegen.TypeMappings;
import com.querydsl.codegen.utils.CodeWriter;
import com.querydsl.sql.ColumnMetadata;
import com.querydsl.sql.codegen.MetaDataSerializer;
import com.querydsl.sql.codegen.NamingStrategy;
import com.querydsl.sql.codegen.SQLCodegenModule;
import org.springframework.data.relational.core.mapping.Column;

public class CustomMetaDataSerializer extends MetaDataSerializer {

    private final ProcessingEnvironment processingEnvironment;
    private final CaseFormat columnCaseFormat;

    @Inject
    public CustomMetaDataSerializer(
        TypeMappings typeMappings,
        NamingStrategy namingStrategy,
        ProcessingEnvironment processingEnvironment,
        CaseFormat columnCaseFormat,
        @Named(SQLCodegenModule.INNER_CLASSES_FOR_KEYS) boolean innerClassesForKeys,
        @Named(SQLCodegenModule.IMPORTS) Set<String> imports,
        @Named(SQLCodegenModule.COLUMN_COMPARATOR) Comparator<Property> columnComparator,
        @Named(SQLCodegenModule.ENTITYPATH_TYPE) Class<?> entityPathType,
        @Named(SQLCodegenModule.GENERATED_ANNOTATION_CLASS) Class<? extends Annotation> generatedAnnotationClass) {
        super(typeMappings, namingStrategy, innerClassesForKeys, imports, columnComparator, entityPathType, generatedAnnotationClass);
        this.processingEnvironment = processingEnvironment;
        this.columnCaseFormat = columnCaseFormat;
    }

    @Override
    protected void serializeProperties(EntityType model, SerializerConfig config,
                                       CodeWriter writer) throws IOException {
        var newModel = withFieldOrderedProperties(model);
        super.serializeProperties(newModel, config, writer);
    }

    @Override
    protected void outro(EntityType model, CodeWriter writer) throws IOException {
        var newModel = withFieldOrderedProperties(model);
        super.outro(newModel, writer);
    }

    public CustomPropertiesEntityType withFieldOrderedProperties(EntityType entityType) {
        var properties = entityType.getProperties();
        var propertyNameToProperty = properties
            .stream()
            .collect(Collectors.toMap(Property::getName, Function.identity()));

        var typeElement = processingEnvironment.getElementUtils().getTypeElement(entityType.getFullName());
        var orderedProperties = ElementFilter.fieldsIn(new ArrayList<>(typeElement.getEnclosedElements()))
                                             .stream()
                                             .map(field -> propertyNameToProperty.get(field.getSimpleName()
                                                                                                               .toString()))
                                             .filter(Objects::nonNull)
                                             .collect(Collectors.toCollection(LinkedHashSet::new));

        properties.stream()
                  .filter(property -> !orderedProperties.contains(property))
                  .forEach(orderedProperties::add);

        addMetaData(orderedProperties);

        return new CustomPropertiesEntityType(entityType, orderedProperties);
    }

    private void addMetaData(LinkedHashSet<Property> orderedProperties) {
        List<Property> orderedPropertiesList = new ArrayList<>(orderedProperties);
        for (var i = 0; i < orderedPropertiesList.size(); i++) {
            var property = orderedPropertiesList.get(i);
            property.getData().put("COLUMN", ColumnMetadata.named(getColumnName(property))
                                                           .withIndex(i));

        }
    }

    protected String getColumnName(Property property) {
        var parentType = processingEnvironment.getElementUtils()
                                              .getTypeElement(property.getDeclaringType().getFullName());

        // Check if property has a column prefix from embedded fields
        var columnPrefix = (String) property.getData().get("columnPrefix");

        var columnNameFromAnnotation = parentType.getEnclosedElements()
                         .stream()
                         .filter(element -> element instanceof VariableElement)
                         .map(element -> (VariableElement) element)
                         .filter(element -> element.getSimpleName().toString().equals(property.getName()))
                         .filter(element -> element.getAnnotation(Column.class) != null)
                         .map(element -> element.getAnnotation(Column.class).value())
                         .findAny();

        if (columnNameFromAnnotation.isPresent()) {
            return columnNameFromAnnotation.get();
        }

        // For prefixed embedded properties, extract the original property name after the prefix
        var propertyName = property.getName();
        if (columnPrefix != null && !columnPrefix.isEmpty() && propertyName.startsWith(columnPrefix)) {
            // Extract the original field name (e.g., "player1_Id" -> "Id")
            var originalFieldName = propertyName.substring(columnPrefix.length());
            // Convert first char to lowercase to get proper camelCase (e.g., "Id" -> "id")
            if (!originalFieldName.isEmpty()) {
                originalFieldName = Character.toLowerCase(originalFieldName.charAt(0)) + originalFieldName.substring(1);
            }
            // Generate column name: prefix + converted field name
            return columnPrefix + CaseFormat.LOWER_CAMEL.to(columnCaseFormat, originalFieldName);
        }

        return CaseFormat.LOWER_CAMEL.to(columnCaseFormat, propertyName);
    }

    static class CustomPropertiesEntityType extends EntityType {

        private final Set<Property> fieldOrderedProperties;

        CustomPropertiesEntityType(EntityType entityType, Set<Property> fieldOrderedProperties) {
            super(entityType);
            this.fieldOrderedProperties = fieldOrderedProperties;
        }

        @Override
        public Set<Property> getProperties() {
            return fieldOrderedProperties;
        }

    }

}
