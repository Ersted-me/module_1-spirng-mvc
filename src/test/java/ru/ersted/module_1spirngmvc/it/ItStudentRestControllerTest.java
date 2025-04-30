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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.ersted.module_1spirngmvc.dto.student.rq.StudentCreateRq;
import ru.ersted.module_1spirngmvc.dto.student.rq.StudentUpdateRq;
import ru.ersted.module_1spirngmvc.entity.Course;
import ru.ersted.module_1spirngmvc.entity.Student;
import ru.ersted.module_1spirngmvc.entity.Teacher;
import ru.ersted.module_1spirngmvc.repository.CourseRepository;
import ru.ersted.module_1spirngmvc.repository.StudentRepository;
import ru.ersted.module_1spirngmvc.repository.TeacherRepository;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@DirtiesContext
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ItStudentRestControllerTest extends AbstractRestControllerBaseTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TeacherRepository teacherRepository;


    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
        courseRepository.deleteAll();
        teacherRepository.deleteAll();
    }

    @Test
    @DisplayName("Test create student functionality")
    void givenStudentCreateRq_whenCreate_thenSuccessResponse() throws Exception {
        StudentCreateRq rq = new StudentCreateRq("John Doe", "john.doe@example.com");

        ResultActions result = mockMvc.perform(post("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rq)));

        result
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is("John Doe")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is("john.doe@example.com")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.courses", CoreMatchers.is(Collections.emptyList())));
    }

    @Test
    @DisplayName("Test findAll student functionality")
    void whenFindAll_thenSuccessResponse() throws Exception {
        Teacher teacher = new Teacher(null, "John Pohn", null, null);
        Course course = new Course(null, "Math", teacher, null);
        Student student = new Student(null, "John Doe", "john.doe@example.com", Set.of(course));

        studentRepository.save(student);

        ResultActions result = mockMvc.perform(get("/api/v1/students"));

        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].name", CoreMatchers.is("John Doe")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].email", CoreMatchers.is("john.doe@example.com")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].courses.[0].id", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].courses.[0].title", CoreMatchers.is("Math")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].courses.[0].teacher.id", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].courses.[0].teacher.name", CoreMatchers.is("John Pohn")));
    }

    @Test
    @DisplayName("Test find by id student functionality")
    void givenStudentId_whenFindById_thenSuccessResponse() throws Exception {
        Teacher teacher = new Teacher(null, "John Pohn", null, null);
        Course course = new Course(null, "Math", teacher, null);
        Student student = new Student(null, "John Doe", "john.doe@example.com", Set.of(course));

        Student transientStudent = studentRepository.save(student);

        ResultActions result = mockMvc.perform(get("/api/v1/students/%d".formatted(transientStudent.getId())));

        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is("John Doe")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is("john.doe@example.com")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.courses.[0].id", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.courses.[0].title", CoreMatchers.is("Math")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.courses.[0].teacher.id", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.courses.[0].teacher.name", CoreMatchers.is("John Pohn")));
    }


    @Test
    @DisplayName("Test find by id student functionality (NOT_FOUND)")
    void givenStudentId_whenFindById_thenNotFoundResponse() throws Exception {
        Long studentId = 1L;

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
        Student student = new Student(null, "John Doe", "john.doe@example.com", Set.of());
        Student transientStudent = studentRepository.save(student);

        StudentUpdateRq rq = new StudentUpdateRq("UpdatedName UpdatedSurname", "updated@example.com");

        ResultActions result = mockMvc.perform(put("/api/v1/students/%d".formatted(transientStudent.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rq)));

        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(rq.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is(rq.email())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.courses", CoreMatchers.is(Collections.emptyList())));
    }

    @Test
    @DisplayName("Test delete student functionality")
    void givenStudentId_whenDelete_thenSuccessResponse() throws Exception {
        Student student = new Student(null, "John Doe", "john.doe@example.com", Set.of());
        Student transientStudent = studentRepository.save(student);

        ResultActions result = mockMvc.perform(delete("/api/v1/students/%d".formatted(transientStudent.getId())));

        Student deletedStudent = studentRepository.findById(transientStudent.getId()).orElse(null);

        assertThat(deletedStudent).isNull();
        result
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Student deleted successfully")));
    }

    @Test
    @DisplayName("Test add course to student functionality")
    void givenStudentIdAndCourseId_whenAddCourse_thenSuccessResponse() throws Exception {
        Teacher teacher = new Teacher(null, "John Pohn", null, null);
        Course course = new Course(null, "Math", teacher, null);
        Student student = new Student(null, "John Doe", "john.doe@example.com", null);

        Course courseTransient = courseRepository.save(course);
        Student studentTransient = studentRepository.save(student);

        ResultActions result = mockMvc.perform(post("/api/v1/students/%d/courses/%d".formatted(studentTransient.getId(), courseTransient.getId())));

        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is("John Doe")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is("john.doe@example.com")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.courses.[0].id", CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.courses.[0].title", CoreMatchers.is("Math")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.courses.[0].teacher.id", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.courses.[0].teacher.name", CoreMatchers.is("John Pohn")));
    }

    @Test
    @DisplayName("Test find all student's courses functionality")
    void givenStudentId_whenFindCourses_thenSuccessResponse() throws Exception {
        Teacher teacher = new Teacher(null, "John Pohn", null, null);
        Course course = new Course(null, "Math", teacher, null);
        Student student = new Student(null, "John Doe", "john.doe@example.com", Set.of(course));

        Student studentTransient = studentRepository.save(student);

        ResultActions result = mockMvc.perform(get("/api/v1/students/%d/courses".formatted(studentTransient.getId())));

        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].title", CoreMatchers.is("Math")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].teacher.id", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].teacher.name", CoreMatchers.is("John Pohn")));
    }

}
