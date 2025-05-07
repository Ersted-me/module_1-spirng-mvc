package ru.ersted.module_1spirngmvc.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import ru.ersted.module_1spirngmvc.dto.generated.TeacherDto;
import ru.ersted.module_1spirngmvc.dto.generated.TeacherCreateRq;
import ru.ersted.module_1spirngmvc.entity.Teacher;
import ru.ersted.module_1spirngmvc.exception.NotFoundException;
import ru.ersted.module_1spirngmvc.mapper.TeacherMapper;
import ru.ersted.module_1spirngmvc.repository.TeacherRepository;
import ru.ersted.module_1spirngmvc.util.DataUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.ersted.module_1spirngmvc.util.DataUtil.*;
import static ru.ersted.module_1spirngmvc.util.DataUtil.transientTeacher;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private TeacherMapper teacherMapper;

    @InjectMocks
    private TeacherService teacherService;


    @Test
    @DisplayName("given valid create request when create then returns saved TeacherDto")
    void givenValidTeacherCreateRequest_whenCreate_thenReturnTeacherDto() {
        TeacherCreateRq teacherCreateRq = teacherCreateRq();
        Teacher teacher = transientTeacher();
        TeacherDto teacherDto = transientTeacherDto();

        when(teacherMapper.map(teacherCreateRq)).thenReturn(teacher);
        when(teacherRepository.save(teacher)).thenReturn(teacher);
        when(teacherMapper.map(teacher)).thenReturn(teacherDto);

        TeacherDto result = teacherService.create(teacherCreateRq);

        assertNotNull(result);
        assertEquals(teacherDto, result);

        verify(teacherMapper).map(teacherCreateRq);
        verify(teacherRepository).save(teacher);
        verify(teacherMapper).map(teacher);
    }

    @Test
    @DisplayName("given existing teachers when findAll then returns all TeacherDto")
    void givenExistingTeachers_whenFindAll_thenReturnAllTeachers() {
        Teacher teacher = transientTeacher();
        TeacherDto teacherDto = transientTeacherDto();

        when(teacherRepository.findAll(PageRequest.of(0, 20)))
                .thenReturn(new SliceImpl<>(Collections.singletonList(teacher)));
        when(teacherMapper.map(teacher)).thenReturn(teacherDto);

        Collection<TeacherDto> result = teacherService.findAll(PageRequest.of(0, 20)).getContent();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(teacherDto, result.iterator().next());

        verify(teacherRepository).findAll(PageRequest.of(0, 20));
        verify(teacherMapper).map(teacher);
    }

    @Test
    @DisplayName("given existing teacher ID when findOrElseThrow then returns Teacher")
    void givenExistingTeacherId_whenFindOrElseThrow_thenReturnTeacher() {
        Teacher teacher = transientTeacher();

        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        Teacher result = teacherService.findOrElseThrow(1L);

        assertNotNull(result);
        assertEquals(teacher.getName(), result.getName());

        verify(teacherRepository).findById(1L);
    }

    @Test
    @DisplayName("given non-existing teacher ID when findOrElseThrow then throws NotFoundException")
    void givenNonExistingTeacherId_whenFindOrElseThrow_thenThrowNotFoundException() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> teacherService.findOrElseThrow(1L));

        assertEquals("Teacher with ID 1 not found", ex.getMessage());

        verify(teacherRepository).findById(1L);
    }

}