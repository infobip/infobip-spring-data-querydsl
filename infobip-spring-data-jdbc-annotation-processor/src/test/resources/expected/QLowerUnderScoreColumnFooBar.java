package com.infobip.spring.data.jdbc.annotation.processor;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QLowerUnderScoreColumnFooBar is a Querydsl query type for LowerUnderScoreColumnFooBar
 */
@Generated("com.infobip.spring.data.jdbc.annotation.processor.CustomMetaDataSerializer")
public class QLowerUnderScoreColumnFooBar extends com.querydsl.sql.RelationalPathBase<LowerUnderScoreColumnFooBar> {

    private static final long serialVersionUID = -1312139885;

    public static final QLowerUnderScoreColumnFooBar lowerUnderScoreColumnFooBar = new QLowerUnderScoreColumnFooBar("LowerUnderScoreColumnFooBar");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath fooBar = createString("fooBar");

    public QLowerUnderScoreColumnFooBar(String variable) {
        super(LowerUnderScoreColumnFooBar.class, forVariable(variable), "dbo", "LowerUnderScoreColumnFooBar");
        addMetadata();
    }

    public QLowerUnderScoreColumnFooBar(String variable, String schema, String table) {
        super(LowerUnderScoreColumnFooBar.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QLowerUnderScoreColumnFooBar(String variable, String schema) {
        super(LowerUnderScoreColumnFooBar.class, forVariable(variable), schema, "LowerUnderScoreColumnFooBar");
        addMetadata();
    }

    public QLowerUnderScoreColumnFooBar(Path<? extends LowerUnderScoreColumnFooBar> path) {
        super(path.getType(), path.getMetadata(), "dbo", "LowerUnderScoreColumnFooBar");
        addMetadata();
    }

    public QLowerUnderScoreColumnFooBar(PathMetadata metadata) {
        super(LowerUnderScoreColumnFooBar.class, metadata, "dbo", "LowerUnderScoreColumnFooBar");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("id").withIndex(0));
        addMetadata(fooBar, ColumnMetadata.named("foo_bar").withIndex(1));
    }

}
