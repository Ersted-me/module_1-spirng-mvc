package ru.ersted.module_1spirngmvc.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import ru.ersted.module_1spirngmvc.entity.Course;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CourseRepository {

    Course save(Course course);

    Slice<Course> findAll(Pageable pageable);

    Optional<Course> findById(Long id);

    Slice<Course> findStudentCourses(Long studentId, Pageable pageable);

    void deleteAll();

}
