package com.infobip.spring.data.jdbc.annotation.processor;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Embedded;

@Schema("foo")
public class EntityWithEmbeddedNullable {

    @Id
    private final Long id;

    @Embedded.Nullable
    private final EmbeddedClass embeddedClass;

    public EntityWithEmbeddedNullable(Long id,
                                      EmbeddedClass embeddedClass) {
        this.id = id;
        this.embeddedClass = embeddedClass;
    }
}
