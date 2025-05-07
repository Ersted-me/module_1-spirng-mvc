package ru.ersted.module_1spirngmvc.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.ersted.module_1spirngmvc.dto.generated.CourseDto;
import ru.ersted.module_1spirngmvc.dto.generated.CourseShortDto;
import ru.ersted.module_1spirngmvc.dto.generated.CourseCreateRq;
import ru.ersted.module_1spirngmvc.entity.Course;
import ru.ersted.module_1spirngmvc.entity.Teacher;
import ru.ersted.module_1spirngmvc.exception.NotFoundException;
import ru.ersted.module_1spirngmvc.mapper.CourseMapper;
import ru.ersted.module_1spirngmvc.repository.CourseRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.ersted.module_1spirngmvc.util.DataUtil.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseMapper courseMapper;

    @Mock
    private TeacherService teacherService;

    @InjectMocks
    private CourseService courseService;

    @Test
    @DisplayName("Test save course")
    void givenCreateRq_whenSave_thenReturnedCourseDto() {
        CourseCreateRq courseCreateRq = courseCreateRq();
        Course newCourse = persistCourse();
        Course course = transientCourse();
        CourseDto courseDto = transientCourseDto();

        when(courseMapper.map(courseCreateRq)).thenReturn(newCourse);
        when(courseRepository.save(newCourse)).thenReturn(course);
        when(courseMapper.map(course)).thenReturn(courseDto);

        CourseDto result = courseService.save(courseCreateRq);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Math", result.getTitle());

        verify(courseRepository).save(newCourse);
        verify(courseMapper).map(course);
    }

    @Test
    @DisplayName("Test find all by student id")
    void givenStudentId_whenFindAllByStudentId_thenReturnCollection() {
        Long studentId = 1L;
        Course course = transientCourse();
        CourseShortDto courseDto = transientCourseShortDto();
        PageRequest pageRequest = PageRequest.of(0, 20);

        when(courseRepository.findStudentCourses(studentId, pageRequest))
                .thenReturn(new SliceImpl<>(new ArrayList<>(List.of(course))));
        when(courseMapper.mapShort(course)).thenReturn(courseDto);

        Slice<CourseShortDto> result = courseService.findAllByStudentId(studentId, pageRequest);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1L, result.iterator().next().getId());
        assertEquals("Math", result.iterator().next().getTitle());

        verify(courseRepository).findStudentCourses(studentId, pageRequest);
        verify(courseMapper).mapShort(course);
    }

    @Test
    @DisplayName("Test assigning teacher")
    void givenCourseAndTeacher_whenAssign_thenReturnedCourseDtoWithTeacher() {
        Course course = transientCourse();
        Teacher teacher = transientTeacher();
        CourseDto courseDto = transientCourseDtoWithTeacher();

        Course spyCourse = spy(course);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(spyCourse));
        when(teacherService.findOrElseThrow(1L)).thenReturn(teacher);
        when(courseRepository.save(spyCourse)).thenReturn(spyCourse);
        when(courseMapper.map(spyCourse)).thenReturn(courseDto);


        CourseDto result = courseService.assigningTeacher(1L, 1L);

        assertNotNull(result);

        verify(courseRepository).findById(1L);
        verify(teacherService).findOrElseThrow(1L);
        verify(courseRepository).save(spyCourse);
        verify(courseMapper).map(spyCourse);

        verify(spyCourse).setTeacher(teacher);
    }

    @Test
    @DisplayName("Test assign course (NOT FOUND)")
    void givenNothing_whenAssigningTeacherCourse_thenThrowNotFoundException() {
        Course course = transientCourse();

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(teacherService.findOrElseThrow(1L)).thenThrow(new NotFoundException("Teacher not found"));

        assertThrows(NotFoundException.class, () -> {
            courseService.assigningTeacher(1L, 1L);
        });

        verify(courseRepository).findById(1L);
        verify(teacherService).findOrElseThrow(1L);
        verify(courseRepository, never()).save(any());
    }

}