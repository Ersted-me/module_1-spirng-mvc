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
import ru.ersted.module_1spirngmvc.dto.generated.CourseCreateRq;
import ru.ersted.module_1spirngmvc.entity.Course;
import ru.ersted.module_1spirngmvc.entity.Student;
import ru.ersted.module_1spirngmvc.entity.Teacher;
import ru.ersted.module_1spirngmvc.repository.CourseRepository;
import ru.ersted.module_1spirngmvc.repository.StudentRepository;
import ru.ersted.module_1spirngmvc.repository.TeacherRepository;
import ru.ersted.module_1spirngmvc.repository.jpa.CourseJpaRepository;
import ru.ersted.module_1spirngmvc.util.DataUtil;

import java.util.Collections;
import java.util.Set;

import static ru.ersted.module_1spirngmvc.util.DataUtil.courseCreateRq;
import static ru.ersted.module_1spirngmvc.util.DataUtil.persistFilledStudent;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import({DatabaseConfig.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class ItCourseRestControllerV1Test {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseJpaRepository coursejpaRepository;

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
        CourseCreateRq rq = courseCreateRq();

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rq)));

        int courseId = coursejpaRepository.findAll().iterator().next().getId().intValue();

        result
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(courseId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", CoreMatchers.is("Math")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.teacher", CoreMatchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.students", CoreMatchers.is(Collections.emptyList())));
    }

    @Test
    @DisplayName("Test find all courses functionality")
    void whenFindAll_thenSuccessResponse() throws Exception {
        Student persistStudent = persistFilledStudent();
        Student student = studentRepository.save(persistStudent);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/courses"));


        int studentId = student.getId().intValue();
        int courseId = student.getCourses().iterator().next().getId().intValue();
        int teacherId = student.getCourses().iterator().next().getTeacher().getId().intValue();

        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id", CoreMatchers.is(courseId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].title", CoreMatchers.is("Math")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].teacher.id", CoreMatchers.is(teacherId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].teacher.name", CoreMatchers.is("John Toy")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].students.[0].id", CoreMatchers.is(studentId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].students.[0].name", CoreMatchers.is("John Doe")));
    }

}
