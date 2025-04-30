package ru.ersted.module_1spirngmvc.dto.department;


import ru.ersted.module_1spirngmvc.dto.teacher.TeacherShortDto;

public record DepartmentDto(Long id, String name, TeacherShortDto headOfDepartment) {
}
