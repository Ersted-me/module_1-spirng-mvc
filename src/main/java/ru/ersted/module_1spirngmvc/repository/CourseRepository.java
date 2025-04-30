package ru.ersted.module_1spirngmvc.repository;

import ru.ersted.module_1spirngmvc.entity.Course;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CourseRepository {

    Course save(Course course);

    List<Course> findAll();

    Optional<Course> findById(Long id);

    Collection<Course> findStudentCourses(Long studentId);

    void deleteAll();

}
