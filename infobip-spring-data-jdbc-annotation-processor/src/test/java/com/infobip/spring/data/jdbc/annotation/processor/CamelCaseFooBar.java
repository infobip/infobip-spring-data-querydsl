package com.infobip.spring.data.jdbc.annotation.processor;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Schema("dbo")
@Table("camelCaseFooBar")
public class CamelCaseFooBar {

    @Id
    @Column("id")
    private final Long id;

    @Column("foo")
    private final String foo;

    @Column("bar")
    private final String bar;

    public CamelCaseFooBar(Long id, String foo, String bar) {
        this.id = id;
        this.foo = foo;
        this.bar = bar;
    }
}
