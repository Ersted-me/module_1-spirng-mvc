package ru.ersted.module_1spirngmvc.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.ersted.module_1spirngmvc.dto.course.CourseShortDto;
import ru.ersted.module_1spirngmvc.dto.student.StudentDto;
import ru.ersted.module_1spirngmvc.dto.student.rq.StudentCreateRq;
import ru.ersted.module_1spirngmvc.dto.student.rq.StudentUpdateRq;
import ru.ersted.module_1spirngmvc.dto.teacher.TeacherShortDto;
import ru.ersted.module_1spirngmvc.exception.NotFoundException;
import ru.ersted.module_1spirngmvc.service.CourseService;
import ru.ersted.module_1spirngmvc.service.StudentService;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(StudentRestController.class)
class StudentRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StudentService studentService;

    @MockitoBean
    private CourseService courseService;


    @Test
    @DisplayName("Test create student functionality")
    void givenStudentCreateRq_whenCreate_thenSuccessResponse() throws Exception {
        StudentCreateRq rq = new StudentCreateRq("John Doe", "john.doe@example.com");
        StudentDto dto = new StudentDto(1L, "John Doe", "john.doe@example.com", null);

        BDDMockito.given(studentService.create(any(StudentCreateRq.class))).willReturn(dto);


        ResultActions result = mockMvc.perform(post("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rq)));

        result
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is("John Doe")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is("john.doe@example.com")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.courses", CoreMatchers.is(Collections.emptyList())));
    }

    @Test
    @DisplayName("Test findAll student functionality")
    void whenFindAll_thenSuccessResponse() throws Exception {
        List<StudentDto> list = List.of(new StudentDto(1L, "John Doe", "john.doe@example.com",
                Set.of(new CourseShortDto(1L, "Math", new TeacherShortDto(1L, "John Pohn")))));

        BDDMockito.given(studentService.findAll()).willReturn(list);

        ResultActions result = mockMvc.perform(get("/api/v1/students"));

        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id", CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].name", CoreMatchers.is("John Doe")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].email", CoreMatchers.is("john.doe@example.com")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].courses.[0].id", CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].courses.[0].title", CoreMatchers.is("Math")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].courses.[0].teacher.id", CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].courses.[0].teacher.name", CoreMatchers.is("John Pohn")));
    }

    @Test
    @DisplayName("Test find by id student functionality")
    void givenStudentId_whenFindById_thenSuccessResponse() throws Exception {
        StudentDto dto = new StudentDto(1L, "John Doe", "john.doe@example.com",
                Set.of(new CourseShortDto(1L, "Math", new TeacherShortDto(1L, "John Pohn"))));
        Long studentId = 1L;

        BDDMockito.given(studentService.find(studentId)).willReturn(dto);

        ResultActions result = mockMvc.perform(get("/api/v1/students/%d".formatted(studentId)));

        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is("John Doe")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is("john.doe@example.com")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.courses.[0].id", CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.courses.[0].title", CoreMatchers.is("Math")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.courses.[0].teacher.id", CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.courses.[0].teacher.name", CoreMatchers.is("John Pohn")));
    }


    @Test
    @DisplayName("Test find by id student functionality (NOT_FOUND)")
    void givenStudentId_whenFindById_thenNotFoundResponse() throws Exception {

        Long studentId = 1L;

        BDDMockito.given(studentService.find(studentId))
                .willThrow(new NotFoundException("Student with ID %d not found".formatted(studentId)));

        ResultActions result = mockMvc.perform(get("/api/v1/students/%d".formatted(studentId)));

        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.is(404)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", CoreMatchers.is("Not found")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Student with ID %d not found".formatted(studentId))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path", CoreMatchers.is("/api/v1/students/%d".formatted(studentId))));
    }

    @Test
    @DisplayName("Test update student functionality")
    void givenStudentUpdateRq_whenUpdate_thenSuccessResponse() throws Exception {
        StudentUpdateRq rq = new StudentUpdateRq("John Doe", "john.doe@example.com");
        StudentDto dto = new StudentDto(1L, "John Doe", "john.doe@example.com", null);

        Long studentId = 1L;

        BDDMockito.given(studentService.update(studentId, rq)).willReturn(dto);

        ResultActions result = mockMvc.perform(put("/api/v1/students/%d".formatted(studentId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rq)));

        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is("John Doe")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is("john.doe@example.com")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.courses", CoreMatchers.is(Collections.emptyList())));
    }

    @Test
    @DisplayName("Test delete student functionality")
    void givenStudentId_whenDelete_thenSuccessResponse() throws Exception {
        Long studentId = 1L;

        BDDMockito.doNothing().when(studentService).delete(studentId);

        ResultActions result = mockMvc.perform(delete("/api/v1/students/%d".formatted(studentId)));

        result
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Student deleted successfully")));
    }

    @Test
    @DisplayName("Test add course to student functionality")
    void givenStudentIdAndCourseId_whenAddCourse_thenSuccessResponse() throws Exception {
        Long studentId = 1L;
        Long courseId = 1L;
        StudentDto dto = new StudentDto(1L, "John Doe", "john.doe@example.com",
                Set.of(new CourseShortDto(1L, "Math", new TeacherShortDto(1L, "John Pohn"))));

        BDDMockito.given(studentService.addCourse(courseId, studentId)).willReturn(dto);

        ResultActions result = mockMvc.perform(post("/api/v1/students/%d/courses/%d".formatted(studentId, courseId)));

        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is("John Doe")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is("john.doe@example.com")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.courses.[0].id", CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.courses.[0].title", CoreMatchers.is("Math")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.courses.[0].teacher.id", CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.courses.[0].teacher.name", CoreMatchers.is("John Pohn")));
    }

    @Test
    @DisplayName("Test find all student's courses functionality")
    void givenStudentId_whenFindCourses_thenSuccessResponse() throws Exception {
        Long studentId = 1L;
        Set<CourseShortDto> courses = Set.of(new CourseShortDto(1L, "Math", new TeacherShortDto(1L, "John Pohn")));

        BDDMockito.given(courseService.findAllByStudentId(studentId)).willReturn(courses);

        ResultActions result = mockMvc.perform(get("/api/v1/students/%d/courses".formatted(studentId)));

        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id", CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].title", CoreMatchers.is("Math")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].teacher.id", CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].teacher.name", CoreMatchers.is("John Pohn")));
    }

}