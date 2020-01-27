package com.infobip.spring.data.jdbc.annotation.processor;

import com.google.common.base.Function;
import com.mysema.codegen.model.*;
import com.querydsl.codegen.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

final class CustomExtendedTypeFactory extends com.querydsl.apt.ExtendedTypeFactory {

    private final Elements elements;

    public CustomExtendedTypeFactory(
            ProcessingEnvironment env,
            Set<Class<? extends Annotation>> annotations,
            TypeMappings typeMappings,
            QueryTypeFactory queryTypeFactory,
            Function<EntityType,
                    String> variableNameFunction) {
        super(env, annotations, typeMappings, queryTypeFactory, variableNameFunction);
        this.elements = env.getElementUtils();
    }

    @Override
    protected Type createType(TypeElement typeElement, TypeCategory category,
                              List<? extends TypeMirror> typeArgs, boolean deep) {
        String simpleName = getSimpleName(typeElement, category);
        String name = elements.getPackageOf(typeElement) + "." + simpleName;
        String packageName = elements.getPackageOf(typeElement).getQualifiedName().toString();
        Type[] params = new Type[typeArgs.size()];
        for (int i = 0; i < params.length; i++) {
            params[i] = getType(typeArgs.get(i), deep);
        }
        return new SimpleType(category, name, packageName, simpleName, false,
                              typeElement.getModifiers().contains(Modifier.FINAL), params);
    }

    private String getSimpleName(TypeElement typeElement, TypeCategory category) {
        if (category.equals(TypeCategory.ENTITY)) {
            return "Q" + typeElement.getSimpleName().toString();
        }

        return typeElement.getSimpleName().toString();
    }

    @Override
    public boolean isSimpleTypeEntity(TypeElement typeElement, Class<? extends Annotation> entityAnn) {
        return typeElement.getAnnotation(entityAnn) != null
               || typeElement.getEnclosedElements()
                             .stream()
                             .anyMatch(element -> element.getAnnotation(entityAnn) != null);
    }
}
