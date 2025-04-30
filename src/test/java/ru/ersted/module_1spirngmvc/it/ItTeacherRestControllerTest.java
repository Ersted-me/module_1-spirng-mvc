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
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.ersted.module_1spirngmvc.dto.teacher.rq.TeacherCreateRq;
import ru.ersted.module_1spirngmvc.entity.Course;
import ru.ersted.module_1spirngmvc.entity.Department;
import ru.ersted.module_1spirngmvc.entity.Student;
import ru.ersted.module_1spirngmvc.entity.Teacher;
import ru.ersted.module_1spirngmvc.repository.CourseRepository;
import ru.ersted.module_1spirngmvc.repository.DepartmentRepository;
import ru.ersted.module_1spirngmvc.repository.TeacherRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@DirtiesContext
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ItTeacherRestControllerTest extends AbstractRestControllerBaseTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @BeforeEach
    public void setUp() {
        teacherRepository.deleteAll();
        courseRepository.deleteAll();
        departmentRepository.deleteAll();
    }

    @Test
    @DisplayName("Test create teacher functionality")
    void givenTeacherCreateRq_whenCreate_thenSuccessResponse() throws Exception {
        TeacherCreateRq rq = new TeacherCreateRq("Professor Smith");

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rq)));

        result
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is("Professor Smith")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.courses", CoreMatchers.is(Collections.emptyList())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.department", CoreMatchers.nullValue()));
    }

    @Test
    @Transactional
    @DisplayName("Test assigning teacher to course functionality")
    void givenTeacherIdAndCourseId_whenAssigningTeacherToCourse_thenSuccessResponse() throws Exception {
        Student student = new Student(null, "John Doe", "example@example.example", null);
        Course course = new Course(null, "Math 101", null, new HashSet<>(List.of(student)));
        Teacher teacher = new Teacher(null, "Professor Smith", null, new HashSet<>(List.of(course)));

        teacherRepository.save(teacher);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/teachers/%d/courses/%d".formatted(teacher.getId(), course.getId())));

        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", CoreMatchers.is("Math 101")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.teacher.id", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.teacher.name", CoreMatchers.is("Professor Smith")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.students.[0].id", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.students.[0].name", CoreMatchers.is("John Doe")));
    }

    @Test
    @Transactional
    @DisplayName("Test find all teachers functionality")
    void whenFindAll_thenSuccessResponse() throws Exception {

        Student student = new Student(null, "John Doe", "example@example.example", Set.of());
        Course course = new Course(null, "Math 101", null, Set.of(student));
        Department department = new Department(null, "Computer Science", null);
        Teacher teacher = new Teacher(null, "Professor Smith", department, Set.of(course));
        teacherRepository.save(teacher);


        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/teachers"));

        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].name", CoreMatchers.is("Professor Smith")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].courses.[0].id", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].courses.[0].title", CoreMatchers.is("Math 101")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].department.id", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].department.name", CoreMatchers.is("Computer Science")));
    }

}
