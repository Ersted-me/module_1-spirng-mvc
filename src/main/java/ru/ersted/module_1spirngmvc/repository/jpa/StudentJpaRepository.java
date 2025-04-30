package ru.ersted.module_1spirngmvc.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ersted.module_1spirngmvc.entity.Student;
import ru.ersted.module_1spirngmvc.repository.StudentRepository;

@Repository
public interface StudentJpaRepository extends StudentRepository, JpaRepository<Student, Long> {
}
