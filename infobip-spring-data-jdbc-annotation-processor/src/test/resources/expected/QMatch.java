package com.infobip.spring.data.jdbc.annotation.processor;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QMatch is a Querydsl query type for Match
 */
@Generated("com.infobip.spring.data.jdbc.annotation.processor.CustomMetaDataSerializer")
public class QMatch extends com.querydsl.sql.RelationalPathBase<Match> {

    private static final long serialVersionUID = -1945547622;

    public static final QMatch match = new QMatch("Match");

    public final NumberPath<Integer> matchId = createNumber("matchId", Integer.class);

    public final NumberPath<Integer> player1_Id = createNumber("player1_Id", Integer.class);

    public final StringPath player1_Name = createString("player1_Name");

    public final NumberPath<Integer> player2_Id = createNumber("player2_Id", Integer.class);

    public final StringPath player2_Name = createString("player2_Name");

    public QMatch(String variable) {
        super(Match.class, forVariable(variable), null, "matches");
        addMetadata();
    }

    public QMatch(String variable, String schema, String table) {
        super(Match.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QMatch(String variable, String schema) {
        super(Match.class, forVariable(variable), schema, "matches");
        addMetadata();
    }

    public QMatch(Path<Match> path) {
        super(path.getType(), path.getMetadata(), null, "matches");
        addMetadata();
    }

    public QMatch(PathMetadata metadata) {
        super(Match.class, metadata, null, "matches");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(matchId, ColumnMetadata.named("MatchId").withIndex(0));
        addMetadata(player1_Id, ColumnMetadata.named("player1_Id").withIndex(1));
        addMetadata(player1_Name, ColumnMetadata.named("player1_Name").withIndex(2));
        addMetadata(player2_Id, ColumnMetadata.named("player2_Id").withIndex(3));
        addMetadata(player2_Name, ColumnMetadata.named("player2_Name").withIndex(4));
    }

}