package com.infobip.spring.data.jdbc.mapped.collection;

import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

@Value
public class StudentCourse {

    @Id
    Long id;

    AggregateReference<Course,Long> courseId;

    Long studentId;
}
