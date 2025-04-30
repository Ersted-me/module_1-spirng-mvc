package ru.ersted.module_1spirngmvc.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ersted.module_1spirngmvc.dto.course.CourseShortDto;
import ru.ersted.module_1spirngmvc.dto.student.StudentDto;
import ru.ersted.module_1spirngmvc.dto.student.rq.StudentCreateRq;
import ru.ersted.module_1spirngmvc.dto.student.rq.StudentUpdateRq;
import ru.ersted.module_1spirngmvc.entity.Course;
import ru.ersted.module_1spirngmvc.entity.Student;
import ru.ersted.module_1spirngmvc.exception.NotFoundException;
import ru.ersted.module_1spirngmvc.mapper.StudentMapper;
import ru.ersted.module_1spirngmvc.repository.StudentRepository;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private StudentService studentService;

    @Mock
    private CourseService courseService;


    @Test
    void testCreateStudent() {

        StudentCreateRq studentCreateRq = new StudentCreateRq("John Doe", "john.doe@example.com");
        Student student = new Student(1L, "John Doe", "john.doe@example.com", new HashSet<>());
        StudentDto studentDto = new StudentDto(1L, "John Doe", "john.doe@example.com", new HashSet<>());

        when(studentMapper.map(studentCreateRq)).thenReturn(student);
        when(studentRepository.save(student)).thenReturn(student);
        when(studentMapper.map(student)).thenReturn(studentDto);

        StudentDto result = studentService.create(studentCreateRq);

        assertNotNull(result);
        assertEquals(studentDto.name(), result.name());
        assertEquals(studentDto.email(), result.email());

        verify(studentMapper).map(studentCreateRq);
        verify(studentRepository).save(student);
        verify(studentMapper).map(student);
    }

    @Test
    void findAllStudents() {
        Student student = new Student(1L, "John Doe", "john.doe@example.com", new HashSet<>());
        StudentDto dto = new StudentDto(1L, "John Doe", "john.doe@example.com", new HashSet<>());
        Set<Student> students = Set.of(student);
        Set<StudentDto> dtos = Set.of(dto);

        when(studentRepository.findAll()).thenReturn(students);
        when(studentMapper.map(student)).thenReturn(dto);

        Collection<StudentDto> result = studentService.findAll();

        assertNotNull(result);
        assertThat(result).containsExactlyElementsOf(dtos);

        verify(studentMapper).map(student);
        verify(studentRepository).findAll();
    }

    @Test
    void findStudent() {
        Student student = new Student(1L, "John Doe", "john.doe@example.com", new HashSet<>());
        StudentDto dto = new StudentDto(1L, "John Doe", "john.doe@example.com", new HashSet<>());

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentMapper.map(student)).thenReturn(dto);

        StudentDto result = studentService.find(1L);

        assertNotNull(result);
        assertEquals(result, dto);

        verify(studentMapper).map(student);
        verify(studentRepository).findById(1L);
    }

    @Test
    void testUpdateStudent() {
        Student student = new Student(1L, "John Doe", "john.doe@example.com", new HashSet<>());
        StudentDto studentDto = new StudentDto(1L, "Johnathan Doe", "johnathan.doe@example.com", new HashSet<>());
        StudentUpdateRq studentUpdateRq = new StudentUpdateRq("Johnathan Doe", "johnathan.doe@example.com");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentMapper.map(student)).thenReturn(studentDto);
        when(studentRepository.save(student)).thenReturn(student);

        StudentDto result = studentService.update(1L, studentUpdateRq);

        assertNotNull(result);
        assertEquals(studentDto, result);
        assertEquals(studentUpdateRq.email(), result.email());
        assertEquals(studentUpdateRq.name(), result.name());

        verify(studentRepository).findById(1L);
        verify(studentRepository).save(student);
        verify(studentMapper).map(student);
    }

    @Test
    void testUpdateStudentNotFound() {
        StudentUpdateRq studentUpdateRq = new StudentUpdateRq("Johnathan Doe", "johnathan.doe@example.com");

        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            studentService.update(1L, studentUpdateRq);
        });

        assertEquals("Student with ID 1 not found", exception.getMessage());

        verify(studentRepository).findById(1L);
        verify(studentRepository, never()).save(any()); // Метод save не должен быть вызван
    }

    @Test
    void delete() {
        Long studentId = 1L;

        studentService.delete(studentId);

        verify(studentRepository).deleteById(studentId);
    }

    @Test
    void addCourse() {
        Student student = new Student(1L, "John Doe", "john.doe@example.com", new HashSet<>());
        Course course = new Course(1L, "Some course", null, null);
        CourseShortDto courseDto = new CourseShortDto(1L, "Some course", null);
        StudentDto studentDto = new StudentDto(1L, "Johnathan Doe", "johnathan.doe@example.com", Set.of(courseDto));

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseService.findOrElseThrow(1L)).thenReturn(course);
        when(studentMapper.map(student)).thenReturn(studentDto);

        StudentDto result = studentService.addCourse(1L, 1L);

        assertNotNull(result);
        assertEquals(studentDto, result);

        verify(studentRepository).findById(1L);
        verify(courseService).findOrElseThrow(1L);
        verify(studentMapper).map(student);
    }

}