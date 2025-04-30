package ru.ersted.module_1spirngmvc.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ersted.module_1spirngmvc.dto.department.DepartmentDto;
import ru.ersted.module_1spirngmvc.dto.department.rq.DepartmentCreateRq;
import ru.ersted.module_1spirngmvc.entity.Department;
import ru.ersted.module_1spirngmvc.entity.Teacher;
import ru.ersted.module_1spirngmvc.exception.NotFoundException;
import ru.ersted.module_1spirngmvc.mapper.DepartmentMapper;
import ru.ersted.module_1spirngmvc.repository.DepartmentRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentMapper departmentMapper;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private TeacherService teacherService;

    @InjectMocks
    private DepartmentService departmentService;


    @Test
    void testSaveDepartment() {
        DepartmentCreateRq departmentCreateRq = new DepartmentCreateRq("Math Department");
        Department department = new Department(1L, "Math Department", null);
        DepartmentDto departmentDto = new DepartmentDto(1L, "Math Department", null);

        when(departmentMapper.map(departmentCreateRq)).thenReturn(department);
        when(departmentRepository.save(department)).thenReturn(department);
        when(departmentMapper.map(department)).thenReturn(departmentDto);

        DepartmentDto result = departmentService.save(departmentCreateRq);

        assertNotNull(result);
        assertEquals(departmentDto, result);

        verify(departmentMapper).map(departmentCreateRq);
        verify(departmentRepository).save(department);
        verify(departmentMapper).map(department);
    }

    @Test
    void testAssignHeadOfDepartment() {
        Department department = new Department(1L, "Math Department", null);
        DepartmentDto departmentDto = new DepartmentDto(1L, "Math Department", null);
        Teacher teacher = new Teacher(1L, "John Toy",null, null);

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(teacherService.findOrElseThrow(1L)).thenReturn(teacher);
        when(departmentRepository.save(department)).thenReturn(department);
        when(departmentMapper.map(department)).thenReturn(departmentDto);

        DepartmentDto result = departmentService.assigningHeadOfDepartment(1L, 1L);

        assertNotNull(result);

        verify(departmentRepository).findById(1L);
        verify(teacherService).findOrElseThrow(1L);
        verify(departmentRepository).save(department);
        verify(departmentMapper).map(department);

        assertEquals(teacher, department.getHeadOfDepartment());
    }

    @Test
    void testAssignHeadOfDepartmentDepartmentNotFound() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            departmentService.assigningHeadOfDepartment(1L, 1L);
        });

        assertEquals("Department with ID 1 not found", exception.getMessage());


        verify(departmentRepository).findById(1L);
        verify(teacherService, never()).findOrElseThrow(1L);
    }


    @Test
    void testFindDepartmentById() {
        Department department = new Department(1L, "Math Department", null);

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        Department result = departmentService.findOrElseThrow(1L);

        assertNotNull(result);
        assertEquals(department.getName(), result.getName());

        verify(departmentRepository).findById(1L);
    }

    @Test
    void testFindDepartmentByIdNotFound() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            departmentService.findOrElseThrow(1L);
        });

        assertEquals("Department with ID 1 not found", exception.getMessage());

        verify(departmentRepository).findById(1L);
    }

}