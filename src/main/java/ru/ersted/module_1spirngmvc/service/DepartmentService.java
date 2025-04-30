package ru.ersted.module_1spirngmvc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ersted.module_1spirngmvc.dto.department.DepartmentDto;
import ru.ersted.module_1spirngmvc.dto.department.rq.DepartmentCreateRq;
import ru.ersted.module_1spirngmvc.entity.Department;
import ru.ersted.module_1spirngmvc.entity.Teacher;
import ru.ersted.module_1spirngmvc.exception.NotFoundException;
import ru.ersted.module_1spirngmvc.mapper.DepartmentMapper;
import ru.ersted.module_1spirngmvc.repository.DepartmentRepository;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentMapper departmentMapper;
    private final DepartmentRepository departmentRepository;
    private final TeacherService teacherService;

    @Transactional
    public DepartmentDto save(DepartmentCreateRq request) {
        Department newDepartment = departmentMapper.map(request);
        departmentRepository.save(newDepartment);

        return departmentMapper.map(newDepartment);
    }

    @Transactional
    public DepartmentDto assigningHeadOfDepartment(Long departmentId, Long teacherId) {
        Department foundDepartment = findOrElseThrow(departmentId);
        Teacher foundTeacher = teacherService.findOrElseThrow(teacherId);

        foundDepartment.setHeadOfDepartment(foundTeacher);
        departmentRepository.save(foundDepartment);

        return departmentMapper.map(foundDepartment);
    }

    public Department findOrElseThrow(Long departmentId) {
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new NotFoundException("Department with ID %d not found".formatted(departmentId)));
    }

}
