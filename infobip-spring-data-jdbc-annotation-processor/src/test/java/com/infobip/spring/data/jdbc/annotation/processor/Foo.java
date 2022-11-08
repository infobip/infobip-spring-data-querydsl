package com.infobip.spring.data.jdbc.annotation.processor;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Schema("dbo")
@Table("foo")
public class Foo {

    @Id
    @Column("id")
    private final Long id;

    public Foo(Long id) {
        this.id = id;
    }
}
