package ru.ersted.module_1spirngmvc.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ersted.module_1spirngmvc.entity.Department;
import ru.ersted.module_1spirngmvc.repository.DepartmentRepository;

public interface DepartmentJpaRepository extends DepartmentRepository, JpaRepository<Department, Long> {
}
