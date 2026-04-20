package com.infobip.spring.data.jdbc.annotation.processor;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QEntityWithEmbeddedEmpty is a Querydsl query type for EntityWithEmbeddedEmpty
 */
@Generated("com.infobip.spring.data.jdbc.annotation.processor.CustomMetaDataSerializer")
public class QEntityWithEmbeddedEmpty extends com.querydsl.sql.RelationalPathBase<EntityWithEmbeddedEmpty> {

    private static final long serialVersionUID = 738162607;

    public static final QEntityWithEmbeddedEmpty entityWithEmbeddedEmpty = new QEntityWithEmbeddedEmpty("EntityWithEmbeddedEmpty");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath foo = createString("foo");

    public QEntityWithEmbeddedEmpty(String variable) {
        super(EntityWithEmbeddedEmpty.class, forVariable(variable), "foo", "EntityWithEmbeddedEmpty");
        addMetadata();
    }

    public QEntityWithEmbeddedEmpty(String variable, String schema, String table) {
        super(EntityWithEmbeddedEmpty.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QEntityWithEmbeddedEmpty(String variable, String schema) {
        super(EntityWithEmbeddedEmpty.class, forVariable(variable), schema, "EntityWithEmbeddedEmpty");
        addMetadata();
    }

    public QEntityWithEmbeddedEmpty(Path<? extends EntityWithEmbeddedEmpty> path) {
        super(path.getType(), path.getMetadata(), "foo", "EntityWithEmbeddedEmpty");
        addMetadata();
    }

    public QEntityWithEmbeddedEmpty(PathMetadata metadata) {
        super(EntityWithEmbeddedEmpty.class, metadata, "foo", "EntityWithEmbeddedEmpty");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("Id").withIndex(0));
        addMetadata(foo, ColumnMetadata.named("Foo").withIndex(1));
    }

}
