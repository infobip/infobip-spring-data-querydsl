package com.infobip.spring.data.jdbc.mapped.collection;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

public record StudentCourse(
    @Id
    Long id,

    AggregateReference<Course, Long> courseId,

    Long studentId
) {

}
