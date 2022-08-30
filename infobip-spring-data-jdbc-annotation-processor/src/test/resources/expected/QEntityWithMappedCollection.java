package com.infobip.spring.data.jdbc.annotation.processor;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;

import com.querydsl.sql.ColumnMetadata;

/**
 * QEntityWithMappedCollection is a Querydsl query type for EntityWithMappedCollection
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QEntityWithMappedCollection extends com.querydsl.sql.RelationalPathBase<EntityWithMappedCollection> {

    private static final long serialVersionUID = 121269028;

    public static final QEntityWithMappedCollection entityWithMappedCollection = new QEntityWithMappedCollection("EntityWithMappedCollection");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<Object, SimplePath<Object>> mappedCollection = this.<Object, SimplePath<Object>>createList("mappedCollection", Object.class, SimplePath.class, PathInits.DIRECT2);

    public QEntityWithMappedCollection(String variable) {
        super(EntityWithMappedCollection.class, forVariable(variable), null, "EntityWithMappedCollection");
        addMetadata();
    }

    public QEntityWithMappedCollection(String variable, String schema, String table) {
        super(EntityWithMappedCollection.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QEntityWithMappedCollection(String variable, String schema) {
        super(EntityWithMappedCollection.class, forVariable(variable), schema, "EntityWithMappedCollection");
        addMetadata();
    }

    public QEntityWithMappedCollection(Path<? extends EntityWithMappedCollection> path) {
        super(path.getType(), path.getMetadata(), null, "EntityWithMappedCollection");
        addMetadata();
    }

    public QEntityWithMappedCollection(PathMetadata metadata) {
        super(EntityWithMappedCollection.class, metadata, null, "EntityWithMappedCollection");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("Id").withIndex(0));
        addMetadata(mappedCollection, ColumnMetadata.named("MappedCollection").withIndex(1));
    }

}
