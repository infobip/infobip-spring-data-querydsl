package com.infobip.spring.data.jdbc.annotation.processor;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QEntityWithEmbeddedEmptyEmbeddedClass is a Querydsl query type for EntityWithEmbeddedEmptyEmbeddedClass
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QEntityWithEmbeddedEmptyEmbeddedClass extends BeanPath<EntityWithEmbeddedEmptyEmbeddedClass> {

    private static final long serialVersionUID = 104763807L;

    public static final QEntityWithEmbeddedEmptyEmbeddedClass entityWithEmbeddedEmptyEmbeddedClass = new QEntityWithEmbeddedEmptyEmbeddedClass("entityWithEmbeddedEmptyEmbeddedClass");

    public final StringPath foo = createString("foo");

    public QEntityWithEmbeddedEmptyEmbeddedClass(String variable) {
        super(EntityWithEmbeddedEmptyEmbeddedClass.class, forVariable(variable));
    }

    public QEntityWithEmbeddedEmptyEmbeddedClass(Path<? extends EntityWithEmbeddedEmptyEmbeddedClass> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEntityWithEmbeddedEmptyEmbeddedClass(PathMetadata metadata) {
        super(EntityWithEmbeddedEmptyEmbeddedClass.class, metadata);
    }

}

