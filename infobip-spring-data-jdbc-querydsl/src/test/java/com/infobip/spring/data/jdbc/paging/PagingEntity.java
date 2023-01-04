package com.infobip.spring.data.jdbc.paging;

import lombok.With;
import org.springframework.data.annotation.Id;

public record PagingEntity(
    @With
    @Id
    Long id,

    String value
) {

}
