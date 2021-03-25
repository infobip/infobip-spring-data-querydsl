package com.infobip.spring.data.jdbc.transientannotation;

import lombok.Value;
import lombok.With;
import org.springframework.data.annotation.*;

@Value
public class TransientEntity {

    @With
    @Id
    private final Long id;
    private final String value;

    @Transient
    private final String transientValue;

    public TransientEntity(Long id, String value, String transientValue) {
        this.id = id;
        this.value = value;
        this.transientValue = transientValue;
    }

    @PersistenceConstructor
    public TransientEntity(Long id, String value) {
        this.id = id;
        this.value = value;
        this.transientValue = null;
    }
}
