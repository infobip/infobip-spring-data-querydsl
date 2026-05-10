package com.infobip.spring.data.jdbc.embedded;

import java.time.Instant;

import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

@Table("PersonWithEmbeddedId")
public record PersonWithEmbeddedId(

    @With
    @Id
    @Embedded(onEmpty = Embedded.OnEmpty.USE_EMPTY)
    FirstAndLastName id,

    Instant createdAt,

    @With
    @Transient
    boolean newEntity
) implements Persistable<FirstAndLastName> {

    @PersistenceCreator
    public PersonWithEmbeddedId(FirstAndLastName id, Instant createdAt) {
        this(id, createdAt, false);
    }

    @Override
    public boolean isNew() {
        return newEntity;
    }

    @Override
    public FirstAndLastName getId() {
        return id;
    }
}
