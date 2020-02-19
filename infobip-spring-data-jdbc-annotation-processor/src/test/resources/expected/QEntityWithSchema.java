package com.infobip.spring.data.jdbc.annotation.processor;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QEntityWithSchema is a Querydsl query type for QEntityWithSchema
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QEntityWithSchema extends com.querydsl.sql.RelationalPathBase<QEntityWithSchema> {

    private static final long serialVersionUID = 1703560240;

    public static final QEntityWithSchema entityWithSchema = new QEntityWithSchema("EntityWithSchema");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QEntityWithSchema(String variable) {
        super(QEntityWithSchema.class, forVariable(variable), "foo", "EntityWithSchema");
        addMetadata();
    }

    public QEntityWithSchema(String variable, String schema, String table) {
        super(QEntityWithSchema.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QEntityWithSchema(String variable, String schema) {
        super(QEntityWithSchema.class, forVariable(variable), schema, "EntityWithSchema");
        addMetadata();
    }

    public QEntityWithSchema(Path<? extends QEntityWithSchema> path) {
        super(path.getType(), path.getMetadata(), "foo", "EntityWithSchema");
        addMetadata();
    }

    public QEntityWithSchema(PathMetadata metadata) {
        super(QEntityWithSchema.class, metadata, "foo", "EntityWithSchema");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("Id").withIndex(1));
    }

}

