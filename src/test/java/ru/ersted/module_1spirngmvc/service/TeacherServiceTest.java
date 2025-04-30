package ru.ersted.module_1spirngmvc.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ersted.module_1spirngmvc.dto.teacher.TeacherDto;
import ru.ersted.module_1spirngmvc.dto.teacher.rq.TeacherCreateRq;
import ru.ersted.module_1spirngmvc.entity.Teacher;
import ru.ersted.module_1spirngmvc.exception.NotFoundException;
import ru.ersted.module_1spirngmvc.mapper.TeacherMapper;
import ru.ersted.module_1spirngmvc.repository.TeacherRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private TeacherMapper teacherMapper;

    @InjectMocks
    private TeacherService teacherService;


    @Test
    void testCreateTeacher() {
        TeacherCreateRq teacherCreateRq = new TeacherCreateRq("John Toy");
        Teacher teacher = new Teacher(1L, "John Toy",null, null);
        TeacherDto teacherDto = new TeacherDto(1L, "John Toy", new HashSet<>(), null);


        when(teacherMapper.map(teacherCreateRq)).thenReturn(teacher);
        when(teacherRepository.save(teacher)).thenReturn(teacher);
        when(teacherMapper.map(teacher)).thenReturn(teacherDto);


        TeacherDto result = teacherService.create(teacherCreateRq);


        assertNotNull(result);
        assertEquals(teacherDto, result);

        // Проверки взаимодействий
        verify(teacherMapper).map(teacherCreateRq);
        verify(teacherRepository).save(teacher);
        verify(teacherMapper).map(teacher);
    }

    @Test
    void testFindAllTeachers() {
        Teacher teacher = new Teacher(1L, "John Toy", null, null);
        TeacherDto teacherDto = new TeacherDto(1L, "John Toy", new HashSet<>(), null);

        when(teacherRepository.findAll()).thenReturn(Collections.singletonList(teacher));
        when(teacherMapper.map(teacher)).thenReturn(teacherDto);

        Collection<TeacherDto> result = teacherService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(teacherDto, result.iterator().next());

        verify(teacherRepository).findAll();
        verify(teacherMapper).map(teacher);
    }

    @Test
    void testFindTeacherDtoById() {
        Teacher teacher = new Teacher(1L, "John Toy",null, null);

        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        Teacher result = teacherService.findOrElseThrow(1L);

        assertNotNull(result);
        assertEquals(teacher.getName(), result.getName());

        verify(teacherRepository).findById(1L);
    }

    @Test
    void testFindTeacherByIdNotFound() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            teacherService.findOrElseThrow(1L);
        });

        assertEquals("Teacher with ID 1 not found", exception.getMessage());

        verify(teacherRepository).findById(1L);
    }

}