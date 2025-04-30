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
import ru.ersted.module_1spirngmvc.dto.course.rq.CourseCreateRq;
import ru.ersted.module_1spirngmvc.entity.Course;
import ru.ersted.module_1spirngmvc.entity.Student;
import ru.ersted.module_1spirngmvc.entity.Teacher;
import ru.ersted.module_1spirngmvc.repository.CourseRepository;
import ru.ersted.module_1spirngmvc.repository.StudentRepository;
import ru.ersted.module_1spirngmvc.repository.TeacherRepository;

import java.util.Collections;
import java.util.Set;

@DirtiesContext
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ItCourseRestControllerTest extends AbstractRestControllerBaseTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
        studentRepository.deleteAll();
        teacherRepository.deleteAll();
    }


    @Test
    @DisplayName("Test create course functionality")
    void givenCourseCreteRq_whenCreateCourse_thenSuccessResponse() throws Exception {
        CourseCreateRq rq = new CourseCreateRq("Math");

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rq)));

        result
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", CoreMatchers.is("Math")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.teacher", CoreMatchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.students", CoreMatchers.is(Collections.emptyList())));
    }

    @Test
    @DisplayName("Test find all courses functionality")
    void whenFindAll_thenSuccessResponse() throws Exception {
        Teacher teacher = new Teacher(null, "Professor Smith", null, null);
        Course course = new Course(null, "Math", teacher, null);
        Student student = new Student(null, "John Doe", "some@example.example", Set.of(course));
        studentRepository.save(student);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/courses"));

        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].title", CoreMatchers.is("Math")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].teacher.id", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].teacher.name", CoreMatchers.is("Professor Smith")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].students.[0].id", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].students.[0].name", CoreMatchers.is("John Doe")));
    }

}
