package ru.ersted.module_1spirngmvc.repository;

import ru.ersted.module_1spirngmvc.entity.Teacher;

import java.util.Collection;
import java.util.Optional;

public interface TeacherRepository {

    Teacher save(Teacher teacher);

    Optional<Teacher> findById(Long id);

    Collection<Teacher> findAll();

    void deleteAll();

}
