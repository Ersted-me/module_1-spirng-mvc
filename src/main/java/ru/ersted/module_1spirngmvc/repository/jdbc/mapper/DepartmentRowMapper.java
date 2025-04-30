package ru.ersted.module_1spirngmvc.repository.jdbc.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.ersted.module_1spirngmvc.entity.Department;
import ru.ersted.module_1spirngmvc.entity.Teacher;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DepartmentRowMapper implements RowMapper<Department> {

    @Override
    public Department mapRow(ResultSet rs, int rowNum) throws SQLException {
        Department department = new Department();
        department.setId(rs.getLong("department_id"));
        department.setName(rs.getString("department_name"));

        long headId = rs.getLong("head_of_department_id");

        if (!rs.wasNull()) {
            Teacher head = new Teacher();
            head.setId(headId);
            head.setName(rs.getString("head_of_department_name"));
            department.setHeadOfDepartment(head);
        }

        return department;
    }

}
