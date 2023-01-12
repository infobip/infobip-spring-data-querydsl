package com.infobip.spring.data.jdbc;

import java.time.Instant;

import lombok.With;
import org.springframework.data.annotation.Id;

public record Person(

    @With
    @Id
    Long id,

    String firstName,
    String lastName,
    Instant createdAt
) {

}
