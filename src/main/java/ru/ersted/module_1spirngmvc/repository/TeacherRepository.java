package ru.ersted.module_1spirngmvc.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import ru.ersted.module_1spirngmvc.entity.Teacher;

import java.util.Optional;

public interface TeacherRepository {

    Teacher save(Teacher teacher);

    Optional<Teacher> findById(Long id);

    Slice<Teacher> findAll(Pageable pageable);

    void deleteAll();

}
