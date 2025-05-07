package ru.ersted.module_1spirngmvc.repository.jdbc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.ersted.module_1spirngmvc.config.DatabaseConfig;
import ru.ersted.module_1spirngmvc.entity.Department;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({DatabaseConfig.class, DepartmentJdbcRepository.class})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DepartmentJdbcRepositoryTest {


    @Autowired
    NamedParameterJdbcTemplate template;

    @Autowired
    DepartmentJdbcRepository repository;

    @Test
    @DisplayName("Test create department")
    void givenNewDepartment_whenSave_thenTransientDepartment() {
        Department department = new Department(null, "Physics", null);

        Department saved = repository.save(department);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Physics");
    }

    @Test
    @DisplayName("Test find department by id")
    void givenDepartmentId_whenFindById_thenReturnDepartment() {

        Long teacherId = template.queryForObject("INSERT INTO teacher(name) VALUES(:name) RETURNING id", Map.of("name", "Tom"), Long.class);

        Long departmentId = template.queryForObject("INSERT INTO department(name, head_of_department_id) " +
                "VALUES(:name, :head_of_department_id) " +
                "RETURNING id", Map.of("name", "Social", "head_of_department_id", teacherId), Long.class);


        Optional<Department> optionalDepartment = repository.findById(departmentId);

        assertThat(optionalDepartment).isPresent();

        Department department = optionalDepartment.get();
        assertThat(department.getId()).isEqualTo(departmentId);
        assertThat(department.getName()).isEqualTo("Social");
        assertThat(department.getHeadOfDepartment().getId()).isEqualTo(teacherId);
        assertThat(department.getHeadOfDepartment().getName()).isEqualTo("Tom");
    }

    @Test
    @DisplayName("Test delete all departments")
    void whenDeleteAllDepartments_thenAllDepartmentsDeleted() {

        template.queryForObject("INSERT INTO department(name) " +
                "VALUES(:name) " +
                "RETURNING id", Map.of("name", "Social"), Long.class);

        repository.deleteAll();

        Integer count = template.queryForObject("SELECT COUNT(*) FROM department", Map.of(), Integer.class);

        assertThat(count).isZero();

    }


}