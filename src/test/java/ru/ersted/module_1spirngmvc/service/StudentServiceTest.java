package ru.ersted.module_1spirngmvc.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ersted.module_1spirngmvc.dto.generated.CourseShortDto;
import ru.ersted.module_1spirngmvc.dto.generated.StudentDto;
import ru.ersted.module_1spirngmvc.dto.generated.StudentCreateRq;
import ru.ersted.module_1spirngmvc.dto.generated.StudentUpdateRq;
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
import static ru.ersted.module_1spirngmvc.util.DataUtil.*;
import static ru.ersted.module_1spirngmvc.util.DataUtil.transientStudent;

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
    @DisplayName("given valid create request when create then returns saved StudentDto")
    void givenValidStudentCreateRequest_whenCreate_thenReturnStudentDto() {

        StudentCreateRq studentCreateRq = studentCreateRq();
        Student student = transientStudent();
        StudentDto studentDto = transientStudentDto();

        when(studentMapper.map(studentCreateRq)).thenReturn(student);
        when(studentRepository.save(student)).thenReturn(student);
        when(studentMapper.map(student)).thenReturn(studentDto);

        StudentDto result = studentService.create(studentCreateRq);

        assertNotNull(result);
        assertEquals(studentDto.getName(), result.getName());
        assertEquals(studentDto.getEmail(), result.getEmail());

        verify(studentMapper).map(studentCreateRq);
        verify(studentRepository).save(student);
        verify(studentMapper).map(student);
    }

    @Test
    @DisplayName("given existing students when findAll then returns all StudentDto")
    void givenExistingStudents_whenFindAll_thenReturnAllStudents() {
        Student student = transientStudent();
        StudentDto dto = transientStudentDto();
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
    @DisplayName("given existing student when find then returns StudentDto")
    void givenExistingStudent_whenFind_thenReturnStudentDto() {
        Student student = transientStudent();
        StudentDto dto = transientStudentDto();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentMapper.map(student)).thenReturn(dto);

        StudentDto result = studentService.find(1L);

        assertNotNull(result);
        assertEquals(dto, result);

        verify(studentMapper).map(student);
        verify(studentRepository).findById(1L);
    }

    @Test
    @DisplayName("given existing student and update request when update then updates student and returns StudentDto")
    void givenExistingStudentAndUpdateRequest_whenUpdate_thenStudentUpdatedAndDtoReturned() {
        Student student = transientStudent();
        StudentDto studentDto = transientStudentDtoJohnathan();
        StudentUpdateRq studentUpdateRq = studentUpdateRq();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentMapper.map(student)).thenReturn(studentDto);
        when(studentRepository.save(student)).thenReturn(student);

        StudentDto result = studentService.update(1L, studentUpdateRq);

        assertNotNull(result);
        assertEquals(studentDto, result);
        assertEquals(studentUpdateRq.getEmail(), result.getEmail());
        assertEquals(studentUpdateRq.getName(), result.getName());

        verify(studentRepository).findById(1L);
        verify(studentRepository).save(student);
        verify(studentMapper).map(student);
    }

    @Test
    @DisplayName("given non-existing student when update then throws NotFoundException")
    void givenNonExistingStudent_whenUpdate_thenThrowsNotFoundException() {
        StudentUpdateRq studentUpdateRq = studentUpdateRq();

        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> studentService.update(1L, studentUpdateRq));

        assertEquals("Student with ID 1 not found", exception.getMessage());

        verify(studentRepository).findById(1L);
        verify(studentRepository, never()).save(any());
    }

    @Test
    @DisplayName("given student ID when delete then repository.deleteById is called once")
    void givenStudentId_whenDelete_thenRepositoryDeleteByIdCalled() {
        Long studentId = 1L;

        studentService.delete(studentId);

        verify(studentRepository).deleteById(studentId);
    }

    @Test
    @DisplayName("given student and course when addCourse then associates course and returns StudentDto")
    void givenStudentAndCourse_whenAddCourse_thenCourseAssociated() {
        Student student = transientStudent();
        Course course = transientCourse();
        CourseShortDto courseDto = transientCourseShortDto();
        StudentDto studentDto = transientFilledStudentDto();

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