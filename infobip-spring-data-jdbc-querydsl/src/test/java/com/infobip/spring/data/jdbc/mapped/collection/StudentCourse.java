package com.infobip.spring.data.jdbc.mapped.collection;

import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

@Value
public class StudentCourse {

    @Id
    Long id;

    AggregateReference<Course,Long> courseId;

    Long studentId;

    public StudentCourse(AggregateReference<Course, Long> courseId, Long id, Long studentId) {
        this(id, courseId, studentId);
    }

    @PersistenceCreator
    public StudentCourse(Long id, AggregateReference<Course, Long> courseId, Long studentId) {
        this.id = id;
        this.courseId = courseId;
        this.studentId = studentId;
    }
}
