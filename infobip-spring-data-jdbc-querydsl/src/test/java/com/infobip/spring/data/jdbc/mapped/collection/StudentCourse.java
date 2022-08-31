package com.infobip.spring.data.jdbc.mapped.collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

@AllArgsConstructor
@Data
public class StudentCourse {

    @Id
    Long id;

    AggregateReference<Course,Long> courseId;
}
