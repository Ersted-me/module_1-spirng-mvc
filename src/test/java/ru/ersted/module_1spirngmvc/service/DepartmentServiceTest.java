package ru.ersted.module_1spirngmvc.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ersted.module_1spirngmvc.dto.generated.DepartmentDto;
import ru.ersted.module_1spirngmvc.dto.generated.DepartmentCreateRq;
import ru.ersted.module_1spirngmvc.entity.Department;
import ru.ersted.module_1spirngmvc.entity.Teacher;
import ru.ersted.module_1spirngmvc.exception.NotFoundException;
import ru.ersted.module_1spirngmvc.mapper.DepartmentMapper;
import ru.ersted.module_1spirngmvc.repository.DepartmentRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.ersted.module_1spirngmvc.util.DataUtil.*;

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
    @DisplayName("Test save department")
    void givenCreateRq_whenSaveDepartment_thenReturnDepartmentDto() {
        DepartmentCreateRq departmentCreateRq = departmentCreateRq();
        Department department = transientDepartment();
        DepartmentDto departmentDto = transientDepartmentDto();

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
    @DisplayName("Test assign head of department")
    void givenDepartmentAndTeacher_whenAssignHeadOfDepartment_thenReturnDepartmentDtoWithHeadOfDepartment() {
        Department department = transientDepartment();
        DepartmentDto departmentDto = transientFilledDepartmentDto();
        Teacher teacher = transientTeacher();

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
    @DisplayName("Test assign head of department (NOT FOUND)")
    void givenNothing_whenAssignHeadOfDepartment_thenThrowNotFoundException() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            departmentService.assigningHeadOfDepartment(1L, 1L);
        });

        assertEquals("Department with ID 1 not found", exception.getMessage());


        verify(departmentRepository).findById(1L);
        verify(teacherService, never()).findOrElseThrow(1L);
    }


    @Test
    @DisplayName("Test find by id (NOT FOUND)")
    void givenDepartment_whenFindById_thenReturnDepartmentDto() {
        Department department = transientDepartment();

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        Department result = departmentService.findOrElseThrow(1L);

        assertNotNull(result);
        assertEquals(department.getName(), result.getName());

        verify(departmentRepository).findById(1L);
    }

    @Test
    @DisplayName("Test find by id (NOT FOUND)")
    void givenNothing_whenFindById_thenThrowNotFoundException() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            departmentService.findOrElseThrow(1L);
        });

        assertEquals("Department with ID 1 not found", exception.getMessage());

        verify(departmentRepository).findById(1L);
    }

}