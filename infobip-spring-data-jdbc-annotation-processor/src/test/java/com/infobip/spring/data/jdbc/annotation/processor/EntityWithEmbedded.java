package com.infobip.spring.data.jdbc.annotation.processor;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Embedded;

@Schema("foo")
public class EntityWithEmbedded {

    @Id
    private final Long id;

    @Embedded(onEmpty = Embedded.OnEmpty.USE_EMPTY)
    private final EmbeddedClass embeddedClass;

    public EntityWithEmbedded(Long id,
                              EmbeddedClass embeddedClass) {
        this.id = id;
        this.embeddedClass = embeddedClass;
    }
}
