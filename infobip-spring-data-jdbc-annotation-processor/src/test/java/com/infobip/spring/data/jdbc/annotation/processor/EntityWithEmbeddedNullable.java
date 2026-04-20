package com.infobip.spring.data.jdbc.annotation.processor;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Embedded;

@Schema("foo")
public class EntityWithEmbeddedNullable {

    @Id
    private final Long id;

    @Embedded.Nullable
    private final EntityWithEmbeddedNullableEmbeddedClass embeddedClass;

    public EntityWithEmbeddedNullable(Long id,
                                      EntityWithEmbeddedNullableEmbeddedClass embeddedClass) {
        this.id = id;
        this.embeddedClass = embeddedClass;
    }
}
