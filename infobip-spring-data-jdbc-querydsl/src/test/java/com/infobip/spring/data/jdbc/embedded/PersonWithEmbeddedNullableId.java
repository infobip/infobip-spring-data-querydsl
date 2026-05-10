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
public record PersonWithEmbeddedNullableId(

    @With
    @Id
    @Embedded.Nullable
    FirstAndLastName id,

    Instant createdAt,

    @With
    @Transient
    boolean newEntity
) implements Persistable<FirstAndLastName> {

    @PersistenceCreator
    public PersonWithEmbeddedNullableId(FirstAndLastName id, Instant createdAt) {
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
