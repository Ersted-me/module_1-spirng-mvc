package ru.ersted.module_1spirngmvc.repository.jdbc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.ersted.module_1spirngmvc.entity.Course;
import ru.ersted.module_1spirngmvc.entity.Student;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({StudentJdbcRepository.class})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class StudentJdbcRepositoryTest {

    @Autowired
    StudentJdbcRepository repository;

    @Autowired
    NamedParameterJdbcTemplate template;

    @Test
    @DisplayName("Test save new student")
    void givenNewStudent_whenSaveNewStudent_thenStudentPersisted() {
        Student newStudent = new Student(null, "Some name", "example@example.example", null);

        Student student = repository.save(newStudent);

        assertThat(student).isNotNull();
        assertThat(student.getId()).isNotNull();
        assertThat(student.getName()).isEqualTo("Some name");
        assertThat(student.getEmail()).isEqualTo("example@example.example");
    }

    @Test
    @DisplayName("Test find all students")
    void whenFindAll_thenListOfStudents() {
        Long studentId = template.queryForObject("INSERT INTO student(name, email) VALUES('Student name', 'example@example.example') RETURNING id", Map.of(), Long.class);
        Long teacherId = template.queryForObject("INSERT INTO teacher(name) VALUES('Some name') RETURNING id", Map.of(), Long.class);
        Long courseId = template.queryForObject("INSERT INTO course(title, teacher_id) VALUES('Some title',:teacherId) RETURNING id", Map.of("teacherId", teacherId), Long.class);
        template.update("INSERT INTO students_courses(student_id, course_id) VALUES(:studentId, :courseId)",
                Map.of("studentId", studentId, "courseId", courseId));

        Collection<Student> students = repository.findAll();

        assertThat(students).hasSize(1);

        Student student = students.iterator().next();
        assertThat(student.getId()).isEqualTo(studentId);
        assertThat(student.getName()).isEqualTo("Student name");
        assertThat(student.getEmail()).isEqualTo("example@example.example");

        Course course = student.getCourses().iterator().next();
        assertThat(course.getId()).isEqualTo(courseId);
        assertThat(course.getTitle()).isEqualTo("Some title");
        assertThat(course.getTeacher().getId()).isEqualTo(teacherId);
        assertThat(course.getTeacher().getName()).isEqualTo("Some name");
    }

    @Test
    @DisplayName("Test find student by id")
    void givenStudentId_whenFindById_thenStudentReturned() {
        Long studentId = template.queryForObject("INSERT INTO student(name, email) VALUES('Student name', 'example@example.example') RETURNING id", Map.of(), Long.class);
        Long teacherId = template.queryForObject("INSERT INTO teacher(name) VALUES('Some name') RETURNING id", Map.of(), Long.class);
        Long courseId = template.queryForObject("INSERT INTO course(title, teacher_id) VALUES('Some title',:teacherId) RETURNING id", Map.of("teacherId", teacherId), Long.class);
        template.update("INSERT INTO students_courses(student_id, course_id) VALUES(:studentId, :courseId)",
                Map.of("studentId", studentId, "courseId", courseId));

        Optional<Student> optionalStudent = repository.findById(studentId);

        assertThat(optionalStudent).isPresent();

        Student student = optionalStudent.get();
        assertThat(student.getId()).isEqualTo(studentId);
        assertThat(student.getName()).isEqualTo("Student name");
        assertThat(student.getEmail()).isEqualTo("example@example.example");

        Course course = student.getCourses().iterator().next();
        assertThat(course.getId()).isEqualTo(courseId);
        assertThat(course.getTitle()).isEqualTo("Some title");
        assertThat(course.getTeacher().getId()).isEqualTo(teacherId);
        assertThat(course.getTeacher().getName()).isEqualTo("Some name");
    }

    @Test
    @DisplayName("Test delete student by id")
    void givenStudentId_whenDeleteById_thenStudentDeleted() {
        Long studentId = template.queryForObject("INSERT INTO student(name, email) VALUES('Student name', 'example@example.example') RETURNING id", Map.of(), Long.class);

        repository.deleteById(studentId);

        Integer count = template.queryForObject("SELECT COUNT(*) FROM student WHERE id = :id", Map.of("id", studentId), Integer.class);

        assertThat(count).isZero();
    }

    @Test
    @DisplayName("Test delete all students")
    void whenDeleteAll_thenStudentsDeleted() {
        template.update("INSERT INTO student(name, email) VALUES('Student name', 'example@example.example')", Map.of());

        repository.deleteAll();

        Integer count = template.queryForObject("SELECT COUNT(*) FROM student", Map.of(), Integer.class);

        assertThat(count).isZero();
    }

}