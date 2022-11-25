package com.infobip.spring.data.jdbc.annotation.processor;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QFooWithTableName is a Querydsl query type for FooWithTableName
 */
@Generated("com.infobip.spring.data.jdbc.annotation.processor.CustomMetaDataSerializer")
public class QFooWithTableName extends com.querydsl.sql.RelationalPathBase<FooWithTableName> {

    private static final long serialVersionUID = 132611128;

    public static final QFooWithTableName fooWithTableName = new QFooWithTableName("FooWithTableName");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QFooWithTableName(String variable) {
        super(FooWithTableName.class, forVariable(variable), "dbo", "foo");
        addMetadata();
    }

    public QFooWithTableName(String variable, String schema, String table) {
        super(FooWithTableName.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QFooWithTableName(String variable, String schema) {
        super(FooWithTableName.class, forVariable(variable), schema, "foo");
        addMetadata();
    }

    public QFooWithTableName(Path<? extends FooWithTableName> path) {
        super(path.getType(), path.getMetadata(), "dbo", "foo");
        addMetadata();
    }

    public QFooWithTableName(PathMetadata metadata) {
        super(FooWithTableName.class, metadata, "dbo", "foo");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("id").withIndex(0));
    }

}

