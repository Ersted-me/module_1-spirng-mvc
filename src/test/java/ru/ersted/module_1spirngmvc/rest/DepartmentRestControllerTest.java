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
import ru.ersted.module_1spirngmvc.dto.department.DepartmentDto;
import ru.ersted.module_1spirngmvc.dto.department.rq.DepartmentCreateRq;
import ru.ersted.module_1spirngmvc.dto.teacher.TeacherShortDto;
import ru.ersted.module_1spirngmvc.service.DepartmentService;

@WebMvcTest(controllers = DepartmentRestController.class)
class DepartmentRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DepartmentService departmentService;


    @Test
    @DisplayName("Test create department functionality")
    void givenDepartmentCreateRq_whenCreateDepartment_thenSuccessResponse() throws Exception {
        DepartmentCreateRq rq = new DepartmentCreateRq("Computer Science");
        DepartmentDto dto = new DepartmentDto(1L, "Computer Science", null);

        BDDMockito.given(departmentService.save(rq)).willReturn(dto);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rq)));

        result
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is("Computer Science")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.headOfDepartment", CoreMatchers.nullValue()));
    }

    @Test
    @DisplayName("Test assigning teacher to departament functionality")
    void givenDepartmentIdAndTeacherId_whenAssign_thenSuccessResponse() throws Exception {
        Long departmentId = 1L;
        Long teacherId = 1L;
        TeacherShortDto teacher = new TeacherShortDto(1L, "Professor Smith");
        DepartmentDto dto = new DepartmentDto(1L, "Computer Science", teacher);

        BDDMockito.given(departmentService.assigningHeadOfDepartment(departmentId, teacherId)).willReturn(dto);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/departments/%d/teacher/%d".formatted(departmentId, teacherId)));

        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is("Computer Science")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.headOfDepartment.id", CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.headOfDepartment.name", CoreMatchers.is("Professor Smith")));


    }

}