package com.infobip.spring.data.jdbc.annotation.processor;

import org.springframework.data.annotation.Id;

@Schema("dbo")
public class LowerUnderScoreTableFooBar {

    @Id
    private final Long id;
    private final String fooBar;

    public LowerUnderScoreTableFooBar(Long id, String fooBar) {
        this.id = id;
        this.fooBar = fooBar;
    }
}
