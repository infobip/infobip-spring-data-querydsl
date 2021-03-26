package com.infobip.spring.data.r2dbc.paging;

import lombok.Value;
import lombok.With;
import org.springframework.data.annotation.Id;

@Value
public class PagingEntity {

    @With
    @Id
    private final Long id;
    private final String value;
}
