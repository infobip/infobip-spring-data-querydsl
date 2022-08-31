package com.infobip.spring.data.jdbc.mapped.collection;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.MappedCollection;

@Value
public class Student {

    @Id
    Long id;

    String name;

    @MappedCollection(idColumn = "StudentId", keyColumn = "CourseId")
    Set<StudentCourse> courses;

    public Student(Long id, String name, List<StudentCourse> courses) {
        this.id = id;
        this.name = name;
        this.courses = Optional.ofNullable(courses).map(HashSet::new).orElse(null);
    }

    @PersistenceCreator
    public Student(Long id, String name, Set<StudentCourse> courses) {
        this.id = id;
        this.name = name;
        this.courses = Optional.ofNullable(courses).orElseGet(HashSet::new);
    }

    void addItem(Course course) {
        StudentCourse studentCourse = new StudentCourse(null, AggregateReference.to(course.getId()), null);
        courses.add(studentCourse);
    }

}
