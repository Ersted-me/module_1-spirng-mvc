package ru.ersted.module_1spirngmvc.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.ersted.module_1spirngmvc.dto.generated.CourseDto;
import ru.ersted.module_1spirngmvc.dto.generated.CourseShortDto;
import ru.ersted.module_1spirngmvc.dto.generated.DepartmentShortDto;
import ru.ersted.module_1spirngmvc.dto.generated.StudentShortDto;
import ru.ersted.module_1spirngmvc.dto.generated.TeacherDto;
import ru.ersted.module_1spirngmvc.dto.generated.TeacherShortDto;
import ru.ersted.module_1spirngmvc.dto.generated.TeacherCreateRq;
import ru.ersted.module_1spirngmvc.service.CourseService;
import ru.ersted.module_1spirngmvc.service.TeacherService;
import ru.ersted.module_1spirngmvc.util.DataUtil;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static ru.ersted.module_1spirngmvc.util.DataUtil.*;

@WebMvcTest(controllers = TeacherRestControllerV1.class)
class TeacherRestControllerV1Test {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TeacherService teacherService;

    @MockitoBean
    private CourseService courseService;

    @Test
    @DisplayName("Test create teacher functionality")
    void givenTeacherCreateRq_whenCreate_thenSuccessResponse() throws Exception {
        TeacherCreateRq rq = teacherCreateRq();
        TeacherDto dto = transientTeacherDto();

        BDDMockito.given(teacherService.create(rq)).willReturn(dto);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rq)));

        result
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is("John Toy")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.courses", CoreMatchers.is(Collections.emptyList())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.department", CoreMatchers.nullValue()));
    }

    @Test
    @DisplayName("Test assigning teacher to course functionality")
    void givenTeacherIdAndCourseId_whenAssigningTeacherToCourse_thenSuccessResponse() throws Exception {
        Long teacherId = 1L;
        Long courseId = 1L;
        CourseDto courseDto = transientFilledCourseDto();

        BDDMockito.given(courseService.assigningTeacher(courseId, teacherId)).willReturn(courseDto);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/teachers/%d/courses/%d".formatted(teacherId, courseId)));

        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", CoreMatchers.is("Math")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.teacher.id", CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.teacher.name", CoreMatchers.is("John Toy")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.students.[0].id", CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.students.[0].name", CoreMatchers.is("John Doe")));
    }

    @Test
    @DisplayName("Test find all teachers functionality")
    void whenFindAll_thenSuccessResponse() throws Exception {
        TeacherDto dto = transientFilledTeacherDto();

        BDDMockito.given(teacherService.findAll(PageRequest.of(0, 20)))
                .willReturn(new SliceImpl<>(List.of(dto)));

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/teachers"));

        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id", CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].name", CoreMatchers.is("John Toy")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].courses.[0].id", CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].courses.[0].title", CoreMatchers.is("Math")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].department.id", CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].department.name", CoreMatchers.is("Math Department")));
    }

}