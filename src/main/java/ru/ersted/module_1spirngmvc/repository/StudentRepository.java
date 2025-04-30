package ru.ersted.module_1spirngmvc.repository;

import ru.ersted.module_1spirngmvc.entity.Student;

import java.util.Collection;
import java.util.Optional;

public interface StudentRepository {

    Student save(Student entity);

    Collection<Student> findAll();

    Optional<Student> findById(Long id);

    void deleteById(Long id);

    void deleteAll();
}
