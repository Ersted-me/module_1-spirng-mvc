package ru.ersted.module_1spirngmvc.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ersted.module_1spirngmvc.entity.Teacher;
import ru.ersted.module_1spirngmvc.repository.TeacherRepository;

@Repository
public interface TeacherJpaRepository extends TeacherRepository, JpaRepository<Teacher, Long> {
}
