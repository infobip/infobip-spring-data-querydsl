package com.infobip.spring.data.jdbc.annotation.processor;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QEntityWithEmbeddedNullableEmbeddedClass is a Querydsl query type for EntityWithEmbeddedNullableEmbeddedClass
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QEntityWithEmbeddedNullableEmbeddedClass extends BeanPath<EntityWithEmbeddedNullableEmbeddedClass> {

    private static final long serialVersionUID = -2029368945L;

    public static final QEntityWithEmbeddedNullableEmbeddedClass entityWithEmbeddedNullableEmbeddedClass = new QEntityWithEmbeddedNullableEmbeddedClass("entityWithEmbeddedNullableEmbeddedClass");

    public final StringPath foo = createString("foo");

    public QEntityWithEmbeddedNullableEmbeddedClass(String variable) {
        super(EntityWithEmbeddedNullableEmbeddedClass.class, forVariable(variable));
    }

    public QEntityWithEmbeddedNullableEmbeddedClass(Path<? extends EntityWithEmbeddedNullableEmbeddedClass> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEntityWithEmbeddedNullableEmbeddedClass(PathMetadata metadata) {
        super(EntityWithEmbeddedNullableEmbeddedClass.class, metadata);
    }

}

