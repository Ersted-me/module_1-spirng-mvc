package ru.ersted.module_1spirngmvc.dto.teacher;

import ru.ersted.module_1spirngmvc.dto.course.CourseShortDto;
import ru.ersted.module_1spirngmvc.dto.department.DepartmentShortDto;

import java.util.Optional;
import java.util.Set;

public record TeacherDto(Long id, String name, Set<CourseShortDto> courses, DepartmentShortDto department) {

    @Override
    public Set<CourseShortDto> courses() {
        return Optional.ofNullable(courses).orElse(Set.of());
    }

}
