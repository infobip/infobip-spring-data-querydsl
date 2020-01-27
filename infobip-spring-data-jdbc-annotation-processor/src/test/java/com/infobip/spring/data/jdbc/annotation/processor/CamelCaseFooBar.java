package com.infobip.spring.data.jdbc.annotation.processor;

import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("camelCaseFooBar")
@Value
public class CamelCaseFooBar {

    @Id
    @Column("id")
    private final Long id;

    @Column("foo")
    private final String foo;

    @Column("bar")
    private final String bar;
}
