package ru.ersted.module_1spirngmvc.repository.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.ersted.module_1spirngmvc.entity.Course;
import ru.ersted.module_1spirngmvc.entity.Student;
import ru.ersted.module_1spirngmvc.repository.StudentRepository;
import ru.ersted.module_1spirngmvc.repository.jdbc.mapper.CourseRowMapper;
import ru.ersted.module_1spirngmvc.repository.jdbc.mapper.StudentRowMapper;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class StudentJdbcRepository implements StudentRepository {

    private final NamedParameterJdbcTemplate template;

    @Override
    public Student save(Student student) {
        return student.getId() == null
                ? createStudent(student)
                : updateStudent(student);
    }

    private Student createStudent(Student student) {
        String sql = """
                    INSERT INTO student (name, email)
                    VALUES (:name, :email)
                    RETURNING id;
                """;

        MapSqlParameterSource ps = new MapSqlParameterSource()
                .addValue("name", student.getName())
                .addValue("email", student.getEmail());

        Long id = template.queryForObject(sql, ps, Long.class);
        student.setId(id);

        return student;
    }

    private Student updateStudent(Student student) {
        String sql = """
                    UPDATE student
                       SET name  = :name,
                           email = :email
                     WHERE id    = :id
                """;

        MapSqlParameterSource ps = new MapSqlParameterSource()
                .addValue("id", student.getId())
                .addValue("name", student.getName())
                .addValue("email", student.getEmail());

        template.update(sql, ps);

        return student;
    }

    @Override
    public Collection<Student> findAll() {
        String sqlStudents = """
                    SELECT id as student_id,
                           name as student_name,
                           email as student_email
                    FROM student
                    ORDER BY id
                """;

        List<Student> students = template.query(sqlStudents, new StudentRowMapper());

        if (!students.isEmpty()) {
            findStudentsCourses(students);
        }

        return students.isEmpty()
                ? Collections.emptyList()
                : students;
    }

    private void findStudentsCourses(List<Student> students) {
        List<Long> studentIds = students.stream()
                .map(Student::getId)
                .toList();

        String sqlCourses = """
                    SELECT
                        sc.student_id,
                        c.id    AS course_id,
                        c.title AS course_title,
                        t.id    AS teacher_id,
                        t.name  AS teacher_name
                    FROM students_courses sc
                    JOIN course   c ON c.id = sc.course_id
                    LEFT JOIN teacher t ON t.id = c.teacher_id
                    WHERE sc.student_id IN (:studentIds)
                    ORDER BY sc.student_id, c.id
                """;

        Map<Long, Student> studentById = students.stream()
                .collect(Collectors.toMap(
                        Student::getId,
                        Function.identity()));

        CourseRowMapper courseMapper = new CourseRowMapper();
        int[] rowNum = {0};

        template.query(sqlCourses,
                new MapSqlParameterSource("studentIds", studentIds),
                rs -> {
                    Student st = studentById.get(rs.getLong("student_id"));
                    if (st != null) {
                        Course c = courseMapper.mapRow(rs, rowNum[0]++);
                        st.getCourses().add(c);
                    }
                });
    }

    @Override
    public Optional<Student> findById(Long id) {
        String sqlStudent = """
                SELECT id as student_id,
                       name as student_name,
                       email as student_email
                FROM student WHERE id = :id
                """;

        Student student = template.queryForObject(sqlStudent, Map.of("id", id), new StudentRowMapper());

        if (student != null) {
            findStudentCourses(student);
        }

        return Optional.ofNullable(student);

    }

    private void findStudentCourses(Student student) {
        String sqlCourses = """
                    SELECT
                        c.id    AS course_id,
                        c.title AS course_title,
                        t.id    AS teacher_id,
                        t.name  AS teacher_name
                    FROM students_courses sc
                    JOIN course   c ON c.id = sc.course_id
                    LEFT JOIN teacher t ON t.id = c.teacher_id
                    WHERE sc.student_id = :id
                    ORDER BY c.id
                """;

        List<Course> courses = template.query(
                sqlCourses,
                Map.of("id", student.getId()),
                new CourseRowMapper());

        student.setCourses(new HashSet<>(courses));
    }

    @Override
    public void deleteById(Long id) {
        template.update("DELETE FROM student WHERE id = :id", Map.of("id", id));
    }

    @Override
    public void deleteAll() {
        template.update("DELETE FROM student", Map.of());
    }
}
