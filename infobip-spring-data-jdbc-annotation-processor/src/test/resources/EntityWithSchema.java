package com.infobip.spring.data.jdbc.annotation.processor;

import lombok.Value;
import org.springframework.data.annotation.Id;

@Schema("foo")
public class EntityWithSchema {

    @Id
    private final Long id;

    public EntityWithSchema(Long id) {
        this.id = id;
    }
}
