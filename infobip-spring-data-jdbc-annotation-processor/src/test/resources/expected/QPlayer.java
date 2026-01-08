package com.infobip.spring.data.jdbc.annotation.processor;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPlayer is a Querydsl query type for Player
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QPlayer extends BeanPath<Player> {

    private static final long serialVersionUID = -86932820L;

    public static final QPlayer player = new QPlayer("player");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath name = createString("name");

    public QPlayer(String variable) {
        super(Player.class, forVariable(variable));
    }

    public QPlayer(Path<Player> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPlayer(PathMetadata metadata) {
        super(Player.class, metadata);
    }

}

