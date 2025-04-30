package ru.ersted.module_1spirngmvc.repository.jdbc.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.ersted.module_1spirngmvc.entity.Student;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentRowMapper implements RowMapper<Student> {

    @Override
    public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
        Student student = new Student();
        student.setId(rs.getLong("student_id"));
        student.setName(rs.getString("student_name"));
        student.setEmail(rs.getString("student_email"));

        return student;
    }

}
