package ru.ersted.module_1spirngmvc.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.ersted.module_1spirngmvc.dto.department.rq.DepartmentCreateRq;
import ru.ersted.module_1spirngmvc.entity.Department;
import ru.ersted.module_1spirngmvc.entity.Teacher;
import ru.ersted.module_1spirngmvc.repository.DepartmentRepository;
import ru.ersted.module_1spirngmvc.repository.TeacherRepository;

@DirtiesContext
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ItDepartmentRestControllerTest extends AbstractRestControllerBaseTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @BeforeEach
    void setUp() {
        departmentRepository.deleteAll();
        teacherRepository.deleteAll();
    }


    @Test
    @DisplayName("Test create department functionality")
    void givenDepartmentCreateRq_whenCreateDepartment_thenSuccessResponse() throws Exception {
        DepartmentCreateRq rq = new DepartmentCreateRq("Computer Science");

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rq)));

        result
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is("Computer Science")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.headOfDepartment", CoreMatchers.nullValue()));
    }

    @Test
    @DisplayName("Test assigning teacher to departament functionality")
    void givenDepartmentIdAndTeacherId_whenAssign_thenSuccessResponse() throws Exception {
        Department department = new Department(null, "Computer Science", null);
        departmentRepository.save(department);


        Teacher teacher = new Teacher(null, "Professor Smith", null, null);
        teacherRepository.save(teacher);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/departments/%d/teacher/%d".formatted(department.getId(), teacher.getId())));

        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is("Computer Science")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.headOfDepartment.id", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.headOfDepartment.name", CoreMatchers.is("Professor Smith")));
    }

}
