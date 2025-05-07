package ru.ersted.module_1spirngmvc.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.ersted.module_1spirngmvc.config.DatabaseConfig;
import ru.ersted.module_1spirngmvc.dto.generated.DepartmentCreateRq;
import ru.ersted.module_1spirngmvc.entity.Department;
import ru.ersted.module_1spirngmvc.entity.Teacher;
import ru.ersted.module_1spirngmvc.repository.DepartmentRepository;
import ru.ersted.module_1spirngmvc.repository.TeacherRepository;
import ru.ersted.module_1spirngmvc.repository.jpa.DepartmentJpaRepository;
import ru.ersted.module_1spirngmvc.util.DataUtil;

import static ru.ersted.module_1spirngmvc.util.DataUtil.persistDepartment;
import static ru.ersted.module_1spirngmvc.util.DataUtil.persistTeacher;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import({DatabaseConfig.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class ItDepartmentRestControllerV1Test {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DepartmentJpaRepository departmentJpaRepository;

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
        DepartmentCreateRq rq = DataUtil.departmentCreateRq();

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rq)));

        int departmentId = departmentJpaRepository.findAll().iterator().next().getId().intValue();

        result
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(departmentId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is("Math Department")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.headOfDepartment", CoreMatchers.nullValue()));
    }

    @Test
    @DisplayName("Test assigning teacher to departament functionality")
    void givenDepartmentIdAndTeacherId_whenAssign_thenSuccessResponse() throws Exception {
        Department persistDepartment = persistDepartment();
        Department department = departmentRepository.save(persistDepartment);


        Teacher persistTeacher = persistTeacher();
        Teacher teacher = teacherRepository.save(persistTeacher);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/departments/%d/teacher/%d".formatted(persistDepartment.getId(), teacher.getId())));

        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(department.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is("Math Department")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.headOfDepartment.id", CoreMatchers.is(teacher.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.headOfDepartment.name", CoreMatchers.is("John Toy")));
    }

}
