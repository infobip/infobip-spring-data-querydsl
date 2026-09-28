package com.infobip.spring.data.jdbc.annotation.processor;

import org.springframework.data.annotation.Id;

@Schema("dbo")
public class AggregateReferenceTarget {

    @Id
    private final String id;

    public AggregateReferenceTarget(String id) {
        this.id = id;
    }
}
