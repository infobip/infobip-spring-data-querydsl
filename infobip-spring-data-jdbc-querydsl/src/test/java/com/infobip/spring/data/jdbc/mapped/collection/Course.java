package com.infobip.spring.data.jdbc.mapped.collection;

import lombok.Value;
import org.springframework.data.annotation.Id;

@Value
public class Course {

    @Id
    Long id;

    String name;
}
