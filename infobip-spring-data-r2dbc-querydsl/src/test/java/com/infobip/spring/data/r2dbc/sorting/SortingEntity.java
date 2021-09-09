package com.infobip.spring.data.r2dbc.sorting;

import lombok.Value;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Value
@Table("sorting_entity")
public class SortingEntity {

    @With
    @Id
    @Column("id")
    private final Long id;

    @Column("foo_bar")
    private final String fooBar;
}
