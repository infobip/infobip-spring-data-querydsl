package com.infobip.spring.data.jdbc;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.With;
import org.springframework.data.annotation.Id;

;

@Getter
@EqualsAndHashCode
public class NoArgsEntity {

    @With
    @Id
    private final Long id;
    private final String value;

    NoArgsEntity() {
        this.id = null;
        this.value = null;
    }

    NoArgsEntity(Long id) {
        this.id = id;
        this.value = null;
    }

    public NoArgsEntity(Long id, String value) {
        this.id = id;
        this.value = value;
    }

    NoArgsEntity(String value) {
        this.id = null;
        this.value = value;
    }
}
