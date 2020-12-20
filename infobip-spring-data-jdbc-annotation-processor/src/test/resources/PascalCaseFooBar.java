package com.infobip.spring.data.jdbc.annotation.processor;

import lombok.Value;
import org.springframework.data.annotation.Id;

@DefaultSchema("dbo")
public class PascalCaseFooBar {

    @Id
    private final Long id;
    private final String foo;
    private final String bar;

    public PascalCaseFooBar(Long id, String foo, String bar) {
        this.id = id;
        this.foo = foo;
        this.bar = bar;
    }
}
