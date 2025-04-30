package ru.ersted.module_1spirngmvc.repository.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.ersted.module_1spirngmvc.entity.Department;
import ru.ersted.module_1spirngmvc.repository.DepartmentRepository;
import ru.ersted.module_1spirngmvc.repository.jdbc.mapper.DepartmentRowMapper;

import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DepartmentJdbcRepository implements DepartmentRepository {

    private final NamedParameterJdbcTemplate template;

    @Override
    public Department save(Department department) {
        return department.getId() == null
                ? createDepatment(department)
                : updateDepartment(department);
    }

    private Department updateDepartment(Department department) {
        String sql = """
                    UPDATE department
                       SET name                  = :name,
                           head_of_department_id = :headId
                     WHERE id                    = :id
                """;

        MapSqlParameterSource ps = new MapSqlParameterSource()
                .addValue("id", department.getId())
                .addValue("name", department.getName())
                .addValue("headId", department.getHeadOfDepartment() != null
                        ? department.getHeadOfDepartment().getId()
                        : null);

        template.update(sql, ps);

        return department;
    }

    private Department createDepatment(Department d) {
        String sql = """
                    INSERT INTO department (name, head_of_department_id)
                    VALUES (:name, :headId)
                    RETURNING id
                """;

        MapSqlParameterSource ps = new MapSqlParameterSource()
                .addValue("name", d.getName())
                .addValue("headId", d.getHeadOfDepartment() != null
                        ? d.getHeadOfDepartment().getId()
                        : null);

        Long id = template.queryForObject(sql, ps, Long.class);
        d.setId(id);

        return d;
    }

    @Override
    public Optional<Department> findById(Long id) {
        try {

            final String sqlDept = """
                        SELECT
                            d.id   AS department_id,
                            d.name AS department_name,
                    
                            h.id   AS head_of_department_id,
                            h.name AS head_of_department_name
                        FROM department d
                        LEFT JOIN teacher h ON h.id = d.head_of_department_id
                        WHERE d.id = :id
                    """;
            Department dept = template.queryForObject(
                    sqlDept, Map.of("id", id), new DepartmentRowMapper());

            return Optional.ofNullable(dept);

        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteAll() {
        template.update("DELETE FROM department", Map.of());
    }

}
