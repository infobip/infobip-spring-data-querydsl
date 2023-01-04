package com.infobip.spring.data.jdbc.transientannotation;

import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;

public record TransientEntity(

    @With
    @Id
    Long id,
    String value,

    @Transient
    String transientValue
) {

    @PersistenceCreator
    public TransientEntity(Long id, String value) {
        this(id, value, null);
    }

}
