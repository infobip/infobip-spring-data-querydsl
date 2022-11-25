package com.infobip.spring.data.r2dbc.transientannotation;

import lombok.Value;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;

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

    @PersistenceCreator
    public TransientEntity(Long id, String value) {
        this.id = id;
        this.value = value;
        this.transientValue = null;
    }
}
