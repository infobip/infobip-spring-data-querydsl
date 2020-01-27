package com.infobip.spring.data.jdbc.annotation.processor;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QPascalCaseFooBar is a Querydsl query type for QPascalCaseFooBar
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QPascalCaseFooBar extends com.querydsl.sql.RelationalPathBase<QPascalCaseFooBar> {

    private static final long serialVersionUID = 402043823;

    public static final QPascalCaseFooBar pascalCaseFooBar = new QPascalCaseFooBar("PascalCaseFooBar");

    public final StringPath bar = createString("bar");

    public final StringPath foo = createString("foo");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QPascalCaseFooBar(String variable) {
        super(QPascalCaseFooBar.class, forVariable(variable), null, "PascalCaseFooBar");
        addMetadata();
    }

    public QPascalCaseFooBar(String variable, String schema, String table) {
        super(QPascalCaseFooBar.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QPascalCaseFooBar(String variable, String schema) {
        super(QPascalCaseFooBar.class, forVariable(variable), schema, "PascalCaseFooBar");
        addMetadata();
    }

    public QPascalCaseFooBar(Path<? extends QPascalCaseFooBar> path) {
        super(path.getType(), path.getMetadata(), null, "PascalCaseFooBar");
        addMetadata();
    }

    public QPascalCaseFooBar(PathMetadata metadata) {
        super(QPascalCaseFooBar.class, metadata, null, "PascalCaseFooBar");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(bar, ColumnMetadata.named("Bar").withIndex(3));
        addMetadata(foo, ColumnMetadata.named("Foo").withIndex(2));
        addMetadata(id, ColumnMetadata.named("Id").withIndex(1));
    }

}

