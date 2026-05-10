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
public record PersonWithEmbeddedEmptyId(

    @With
    @Id
    @Embedded.Empty
    FirstAndLastName id,

    Instant createdAt,

    @With
    @Transient
    boolean newEntity
) implements Persistable<FirstAndLastName> {

    @PersistenceCreator
    public PersonWithEmbeddedEmptyId(FirstAndLastName id, Instant createdAt) {
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
