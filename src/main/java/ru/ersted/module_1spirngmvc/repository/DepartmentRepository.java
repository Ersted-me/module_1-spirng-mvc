package ru.ersted.module_1spirngmvc.repository;

import ru.ersted.module_1spirngmvc.entity.Department;

import java.util.Optional;

public interface DepartmentRepository {

    Department save(Department department);

    Optional<Department> findById(Long id);

    void deleteAll();

}
