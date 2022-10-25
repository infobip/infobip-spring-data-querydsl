package com.infobip.spring.data.jdbc.annotation.processor;

import javax.lang.model.element.Element;
import javax.lang.model.util.ElementFilter;
import java.util.Objects;

import com.querydsl.apt.Configuration;
import com.querydsl.codegen.Property;

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
                            .allMatch(enclosedElement -> Objects.nonNull(
                                    enclosedElement.getAnnotation(embeddedAnnotation)));
    }

    static boolean isEmbedded(Configuration configuration, Element element) {

        var embeddedAnnotation = configuration.getEmbeddedAnnotation();

        if (Objects.isNull(embeddedAnnotation)) {
            return false;
        }

        return Objects.nonNull(element.getAnnotation(embeddedAnnotation));
    }
}
