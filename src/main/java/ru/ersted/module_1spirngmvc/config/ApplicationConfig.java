package ru.ersted.module_1spirngmvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ru.ersted.module_1spirngmvc.repository.CourseRepository;
import ru.ersted.module_1spirngmvc.repository.DepartmentRepository;
import ru.ersted.module_1spirngmvc.repository.StudentRepository;
import ru.ersted.module_1spirngmvc.repository.TeacherRepository;
import ru.ersted.module_1spirngmvc.repository.jpa.CourseJpaRepository;
import ru.ersted.module_1spirngmvc.repository.jpa.DepartmentJpaRepository;
import ru.ersted.module_1spirngmvc.repository.jpa.StudentJpaRepository;
import ru.ersted.module_1spirngmvc.repository.jpa.TeacherJpaRepository;

@Configuration
public class ApplicationConfig {

    @Bean
    @Primary
    public CourseRepository defaultCourseRepository(CourseJpaRepository courseJpaRepository) {
        return courseJpaRepository;
    }

    @Bean
    @Primary
    public TeacherRepository defaultTeacherRepository(TeacherJpaRepository teacherJpaRepository) {
        return teacherJpaRepository;
    }

    @Bean
    @Primary
    public DepartmentRepository defaultDepartmentRepository(DepartmentJpaRepository departmentJpaRepository) {
        return departmentJpaRepository;
    }

    @Bean
    @Primary
    public StudentRepository defaultStudentRepository(StudentJpaRepository studentJpaRepository) {
        return studentJpaRepository;
    }

}
