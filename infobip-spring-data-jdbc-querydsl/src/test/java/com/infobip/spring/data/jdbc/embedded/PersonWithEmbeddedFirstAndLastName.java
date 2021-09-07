package com.infobip.spring.data.jdbc.embedded;

import lombok.Value;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("Person")
@Value
public class PersonWithEmbeddedFirstAndLastName {

    @With
    @Id
    private final Long id;

    @Embedded(onEmpty = Embedded.OnEmpty.USE_EMPTY)
    private final FirstAndLastName firstAndLastName;
    private final Instant createdAt;

    @PersistenceConstructor
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
        this.id = id;
        this.firstAndLastName = new FirstAndLastName(firstName, lastName);
        this.createdAt = createdAt;
    }
}

