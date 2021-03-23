package com.infobip.spring.data.jdbc.annotation.processor;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QSnakeCaseAndTransientType is a Querydsl query type for QSnakeCaseAndTransientType
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QSnakeCaseAndTransientType extends com.querydsl.sql.RelationalPathBase<QSnakeCaseAndTransientType> {

    private static final long serialVersionUID = -1281843753;

    public static final QSnakeCaseAndTransientType snakeCaseAndTransientType = new QSnakeCaseAndTransientType("SnakeCaseAndTransientType");

    public final NumberPath<Long> customerId = createNumber("customerId", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QSnakeCaseAndTransientType(String variable) {
        super(QSnakeCaseAndTransientType.class, forVariable(variable), "dbo", "customer_order");
        addMetadata();
    }

    public QSnakeCaseAndTransientType(String variable, String schema, String table) {
        super(QSnakeCaseAndTransientType.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QSnakeCaseAndTransientType(String variable, String schema) {
        super(QSnakeCaseAndTransientType.class, forVariable(variable), schema, "customer_order");
        addMetadata();
    }

    public QSnakeCaseAndTransientType(Path<? extends QSnakeCaseAndTransientType> path) {
        super(path.getType(), path.getMetadata(), "dbo", "customer_order");
        addMetadata();
    }

    public QSnakeCaseAndTransientType(PathMetadata metadata) {
        super(QSnakeCaseAndTransientType.class, metadata, "dbo", "customer_order");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(customerId, ColumnMetadata.named("customer_id").withIndex(2));
        addMetadata(id, ColumnMetadata.named("id").withIndex(1));
    }

}

