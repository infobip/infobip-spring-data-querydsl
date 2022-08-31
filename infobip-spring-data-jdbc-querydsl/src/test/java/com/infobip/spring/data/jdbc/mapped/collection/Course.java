package com.infobip.spring.data.jdbc.mapped.collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;

@AllArgsConstructor
@Data
public class Course {

    @Id
    Long id;

    String name;
}
