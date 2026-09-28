package com.infobip.spring.data.jdbc.annotation.processor;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

@Schema("dbo")
public class EntityWithAggregateReference {

    @Id
    private final String id;

    private final AggregateReference<AggregateReferenceTarget, String> referencedEntityId;

    public EntityWithAggregateReference(String id,
                                        AggregateReference<AggregateReferenceTarget, String> referencedEntityId) {
        this.id = id;
        this.referencedEntityId = referencedEntityId;
    }
}
