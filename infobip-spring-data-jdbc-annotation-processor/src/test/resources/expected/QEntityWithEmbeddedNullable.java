package com.infobip.spring.data.jdbc.annotation.processor;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QEntityWithEmbeddedNullable is a Querydsl query type for EntityWithEmbeddedNullable
 */
@Generated("com.infobip.spring.data.jdbc.annotation.processor.CustomMetaDataSerializer")
public class QEntityWithEmbeddedNullable extends com.querydsl.sql.RelationalPathBase<EntityWithEmbeddedNullable> {

    private static final long serialVersionUID = 1557633471;

    public static final QEntityWithEmbeddedNullable entityWithEmbeddedNullable = new QEntityWithEmbeddedNullable("EntityWithEmbeddedNullable");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath foo = createString("foo");

    public QEntityWithEmbeddedNullable(String variable) {
        super(EntityWithEmbeddedNullable.class, forVariable(variable), "foo", "EntityWithEmbeddedNullable");
        addMetadata();
    }

    public QEntityWithEmbeddedNullable(String variable, String schema, String table) {
        super(EntityWithEmbeddedNullable.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QEntityWithEmbeddedNullable(String variable, String schema) {
        super(EntityWithEmbeddedNullable.class, forVariable(variable), schema, "EntityWithEmbeddedNullable");
        addMetadata();
    }

    public QEntityWithEmbeddedNullable(Path<? extends EntityWithEmbeddedNullable> path) {
        super(path.getType(), path.getMetadata(), "foo", "EntityWithEmbeddedNullable");
        addMetadata();
    }

    public QEntityWithEmbeddedNullable(PathMetadata metadata) {
        super(EntityWithEmbeddedNullable.class, metadata, "foo", "EntityWithEmbeddedNullable");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("Id").withIndex(0));
        addMetadata(foo, ColumnMetadata.named("Foo").withIndex(1));
    }

}
