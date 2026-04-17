package com.infobip.spring.data.jdbc.annotation.processor;

import javax.lang.model.element.Element;
import javax.lang.model.util.ElementFilter;
import java.lang.annotation.Annotation;
import java.util.Objects;

import com.querydsl.apt.Configuration;
import com.querydsl.codegen.Property;
import org.springframework.data.relational.core.mapping.Embedded;

class Embeddeds {

    static boolean isEmbedded(Configuration configuration,
                              Element element,
                              Property property) {

        var embeddedAnnotation = configuration.getEmbeddedAnnotation();

        if (Objects.isNull(embeddedAnnotation)) {
            return false;
        }

        return ElementFilter.fieldsIn(element.getEnclosedElements())
                            .stream()
                            .filter(enclosedElement -> enclosedElement.getSimpleName()
                                                                      .toString()
                                                                      .equals(property.getName()))
                            .allMatch(Embeddeds::hasEmbeddedAnnotation);
    }

    static boolean isEmbedded(Configuration configuration, Element element) {

        var embeddedAnnotation = configuration.getEmbeddedAnnotation();

        if (Objects.isNull(embeddedAnnotation)) {
            return false;
        }

        return hasEmbeddedAnnotation(element);
    }

    private static boolean hasEmbeddedAnnotation(Element element) {
        return hasAnnotation(element, Embedded.class)
               || hasAnnotation(element, Embedded.Empty.class)
               || hasAnnotation(element, Embedded.Nullable.class);
    }

    private static boolean hasAnnotation(Element element, Class<? extends Annotation> annotationType) {
        return Objects.nonNull(element.getAnnotation(annotationType));
    }
}
