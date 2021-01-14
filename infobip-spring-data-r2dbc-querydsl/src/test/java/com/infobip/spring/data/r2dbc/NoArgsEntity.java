package com.infobip.spring.data.r2dbc;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;

@ToString
@Getter
@EqualsAndHashCode
public class NoArgsEntity {

    @With
    @Id
    private final Long id;
    private final String value;

    NoArgsEntity(Long id) {
        this.id = id;
        this.value = null;
    }

    @PersistenceConstructor
    public NoArgsEntity(Long id, String value) {
        this.id = id;
        this.value = value;
    }

    NoArgsEntity(String value) {
        this.id = null;
        this.value = value;
    }
}
