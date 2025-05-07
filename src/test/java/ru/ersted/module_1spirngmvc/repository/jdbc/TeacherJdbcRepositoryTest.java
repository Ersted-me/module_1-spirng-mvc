package ru.ersted.module_1spirngmvc.repository.jdbc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.ersted.module_1spirngmvc.config.DatabaseConfig;
import ru.ersted.module_1spirngmvc.entity.Course;
import ru.ersted.module_1spirngmvc.entity.Department;
import ru.ersted.module_1spirngmvc.entity.Teacher;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import({DatabaseConfig.class, TeacherJdbcRepository.class})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TeacherJdbcRepositoryTest {

    @Autowired
    NamedParameterJdbcTemplate template;

    @Autowired
    TeacherJdbcRepository repository;


    @Test
    @DisplayName("save() persists and returns generated id")
    void givenNewTeacher_whenSave_thenPersisted() {

        Long depId = template.queryForObject(
                "INSERT INTO department(name) VALUES('Physics') RETURNING id",
                Map.of(), Long.class);

        Department dep = new Department(depId, "Physics", null);

        Teacher toSave = new Teacher(null, "Dr. Brown", dep, null);

        Teacher saved = repository.save(toSave);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Dr. Brown");
        assertThat(saved.getDepartment()).isNotNull();
        assertThat(saved.getDepartment().getId()).isEqualTo(depId);
    }

    @Test
    @DisplayName("findAll() returns every teacher in DB")
    void whenFindAll_thenAllTeachersReturned() {

        Long depId = template.queryForObject(
                "INSERT INTO department(name) VALUES('IT') RETURNING id",
                Map.of(), Long.class);

        Long teacherId = template.queryForObject(
                "INSERT INTO teacher(name, department_id) VALUES('Alice', :dep) RETURNING id",
                Map.of("dep", depId), Long.class);


        Collection<Teacher> teachers = repository.findAll(PageRequest.of(0, 20)).getContent();

        assertThat(teachers).hasSize(1);

        Teacher t = teachers.iterator().next();
        assertThat(t.getId()).isEqualTo(teacherId);
        assertThat(t.getName()).isEqualTo("Alice");
        assertThat(t.getDepartment()).isNotNull();
        assertThat(t.getDepartment().getId()).isEqualTo(depId);

        assertThat(t.getCourses()).isNullOrEmpty();
    }

    @Test
    @DisplayName("findById() returns teacher with all courses")
    void givenTeacherId_whenFindById_thenTeacherWithCourses() {

        Long depId = template.queryForObject(
                "INSERT INTO department(name) VALUES('Math') RETURNING id",
                Map.of(), Long.class);

        Long teacherId = template.queryForObject(
                "INSERT INTO teacher(name, department_id) VALUES('Bob', :dep) RETURNING id",
                Map.of("dep", depId), Long.class);

        Long courseId = template.queryForObject(
                "INSERT INTO course(title, teacher_id) VALUES('Linear Algebra', :t) RETURNING id",
                Map.of("t", teacherId), Long.class);

        Optional<Teacher> optionalTeacher = repository.findById(teacherId);

        assertThat(optionalTeacher).isPresent();

        Teacher teacher = optionalTeacher.get();
        assertThat(teacher.getId()).isEqualTo(teacherId);
        assertThat(teacher.getName()).isEqualTo("Bob");

        assertThat(teacher.getDepartment()).isNotNull();
        assertThat(teacher.getDepartment().getId()).isEqualTo(depId);
        assertThat(teacher.getDepartment().getName()).isEqualTo("Math");

        Set<Course> courses = teacher.getCourses();
        assertThat(courses).hasSize(1);

        Course c = courses.iterator().next();
        assertThat(c.getId()).isEqualTo(courseId);
        assertThat(c.getTitle()).isEqualTo("Linear Algebra");
        assertThat(c.getTeacher()).isNotNull();
        assertThat(c.getTeacher().getId()).isEqualTo(teacherId);
    }

    @Test
    @DisplayName("deleteAll() wipes the teacher table")
    void whenDeleteAll_thenTableEmptied() {

        template.update("INSERT INTO teacher(name) VALUES('Temp')", Map.of());

        repository.deleteAll();

        Integer cnt = template.queryForObject(
                "SELECT COUNT(*) FROM teacher", Map.of(), Integer.class);

        assertThat(cnt).isZero();
    }

}