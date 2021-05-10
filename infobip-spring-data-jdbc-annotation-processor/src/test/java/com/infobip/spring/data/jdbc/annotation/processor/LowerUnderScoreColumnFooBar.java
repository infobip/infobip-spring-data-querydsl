package com.infobip.spring.data.jdbc.annotation.processor;

import org.springframework.data.annotation.Id;

@Schema("dbo")
public class LowerUnderScoreColumnFooBar {

    @Id
    private final Long id;
    private final String fooBar;

    public LowerUnderScoreColumnFooBar(Long id, String fooBar) {
        this.id = id;
        this.fooBar = fooBar;
    }
}
