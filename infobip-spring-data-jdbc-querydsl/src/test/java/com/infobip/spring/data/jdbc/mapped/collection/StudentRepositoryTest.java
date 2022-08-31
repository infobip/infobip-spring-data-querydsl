package com.infobip.spring.data.jdbc.mapped.collection;

import com.infobip.spring.data.jdbc.TestBase;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

@AllArgsConstructor
public class StudentRepositoryTest extends TestBase {

    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final StudentCourseRepository studentCourseRepository;

    @AfterEach
    void cleanUp() {
        studentCourseRepository.deleteAll();
    }

    @Test
    void shouldFindAll() {

        // given
        Course givenCourse = courseRepository.save(new Course(null, "givenCourseName"));
        Student givenStudent = new Student(null, "givenStudent", null);
        givenStudent.addItem(givenCourse);
        studentRepository.save(givenStudent);
    }

}
