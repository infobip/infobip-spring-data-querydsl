package com.infobip.spring.data.jdbc.mapped.collection;

import static com.infobip.spring.data.jdbc.mapped.collection.QStudentCourse.studentCourse;
import static org.assertj.core.api.BDDAssertions.then;

import java.util.HashSet;

import com.infobip.spring.data.jdbc.TestBase;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

@AllArgsConstructor
public class StudentCourseRepositoryTest extends TestBase {

    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final StudentCourseRepository studentCourseRepository;

    @Test
    void shouldQueryByAggregateReferenceId() {

        // given
        var givenCourse = courseRepository.save(new Course(null, "givenCourseName"));
        var givenStudent = new Student(null, "givenStudent", new HashSet<>());
        givenStudent.addItem(givenCourse);
        studentRepository.save(givenStudent);

        // when
        var actual = studentCourseRepository.findAll(studentCourse.courseId.eq(givenCourse.id()));

        // then
        then(actual).singleElement()
                    .satisfies(entity -> then(entity.courseId())
                            .isEqualTo(AggregateReference.to(givenCourse.id())));
    }

    @Test
    void shouldFetchEntityProjectionWithAggregateReference() {

        // given
        var givenCourse = courseRepository.save(new Course(null, "givenCourseName"));
        var givenStudent = new Student(null, "givenStudent", new HashSet<>());
        givenStudent.addItem(givenCourse);
        studentRepository.save(givenStudent);

        // when
        var actual = studentCourseRepository.query(query -> query.select(studentCourseRepository.entityProjection())
                                                                 .from(studentCourse)
                                                                 .fetch());

        // then
        then(actual).singleElement()
                    .satisfies(entity -> then(entity.courseId())
                            .isEqualTo(AggregateReference.to(givenCourse.id())));
    }
}
