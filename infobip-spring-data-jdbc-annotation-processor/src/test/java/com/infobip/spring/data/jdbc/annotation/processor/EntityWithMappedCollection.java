package com.infobip.spring.data.jdbc.annotation.processor;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;

public class EntityWithMappedCollection {

    @Id
    private final Long id;

    @MappedCollection
    private final List<Object> mappedCollection;

    public EntityWithMappedCollection(Long id, List<Object> mappedCollection) {
        this.id = id;
        this.mappedCollection = mappedCollection;
    }

}
