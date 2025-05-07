package ru.ersted.module_1spirngmvc.repository.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.ersted.module_1spirngmvc.entity.Course;
import ru.ersted.module_1spirngmvc.entity.Student;
import ru.ersted.module_1spirngmvc.repository.CourseRepository;
import ru.ersted.module_1spirngmvc.repository.jdbc.mapper.CourseRowMapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class CourseJdbcRepository implements CourseRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Course save(Course course) {
        String sql = "INSERT INTO course(title, teacher_id) VALUES (:title, :teacherId) RETURNING id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("title", course.getTitle());
        params.addValue("teacherId", course.getTeacher() != null
                ? course.getTeacher().getId()
                : null
        );

        Long courseId = jdbcTemplate.queryForObject(sql, params, Long.class);

        course.setId(courseId);
        return course;
    }

    @Override
    public Slice<Course> findAll(Pageable pageable) {
        String sqlCourses = """
                SELECT
                    c.id          AS course_id,
                    c.title       AS course_title,
                    t.id          AS teacher_id,
                    t.name        AS teacher_name
                FROM course c
                LEFT JOIN teacher t ON t.id = c.teacher_id
                ORDER BY c.id
                LIMIT :limit OFFSET :offset
                """;

        int pageSize = pageable.getPageSize();
        long offset = pageable.getOffset();

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("limit", pageSize)
                .addValue("offset", offset);

        Slice<Course> pageableCourses = getPageableCourses(pageable, sqlCourses, pageSize, params);

        if(!pageableCourses.getContent().isEmpty()) {
            findCoursesStudents(pageableCourses.getContent());
        }

        return pageableCourses;
    }

    private void findCoursesStudents(List<Course> courses) {
        List<Long> coursesIds = courses.stream()
                .map(Course::getId)
                .toList();

        Map<Long, Course> courseById = courses.stream()
                .collect(Collectors.toMap(
                        Course::getId,
                        Function.identity()));

        String sqlStudents = """
                    SELECT
                        sc.course_id,
                        s.id     AS student_id,
                        s.name   AS student_name,
                        s.email  AS student_email
                    FROM students_courses sc
                    JOIN student s ON s.id = sc.student_id
                    WHERE sc.course_id IN (:ids)
                    ORDER BY sc.course_id
                """;

        jdbcTemplate.query(sqlStudents,
                new MapSqlParameterSource("ids", coursesIds),
                rs -> {
                    Course course = courseById.get(rs.getLong("course_id"));
                    if (course != null) {                 // безопасность
                        Student st = new Student();
                        st.setId(rs.getLong("student_id"));
                        st.setName(rs.getString("student_name"));
                        st.setEmail(rs.getString("student_email"));
                        course.getStudents().add(st);
                    }
                });
    }

    @Override
    public Optional<Course> findById(Long id) {
        String sql = """
                SELECT 
                    c.id as course_id,
                    c.title as course_title,
                    t.id as teacher_id,
                    t.name as teacher_name,
                    t.department_id as department_id
                FROM course c
                    join teacher t on t.id = c.teacher_id
                WHERE c.id = :id
                """;

        Course course = jdbcTemplate.queryForObject(sql, Map.of("id", id), new CourseRowMapper());

        return Optional.ofNullable(course);
    }

    @Override
    public Slice<Course> findStudentCourses(Long studentId, Pageable pageable) {
        String sql = """
                    SELECT
                        c.id    AS course_id,
                        c.title AS course_title,
                        t.id    AS teacher_id,
                        t.name  AS teacher_name
                    FROM students_courses sc
                    JOIN course   c ON c.id = sc.course_id
                    LEFT JOIN teacher t ON t.id = c.teacher_id
                    WHERE sc.student_id = :studentId
                    ORDER BY c.id
                    LIMIT :limit OFFSET :offset
                """;

        int pageSize = pageable.getPageSize();
        long offset = pageable.getOffset();

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("limit", pageSize)
                .addValue("offset", offset)
                .addValue("studentId", studentId);

        return getPageableCourses(pageable, sql, pageSize, params);
    }


    private Slice<Course> getPageableCourses(Pageable pageable, String sql, int pageSize, MapSqlParameterSource params) {
        List<Course> rows = jdbcTemplate.query(sql, params, new CourseRowMapper());

        boolean hasNext = rows.size() > pageSize;
        if (hasNext) {
            rows = rows.subList(0, pageSize);
        }

        return new SliceImpl<>(rows, pageable, hasNext);
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM course";
        jdbcTemplate.update(sql, Map.of());
    }

}
