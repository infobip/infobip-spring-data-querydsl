package com.infobip.spring.data.jdbc.annotation.processor;

import lombok.Value;
import org.springframework.data.annotation.Id;

@Schema("foo")
@Value
public class EntityWithSchema {

    @Id
    private final Long id;
}
