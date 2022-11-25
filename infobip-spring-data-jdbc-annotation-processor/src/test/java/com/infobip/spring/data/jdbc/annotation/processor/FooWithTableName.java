package com.infobip.spring.data.jdbc.annotation.processor;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Schema("dbo")
@Table(name = "foo")
public class FooWithTableName {

    @Id
    @Column("id")
    private final Long id;

    public FooWithTableName(Long id) {
        this.id = id;
    }
}
