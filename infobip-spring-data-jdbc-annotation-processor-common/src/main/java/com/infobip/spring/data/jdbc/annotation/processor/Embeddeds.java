package com.infobip.spring.data.jdbc.annotation.processor;

import com.querydsl.apt.Configuration;
import com.querydsl.codegen.Property;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import java.lang.annotation.Annotation;
import java.util.Objects;

class Embeddeds {

    static boolean isEmbedded(Configuration configuration,
                              Element element,
                              Property property) {

        Class<? extends Annotation> embeddedAnnotation = configuration.getEmbeddedAnnotation();

        if (Objects.isNull(embeddedAnnotation)) {
            return false;
        }

        return ElementFilter.fieldsIn(element.getEnclosedElements())
                            .stream()
                            .filter(enclosedElement -> enclosedElement.getSimpleName()
                                                                      .toString()
                                                                      .equals(property.getName()))
                            .allMatch(enclosedElement -> Objects.nonNull(
                                    enclosedElement.getAnnotation(embeddedAnnotation)));
    }

    static boolean isEmbedded(Configuration configuration, Element element) {

        Class<? extends Annotation> embeddedAnnotation = configuration.getEmbeddedAnnotation();

        if (Objects.isNull(embeddedAnnotation)) {
            return false;
        }

        return Objects.nonNull(element.getAnnotation(embeddedAnnotation));
    }
}
