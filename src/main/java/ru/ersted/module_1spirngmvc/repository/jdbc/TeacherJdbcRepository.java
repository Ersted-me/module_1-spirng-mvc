package ru.ersted.module_1spirngmvc.repository.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.ersted.module_1spirngmvc.entity.Course;
import ru.ersted.module_1spirngmvc.entity.Teacher;
import ru.ersted.module_1spirngmvc.repository.TeacherRepository;
import ru.ersted.module_1spirngmvc.repository.jdbc.mapper.CourseRowMapper;
import ru.ersted.module_1spirngmvc.repository.jdbc.mapper.TeacherRowMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TeacherJdbcRepository implements TeacherRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Teacher save(Teacher teacher) {
        return teacher.getId() == null
                ? createTeacher(teacher)
                : updateTeacher(teacher);
    }

    private Teacher createTeacher(Teacher teacher) {
        String sql = """
                    INSERT INTO teacher (name, department_id)
                    VALUES (:name, :departmentId)
                    RETURNING id
                """;

        MapSqlParameterSource ps = new MapSqlParameterSource()
                .addValue("name", teacher.getName())
                .addValue("departmentId", teacher.getDepartment() != null
                        ? teacher.getDepartment().getId()
                        : null
                );

        Long id = jdbcTemplate.queryForObject(sql, ps, Long.class);
        teacher.setId(id);

        return teacher;
    }

    private Teacher updateTeacher(Teacher teacher) {
        String sql = """
                    UPDATE teacher
                       SET name          = :name,
                           department_id = :depId
                     WHERE id            = :id
                """;

        MapSqlParameterSource ps = new MapSqlParameterSource()
                .addValue("id", teacher.getId())
                .addValue("name", teacher.getName())
                .addValue("depId", teacher.getDepartment() != null
                        ? teacher.getDepartment().getId()
                        : null
                );

        jdbcTemplate.update(sql, ps);

        return teacher;
    }

    @Override
    public Optional<Teacher> findById(Long id) {
        try {
            String sqlTeacher = """
                        SELECT
                            t.id   AS teacher_id,
                            t.name AS teacher_name,
                            d.id   AS department_id,
                            d.name AS department_name
                        FROM teacher t
                        LEFT JOIN department d ON d.id = t.department_id
                        WHERE t.id = :id
                    """;

            Teacher teacher = jdbcTemplate.queryForObject(
                    sqlTeacher,
                    Map.of("id", id),
                    new TeacherRowMapper());

            if (teacher != null) {
                findTeacherCourses(teacher);
            }

            return Optional.ofNullable(teacher);

        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    private void findTeacherCourses(Teacher teacher) {
        final String sqlCourses = """
                    SELECT
                        c.id    AS course_id,
                        c.title AS course_title,
                        t.id    AS teacher_id,
                        t.name  AS teacher_name
                    FROM course c
                    LEFT JOIN teacher t ON t.id = c.teacher_id
                    WHERE c.teacher_id = :id
                    ORDER BY c.id
                """;

        List<Course> courses = jdbcTemplate.query(sqlCourses, Map.of("id", teacher.getId()), new CourseRowMapper());

        teacher.getCourses().addAll(courses);
    }

    @Override
    public Slice<Teacher> findAll(Pageable pageable) {
        String sql = """
                    SELECT
                        t.id  AS teacher_id,
                        t.name AS teacher_name,
                        d.id  AS department_id,
                        d.name AS department_name
                    FROM teacher t
                    LEFT JOIN department d ON d.id = t.department_id
                    ORDER BY t.id
                    LIMIT :limit OFFSET :offset
                """;

        int pageSize = pageable.getPageSize();
        long offset = pageable.getOffset();

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("limit", pageSize)
                .addValue("offset", offset);

        List<Teacher> rows = jdbcTemplate.query(sql, params, new TeacherRowMapper());

        boolean hasNext = rows.size() > pageSize;
        if (hasNext) {
            rows = rows.subList(0, pageSize);
        }

        return new SliceImpl<>(rows, pageable, hasNext);
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("DELETE FROM teacher", Map.of());
    }

}
