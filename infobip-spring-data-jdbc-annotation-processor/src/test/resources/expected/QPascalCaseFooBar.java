package com.infobip.spring.data.jdbc.annotation.processor;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QPascalCaseFooBar is a Querydsl query type for PascalCaseFooBar
 */
@Generated("com.infobip.spring.data.jdbc.annotation.processor.CustomMetaDataSerializer")
public class QPascalCaseFooBar extends com.querydsl.sql.RelationalPathBase<PascalCaseFooBar> {

    private static final long serialVersionUID = -1549108268;

    public static final QPascalCaseFooBar pascalCaseFooBar = new QPascalCaseFooBar("PascalCaseFooBar");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath foo = createString("foo");

    public final StringPath bar = createString("bar");

    public QPascalCaseFooBar(String variable) {
        super(PascalCaseFooBar.class, forVariable(variable), "dbo", "PascalCaseFooBar");
        addMetadata();
    }

    public QPascalCaseFooBar(String variable, String schema, String table) {
        super(PascalCaseFooBar.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QPascalCaseFooBar(String variable, String schema) {
        super(PascalCaseFooBar.class, forVariable(variable), schema, "PascalCaseFooBar");
        addMetadata();
    }

    public QPascalCaseFooBar(Path<? extends PascalCaseFooBar> path) {
        super(path.getType(), path.getMetadata(), "dbo", "PascalCaseFooBar");
        addMetadata();
    }

    public QPascalCaseFooBar(PathMetadata metadata) {
        super(PascalCaseFooBar.class, metadata, "dbo", "PascalCaseFooBar");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("Id").withIndex(0));
        addMetadata(foo, ColumnMetadata.named("Foo").withIndex(1));
        addMetadata(bar, ColumnMetadata.named("Bar").withIndex(2));
    }

}

