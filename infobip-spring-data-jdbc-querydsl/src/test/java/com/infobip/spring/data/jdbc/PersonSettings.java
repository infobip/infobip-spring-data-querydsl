package com.infobip.spring.data.jdbc;

import lombok.With;
import org.springframework.data.annotation.Id;

public record PersonSettings(
    @With
    @Id
    Long id,

    Long personId
) {

}
