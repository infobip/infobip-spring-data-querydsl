package com.infobip.spring.data.jdbc.annotation.processor;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QFoo is a Querydsl query type for Foo
 */
@Generated("com.infobip.spring.data.jdbc.annotation.processor.CustomMetaDataSerializer")
public class QFoo extends com.querydsl.sql.RelationalPathBase<Foo> {

    private static final long serialVersionUID = -900353829;

    public static final QFoo foo = new QFoo("Foo");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QFoo(String variable) {
        super(Foo.class, forVariable(variable), "dbo", "foo");
        addMetadata();
    }

    public QFoo(String variable, String schema, String table) {
        super(Foo.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QFoo(String variable, String schema) {
        super(Foo.class, forVariable(variable), schema, "foo");
        addMetadata();
    }

    public QFoo(Path<? extends Foo> path) {
        super(path.getType(), path.getMetadata(), "dbo", "foo");
        addMetadata();
    }

    public QFoo(PathMetadata metadata) {
        super(Foo.class, metadata, "dbo", "foo");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("id").withIndex(0));
    }

}

