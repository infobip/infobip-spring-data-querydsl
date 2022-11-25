package com.infobip.spring.data.jdbc.annotation.processor;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QSnakeCaseAndTransientType is a Querydsl query type for SnakeCaseAndTransientType
 */
@Generated("com.infobip.spring.data.jdbc.annotation.processor.CustomMetaDataSerializer")
public class QSnakeCaseAndTransientType extends com.querydsl.sql.RelationalPathBase<SnakeCaseAndTransientType> {

    private static final long serialVersionUID = 1346580306;

    public static final QSnakeCaseAndTransientType snakeCaseAndTransientType = new QSnakeCaseAndTransientType("SnakeCaseAndTransientType");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> customerId = createNumber("customerId", Long.class);

    public QSnakeCaseAndTransientType(String variable) {
        super(SnakeCaseAndTransientType.class, forVariable(variable), "dbo", "customer_order");
        addMetadata();
    }

    public QSnakeCaseAndTransientType(String variable, String schema, String table) {
        super(SnakeCaseAndTransientType.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QSnakeCaseAndTransientType(String variable, String schema) {
        super(SnakeCaseAndTransientType.class, forVariable(variable), schema, "customer_order");
        addMetadata();
    }

    public QSnakeCaseAndTransientType(Path<? extends SnakeCaseAndTransientType> path) {
        super(path.getType(), path.getMetadata(), "dbo", "customer_order");
        addMetadata();
    }

    public QSnakeCaseAndTransientType(PathMetadata metadata) {
        super(SnakeCaseAndTransientType.class, metadata, "dbo", "customer_order");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("id").withIndex(0));
        addMetadata(customerId, ColumnMetadata.named("customer_id").withIndex(1));
    }

}

