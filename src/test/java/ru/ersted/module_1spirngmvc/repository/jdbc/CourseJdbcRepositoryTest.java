package ru.ersted.module_1spirngmvc.repository.jdbc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.ersted.module_1spirngmvc.entity.Course;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({CourseJdbcRepository.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CourseJdbcRepositoryTest {

    @Autowired
    CourseJdbcRepository repository;

    @Autowired
    NamedParameterJdbcTemplate template;

    @Test
    @DisplayName("Test save course")
    void save() {
        Course course = new Course(null, "Some title", null, null);
        repository.save(course);

        assertThat(course.getId()).isNotNull();
        assertThat(course.getTitle()).isEqualTo("Some title");
    }

    @Test
    @DisplayName("Test find all courses")
    void whenFindAll_thenFindAll() {
        Long studentId = template.queryForObject("INSERT INTO student(name, email) VALUES('Student name', 'example@example.example') RETURNING id", Map.of(), Long.class);
        Long teacherId = template.queryForObject("INSERT INTO teacher(name) VALUES('Some name') RETURNING id", Map.of(), Long.class);
        Long courseId = template.queryForObject("INSERT INTO course(title, teacher_id) VALUES('Some title',:teacherId) RETURNING id", Map.of("teacherId", teacherId), Long.class);
        template.update("INSERT INTO students_courses(student_id, course_id) VALUES(:studentId, :courseId)",
                Map.of("studentId", studentId, "courseId", courseId));

        List<Course> courses = repository.findAll();

        assertThat(courses).hasSize(1);

        assertThat(courses.get(0).getId()).isEqualTo(courseId);
        assertThat(courses.get(0).getTitle()).isEqualTo("Some title");

        assertThat(courses.get(0).getTeacher().getId()).isEqualTo(teacherId);
        assertThat(courses.get(0).getTeacher().getName()).isEqualTo("Some name");

        assertThat(courses.get(0).getStudents().size()).isEqualTo(1);
        assertThat(courses.get(0).getStudents().iterator().next().getId()).isEqualTo(studentId);
        assertThat(courses.get(0).getStudents().iterator().next().getName()).isEqualTo("Student name");
        assertThat(courses.get(0).getStudents().iterator().next().getEmail()).isEqualTo("example@example.example");

    }

    @Test
    @DisplayName("Test get by id course")
    void givenCourseId_whenFindById_thenReturnCourse() {
        Long teacherId = template.queryForObject("INSERT INTO teacher(name) VALUES('Some name') RETURNING id", Map.of(), Long.class);
        Long courseId = template.queryForObject("INSERT INTO course(title, teacher_id) VALUES('Some title',:teacherId) RETURNING id", Map.of("teacherId", teacherId), Long.class);

        Optional<Course> course = repository.findById(courseId);
        assertThat(course).isPresent();
        assertThat(course.get().getId()).isEqualTo(courseId);
        assertThat(course.get().getTitle()).isEqualTo("Some title");
        assertThat(course.get().getTeacher().getId()).isEqualTo(teacherId);
        assertThat(course.get().getTeacher().getName()).isEqualTo("Some name");
    }

    @Test
    @DisplayName("Test delete all courses")
    void whenDeleteAll_thenDeleteAll() {
        template.update("INSERT INTO course(title) VALUES('Some title')", Map.of());

        repository.deleteAll();

        List<Map<String, Object>> maps = template.queryForList("SELECT * FROM course", Map.of());
        assertThat(maps.size()).isEqualTo(0);
    }

}