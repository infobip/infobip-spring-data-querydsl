package com.infobip.spring.data.r2dbc.mysql;

import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("person")
public record Person(
    @With
    @Id
    Long id,

    String firstName,
    
    String lastName
) {

}
