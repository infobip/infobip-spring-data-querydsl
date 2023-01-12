package com.infobip.spring.data.jdbc.embedded;

import java.time.Instant;

import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

@Table("Person")
public record PersonWithEmbeddedFirstAndLastName(
    
    @With
    @Id
    Long id,

    @Embedded(onEmpty = Embedded.OnEmpty.USE_EMPTY)
    FirstAndLastName firstAndLastName,
    Instant createdAt
) {


    @PersistenceCreator
    public PersonWithEmbeddedFirstAndLastName(Long id,
                                              FirstAndLastName firstAndLastName,
                                              Instant createdAt) {
        this.id = id;
        this.firstAndLastName = firstAndLastName;
        this.createdAt = createdAt;
    }

    public PersonWithEmbeddedFirstAndLastName(Long id,
                                              String firstName,
                                              String lastName,
                                              Instant createdAt) {
        this(id, new FirstAndLastName(firstName, lastName), createdAt);
    }

}

