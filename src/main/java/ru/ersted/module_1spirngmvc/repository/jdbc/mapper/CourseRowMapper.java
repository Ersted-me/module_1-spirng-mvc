package ru.ersted.module_1spirngmvc.repository.jdbc.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.ersted.module_1spirngmvc.entity.Course;
import ru.ersted.module_1spirngmvc.entity.Teacher;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CourseRowMapper implements RowMapper<Course> {

    @Override
    public Course mapRow(ResultSet rs, int rowNum) throws SQLException {
        Course course = new Course();
        course.setId(rs.getLong("course_id"));
        course.setTitle(rs.getString("course_title"));

        long teacherId = rs.getLong("teacher_id");
        if (!rs.wasNull()) {
            Teacher teacher = new Teacher();
            teacher.setId(teacherId);
            teacher.setName(rs.getString("teacher_name"));
            course.setTeacher(teacher);
        }

        return course;
    }

}
