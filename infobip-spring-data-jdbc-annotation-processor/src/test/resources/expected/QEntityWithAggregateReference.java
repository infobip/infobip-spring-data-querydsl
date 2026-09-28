package com.infobip.spring.data.jdbc.annotation.processor;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QEntityWithAggregateReference is a Querydsl query type for EntityWithAggregateReference
 */
@SuppressWarnings("this-escape")
@Generated("com.infobip.spring.data.jdbc.annotation.processor.CustomMetaDataSerializer")
public class QEntityWithAggregateReference extends com.querydsl.sql.RelationalPathBase<EntityWithAggregateReference> {

    private static final long serialVersionUID = 73453824;

    public static final QEntityWithAggregateReference entityWithAggregateReference = new QEntityWithAggregateReference("EntityWithAggregateReference");

    public final StringPath id = createString("id");

    public final StringPath referencedEntityId = createString("referencedEntityId");

    public QEntityWithAggregateReference(String variable) {
        super(EntityWithAggregateReference.class, forVariable(variable), "dbo", "EntityWithAggregateReference");
        addMetadata();
    }

    public QEntityWithAggregateReference(String variable, String schema, String table) {
        super(EntityWithAggregateReference.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QEntityWithAggregateReference(String variable, String schema) {
        super(EntityWithAggregateReference.class, forVariable(variable), schema, "EntityWithAggregateReference");
        addMetadata();
    }

    public QEntityWithAggregateReference(Path<? extends EntityWithAggregateReference> path) {
        super(path.getType(), path.getMetadata(), "dbo", "EntityWithAggregateReference");
        addMetadata();
    }

    public QEntityWithAggregateReference(PathMetadata metadata) {
        super(EntityWithAggregateReference.class, metadata, "dbo", "EntityWithAggregateReference");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("Id").withIndex(0));
        addMetadata(referencedEntityId, ColumnMetadata.named("ReferencedEntityId").withIndex(1));
    }

}
