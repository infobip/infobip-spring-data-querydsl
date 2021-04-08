package com.infobip.spring.data.jdbc.annotation.processor;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QLowerUnderScoreTableFooBar is a Querydsl query type for QLowerUnderScoreTableFooBar
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QLowerUnderScoreTableFooBar extends com.querydsl.sql.RelationalPathBase<QLowerUnderScoreTableFooBar> {

    private static final long serialVersionUID = 548443590;

    public static final QLowerUnderScoreTableFooBar lowerUnderScoreTableFooBar = new QLowerUnderScoreTableFooBar("LowerUnderScoreTableFooBar");

    public final StringPath fooBar = createString("fooBar");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QLowerUnderScoreTableFooBar(String variable) {
        super(QLowerUnderScoreTableFooBar.class, forVariable(variable), "dbo", "lower_under_score_table_foo_bar");
        addMetadata();
    }

    public QLowerUnderScoreTableFooBar(String variable, String schema, String table) {
        super(QLowerUnderScoreTableFooBar.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QLowerUnderScoreTableFooBar(String variable, String schema) {
        super(QLowerUnderScoreTableFooBar.class, forVariable(variable), schema, "lower_under_score_table_foo_bar");
        addMetadata();
    }

    public QLowerUnderScoreTableFooBar(Path<? extends QLowerUnderScoreTableFooBar> path) {
        super(path.getType(), path.getMetadata(), "dbo", "lower_under_score_table_foo_bar");
        addMetadata();
    }

    public QLowerUnderScoreTableFooBar(PathMetadata metadata) {
        super(QLowerUnderScoreTableFooBar.class, metadata, "dbo", "lower_under_score_table_foo_bar");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(fooBar, ColumnMetadata.named("FooBar").withIndex(2));
        addMetadata(id, ColumnMetadata.named("Id").withIndex(1));
    }

}

