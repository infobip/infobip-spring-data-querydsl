package com.infobip.spring.data.jdbc.annotation.processor;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QNest_NestedFoo is a Querydsl query type for NestedFoo
 */
@Generated("com.infobip.spring.data.jdbc.annotation.processor.CustomMetaDataSerializer")
public class QNest_NestedFoo extends com.querydsl.sql.RelationalPathBase<Nest.NestedFoo> {

    private static final long serialVersionUID = -2039752476;

    public static final QNest_NestedFoo nestedFoo = new QNest_NestedFoo("NestedFoo");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QNest_NestedFoo(String variable) {
        super(Nest.NestedFoo.class, forVariable(variable), "dbo", "foo");
        addMetadata();
    }

    public QNest_NestedFoo(String variable, String schema, String table) {
        super(Nest.NestedFoo.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QNest_NestedFoo(String variable, String schema) {
        super(Nest.NestedFoo.class, forVariable(variable), schema, "foo");
        addMetadata();
    }

    public QNest_NestedFoo(Path<? extends Nest.NestedFoo> path) {
        super(path.getType(), path.getMetadata(), "dbo", "foo");
        addMetadata();
    }

    public QNest_NestedFoo(PathMetadata metadata) {
        super(Nest.NestedFoo.class, metadata, "dbo", "foo");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("id").withIndex(0));
    }

}

