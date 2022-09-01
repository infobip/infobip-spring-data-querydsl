package com.infobip.spring.data.jdbc.mapped.collection;

import java.util.Set;

import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.MappedCollection;

@Value
public class Student {

    @Id
    Long id;

    String name;

    @MappedCollection(idColumn = "StudentId", keyColumn = "CourseId")
    Set<StudentCourse> courses;

    void addItem(Course course) {
        StudentCourse studentCourse = new StudentCourse(null, AggregateReference.to(course.getId()), null);
        courses.add(studentCourse);
    }

}
