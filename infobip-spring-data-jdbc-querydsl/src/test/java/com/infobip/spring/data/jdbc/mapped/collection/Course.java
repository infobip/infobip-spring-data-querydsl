package com.infobip.spring.data.jdbc.mapped.collection;

import org.springframework.data.annotation.Id;

public record Course(
    @Id
    Long id,
    String name
) {

}
