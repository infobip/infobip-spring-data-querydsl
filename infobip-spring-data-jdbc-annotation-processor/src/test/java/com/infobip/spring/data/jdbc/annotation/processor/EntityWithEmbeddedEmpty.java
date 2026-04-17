package com.infobip.spring.data.jdbc.annotation.processor;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Embedded;

@Schema("foo")
public class EntityWithEmbeddedEmpty {

    @Id
    private final Long id;

    @Embedded.Empty
    private final EmbeddedClass embeddedClass;

    public EntityWithEmbeddedEmpty(Long id,
                                   EmbeddedClass embeddedClass) {
        this.id = id;
        this.embeddedClass = embeddedClass;
    }
}
