package ru.ersted.module_1spirngmvc.repository.jdbc.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.ersted.module_1spirngmvc.entity.Department;
import ru.ersted.module_1spirngmvc.entity.Teacher;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TeacherRowMapper implements RowMapper<Teacher> {

    @Override
    public Teacher mapRow(ResultSet rs, int rowNum) throws SQLException {
        Teacher teacher = new Teacher();
        teacher.setId(rs.getLong("teacher_id"));
        teacher.setName(rs.getString("teacher_name"));

        long depId = rs.getLong("department_id");
        if (!rs.wasNull()) {
            Department d = new Department();
            d.setId(depId);
            d.setName(rs.getString("department_name"));
            teacher.setDepartment(d);
        }

        return teacher;
    }

}
