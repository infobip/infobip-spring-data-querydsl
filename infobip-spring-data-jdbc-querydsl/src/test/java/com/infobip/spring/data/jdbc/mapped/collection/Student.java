package com.infobip.spring.data.jdbc.mapped.collection;

import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.MappedCollection;

public record Student(
    @Id
    Long id,

    String name,

    @MappedCollection(idColumn = "StudentId", keyColumn = "CourseId")
    Set<StudentCourse> courses
) {

    void addItem(Course course) {
        var studentCourse = new StudentCourse(null, AggregateReference.to(course.id()), null);
        courses.add(studentCourse);
    }

}
