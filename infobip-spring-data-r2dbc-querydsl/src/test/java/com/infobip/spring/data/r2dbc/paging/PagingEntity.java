package com.infobip.spring.data.r2dbc.paging;

import lombok.With;
import org.springframework.data.annotation.Id;

public record PagingEntity(
    @With @Id Long id,
    String value
) {

}
