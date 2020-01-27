package com.infobip.spring.data.jdbc.annotation.processor;

import lombok.Value;
import org.springframework.data.annotation.Id;

@Value
public class PascalCaseFooBar {

    @Id
    private final Long id;
    private final String foo;
    private final String bar;
}
