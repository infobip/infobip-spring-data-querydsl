package com.infobip.spring.data.jdbc.mapped.collection;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.MappedCollection;

public class Student {

    @Id
    Long id;

    String name;

    @MappedCollection(idColumn = "StudentId", keyColumn = "CourseId")
    Set<StudentCourse> courses = new HashSet<>();

    void addItem(Course course) {
        StudentCourse studentCourse = new StudentCourse(null, AggregateReference.to(course.getId()));
        courses.add(studentCourse);
    }

}
