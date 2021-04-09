package com.infobip.spring.data.jdbc.annotation.processor;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QLowerUnderScoreColumnFooBar is a Querydsl query type for QLowerUnderScoreColumnFooBar
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QLowerUnderScoreColumnFooBar extends com.querydsl.sql.RelationalPathBase<QLowerUnderScoreColumnFooBar> {

    private static final long serialVersionUID = -1786890536;

    public static final QLowerUnderScoreColumnFooBar lowerUnderScoreColumnFooBar = new QLowerUnderScoreColumnFooBar("LowerUnderScoreColumnFooBar");

    public final StringPath fooBar = createString("fooBar");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QLowerUnderScoreColumnFooBar(String variable) {
        super(QLowerUnderScoreColumnFooBar.class, forVariable(variable), "dbo", "LowerUnderScoreColumnFooBar");
        addMetadata();
    }

    public QLowerUnderScoreColumnFooBar(String variable, String schema, String table) {
        super(QLowerUnderScoreColumnFooBar.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QLowerUnderScoreColumnFooBar(String variable, String schema) {
        super(QLowerUnderScoreColumnFooBar.class, forVariable(variable), schema, "LowerUnderScoreColumnFooBar");
        addMetadata();
    }

    public QLowerUnderScoreColumnFooBar(Path<? extends QLowerUnderScoreColumnFooBar> path) {
        super(path.getType(), path.getMetadata(), "dbo", "LowerUnderScoreColumnFooBar");
        addMetadata();
    }

    public QLowerUnderScoreColumnFooBar(PathMetadata metadata) {
        super(QLowerUnderScoreColumnFooBar.class, metadata, "dbo", "LowerUnderScoreColumnFooBar");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(fooBar, ColumnMetadata.named("foo_bar").withIndex(2));
        addMetadata(id, ColumnMetadata.named("id").withIndex(1));
    }

}

