package ru.ersted.module_1spirngmvc.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ersted.module_1spirngmvc.dto.course.CourseDto;
import ru.ersted.module_1spirngmvc.dto.course.CourseShortDto;
import ru.ersted.module_1spirngmvc.dto.course.rq.CourseCreateRq;
import ru.ersted.module_1spirngmvc.dto.teacher.TeacherShortDto;
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
    void save() {
        CourseCreateRq courseCreateRq = new CourseCreateRq("Some title");
        Course newCourse = new Course(null, "Some title", null, null);
        Course course = new Course(1L, "Some title", null, null);
        CourseDto courseDto = new CourseDto(1L, "Some title", null, null);

        when(courseMapper.map(courseCreateRq)).thenReturn(newCourse);
        when(courseRepository.save(newCourse)).thenReturn(course);
        when(courseMapper.map(course)).thenReturn(courseDto);

        CourseDto result = courseService.save(courseCreateRq);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Some title", result.title());

        verify(courseRepository).save(newCourse);
        verify(courseMapper).map(course);
    }

    @Test
    void findAllByStudentId() {
        Long studentId = 1L;
        Course course = new Course(1L, "Some title", null, null);
        CourseShortDto courseDto = new CourseShortDto(1L, "Some title",null);


        when(courseRepository.findStudentCourses(studentId)).thenReturn(new ArrayList<>(List.of(course)));
        when(courseMapper.mapShort(course)).thenReturn(courseDto);

        Collection<CourseShortDto> result = courseService.findAllByStudentId(studentId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.iterator().next().id());
        assertEquals("Some title", result.iterator().next().title());

        verify(courseRepository).findStudentCourses(studentId);
        verify(courseMapper).mapShort(course);
    }

    @Test
    void assigningTeacher() {
        Course course = new Course(1L, "Some title", null, null);
        Teacher teacher = new Teacher(1L, "John Toy", null, null);
        TeacherShortDto teacherShortDto = new TeacherShortDto(1L, "John Toy");
        CourseDto courseDto = new CourseDto(1L, "Some title", teacherShortDto, null);

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
    void assigningTeacherCourseNotFound() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            courseService.assigningTeacher(1L, 1L);
        });

        verify(courseRepository).findById(1L);
        verify(teacherService, never()).findOrElseThrow(anyLong());
        verify(courseRepository, never()).save(any());
    }

    @Test
    void assigningTeacherTeacherNotFound() {
        Course course = new Course(1L, "Some title", null, null);

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