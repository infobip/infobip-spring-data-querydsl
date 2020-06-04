package com.infobip.spring.data.jdbc.annotation.processor;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QCamelCaseFooBar is a Querydsl query type for QCamelCaseFooBar
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QCamelCaseFooBar extends com.querydsl.sql.RelationalPathBase<QCamelCaseFooBar> {

    private static final long serialVersionUID = -1949134995;

    public static final QCamelCaseFooBar camelCaseFooBar = new QCamelCaseFooBar("CamelCaseFooBar");

    public final StringPath bar = createString("bar");

    public final StringPath foo = createString("foo");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QCamelCaseFooBar(String variable) {
        super(QCamelCaseFooBar.class, forVariable(variable), "dbo", "camelCaseFooBar");
        addMetadata();
    }

    public QCamelCaseFooBar(String variable, String schema, String table) {
        super(QCamelCaseFooBar.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QCamelCaseFooBar(String variable, String schema) {
        super(QCamelCaseFooBar.class, forVariable(variable), schema, "camelCaseFooBar");
        addMetadata();
    }

    public QCamelCaseFooBar(Path<? extends QCamelCaseFooBar> path) {
        super(path.getType(), path.getMetadata(), "dbo", "camelCaseFooBar");
        addMetadata();
    }

    public QCamelCaseFooBar(PathMetadata metadata) {
        super(QCamelCaseFooBar.class, metadata, "dbo", "camelCaseFooBar");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(bar, ColumnMetadata.named("bar").withIndex(3));
        addMetadata(foo, ColumnMetadata.named("foo").withIndex(2));
        addMetadata(id, ColumnMetadata.named("id").withIndex(1));
    }

}

