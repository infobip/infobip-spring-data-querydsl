package com.infobip.spring.data.r2dbc.sorting;

import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("sorting_entity")
public record SortingEntity(
    @With
    @Id
    @Column("id")
    Long id,

    @Column("foo_bar")
    String fooBar

) {

}
