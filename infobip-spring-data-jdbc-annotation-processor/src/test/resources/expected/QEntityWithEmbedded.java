package com.infobip.spring.data.jdbc.annotation.processor;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import jakarta.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QEntityWithEmbedded is a Querydsl query type for EntityWithEmbedded
 */
@Generated("com.infobip.spring.data.jdbc.annotation.processor.CustomMetaDataSerializer")
public class QEntityWithEmbedded extends com.querydsl.sql.RelationalPathBase<EntityWithEmbedded> {

    private static final long serialVersionUID = -229043138;

    public static final QEntityWithEmbedded entityWithEmbedded = new QEntityWithEmbedded("EntityWithEmbedded");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath foo = createString("foo");

    public QEntityWithEmbedded(String variable) {
        super(EntityWithEmbedded.class, forVariable(variable), "foo", "EntityWithEmbedded");
        addMetadata();
    }

    public QEntityWithEmbedded(String variable, String schema, String table) {
        super(EntityWithEmbedded.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QEntityWithEmbedded(String variable, String schema) {
        super(EntityWithEmbedded.class, forVariable(variable), schema, "EntityWithEmbedded");
        addMetadata();
    }

    public QEntityWithEmbedded(Path<? extends EntityWithEmbedded> path) {
        super(path.getType(), path.getMetadata(), "foo", "EntityWithEmbedded");
        addMetadata();
    }

    public QEntityWithEmbedded(PathMetadata metadata) {
        super(EntityWithEmbedded.class, metadata, "foo", "EntityWithEmbedded");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("Id").withIndex(0));
        addMetadata(foo, ColumnMetadata.named("Foo").withIndex(1));
    }

}

