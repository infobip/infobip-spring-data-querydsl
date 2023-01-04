package com.infobip.spring.data.r2dbc;

import lombok.With;
import org.springframework.data.annotation.Id;

public record PersonSettings(
    @With @Id Long id,
    Long personId
) {

}
