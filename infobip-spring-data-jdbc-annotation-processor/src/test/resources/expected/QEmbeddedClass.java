package com.infobip.spring.data.jdbc.annotation.processor;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import jakarta.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QEmbeddedClass is a Querydsl query type for EmbeddedClass
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QEmbeddedClass extends BeanPath<EmbeddedClass> {

    private static final long serialVersionUID = 505574115L;

    public static final QEmbeddedClass embeddedClass = new QEmbeddedClass("embeddedClass");

    public final StringPath foo = createString("foo");

    public QEmbeddedClass(String variable) {
        super(EmbeddedClass.class, forVariable(variable));
    }

    public QEmbeddedClass(Path<? extends EmbeddedClass> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEmbeddedClass(PathMetadata metadata) {
        super(EmbeddedClass.class, metadata);
    }

}

