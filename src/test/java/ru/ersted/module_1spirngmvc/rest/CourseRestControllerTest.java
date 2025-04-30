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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.ersted.module_1spirngmvc.dto.course.CourseDto;
import ru.ersted.module_1spirngmvc.dto.course.rq.CourseCreateRq;
import ru.ersted.module_1spirngmvc.dto.student.StudentDto;
import ru.ersted.module_1spirngmvc.dto.student.StudentShortDto;
import ru.ersted.module_1spirngmvc.dto.teacher.TeacherShortDto;
import ru.ersted.module_1spirngmvc.service.CourseService;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(controllers = CourseRestController.class)
class CourseRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CourseService courseService;


    @Test
    @DisplayName("Test create course functionality")
    void givenCourseCreteRq_whenCreateCourse_thenSuccessResponse() throws Exception {
        CourseCreateRq rq = new CourseCreateRq("Math");
        CourseDto dto = new CourseDto(1L, "Math", null, null);

        BDDMockito.given(courseService.save(rq)).willReturn(dto);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rq)));

        result
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", CoreMatchers.is("Math")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.teacher", CoreMatchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.students", CoreMatchers.is(Collections.emptyList())));
    }

    @Test
    @DisplayName("Test find all courses functionality")
    void whenFindAll_thenSuccessResponse() throws Exception {
        TeacherShortDto teacher = new TeacherShortDto(1L, "Professor Smith");
        StudentShortDto student = new StudentShortDto(1L, "John Doe");
        List<CourseDto> list = Collections.singletonList(new CourseDto(1L, "Math", teacher, Set.of(student)));

        BDDMockito.given(courseService.findAll()).willReturn(list);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/courses"));

        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id", CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].title", CoreMatchers.is("Math")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].teacher.id", CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].teacher.name", CoreMatchers.is("Professor Smith")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].students.[0].id", CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].students.[0].name", CoreMatchers.is("John Doe")));
    }

}