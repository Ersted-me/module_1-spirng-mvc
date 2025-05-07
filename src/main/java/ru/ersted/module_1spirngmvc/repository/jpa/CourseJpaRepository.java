package ru.ersted.module_1spirngmvc.repository.jpa;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ersted.module_1spirngmvc.entity.Course;
import ru.ersted.module_1spirngmvc.repository.CourseRepository;

@Repository
public interface CourseJpaRepository extends CourseRepository, JpaRepository<Course, Long> {

    Slice<Course> findCoursesByStudents_Id(Long studentId, Pageable pageable);

    @Override
    default Slice<Course> findStudentCourses(Long studentId, Pageable pageable) {
        return findCoursesByStudents_Id(studentId, pageable);
    }

}
