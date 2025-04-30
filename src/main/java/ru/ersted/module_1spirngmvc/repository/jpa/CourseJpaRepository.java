package ru.ersted.module_1spirngmvc.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ersted.module_1spirngmvc.entity.Course;
import ru.ersted.module_1spirngmvc.repository.CourseRepository;

import java.util.Collection;

@Repository
public interface CourseJpaRepository extends CourseRepository, JpaRepository<Course, Long> {

    Collection<Course> findCoursesByStudents_Id(Long studentId);

    @Override
    default Collection<Course> findStudentCourses(Long studentId) {
        return findCoursesByStudents_Id(studentId);
    }

}
