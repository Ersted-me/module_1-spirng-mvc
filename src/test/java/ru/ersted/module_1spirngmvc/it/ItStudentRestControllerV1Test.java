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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.ersted.module_1spirngmvc.config.DatabaseConfig;
import ru.ersted.module_1spirngmvc.dto.generated.StudentCreateRq;
import ru.ersted.module_1spirngmvc.dto.generated.StudentUpdateRq;
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
import static ru.ersted.module_1spirngmvc.util.DataUtil.*;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import({DatabaseConfig.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class ItStudentRestControllerV1Test {


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
        StudentCreateRq rq = studentCreateRq();

        ResultActions result = mockMvc.perform(post("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rq)));

        Student student = studentRepository.findAll().iterator().next();

        result
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(student.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is("John Doe")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is("john.doe@example.com")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.courses", CoreMatchers.is(Collections.emptyList())));
    }

    @Test
    @DisplayName("Test findAll student functionality")
    void whenFindAll_thenSuccessResponse() throws Exception {
        Student persistStudent = persistFilledStudent();

        Student student = studentRepository.save(persistStudent);
        int studentId = student.getId().intValue();
        int courseId = student.getCourses().iterator().next().getId().intValue();
        int teacherId = student.getCourses().iterator().next().getTeacher().getId().intValue();

        ResultActions result = mockMvc.perform(get("/api/v1/students"));

        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id", CoreMatchers.is(studentId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].name", CoreMatchers.is("John Doe")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].email", CoreMatchers.is("john.doe@example.com")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].courses.[0].id", CoreMatchers.is(courseId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].courses.[0].title", CoreMatchers.is("Math")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].courses.[0].teacher.id", CoreMatchers.is(teacherId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].courses.[0].teacher.name", CoreMatchers.is("John Toy")));
    }

    @Test
    @DisplayName("Test find by id student functionality")
    void givenStudentId_whenFindById_thenSuccessResponse() throws Exception {
        Student persistStudent = persistFilledStudent();

        Student student = studentRepository.save(persistStudent);
        int studentId = student.getId().intValue();
        int courseId = student.getCourses().iterator().next().getId().intValue();
        int teacherId = student.getCourses().iterator().next().getTeacher().getId().intValue();


        ResultActions result = mockMvc.perform(get("/api/v1/students/%d".formatted(student.getId())));

        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(studentId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is("John Doe")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is("john.doe@example.com")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.courses.[0].id", CoreMatchers.is(courseId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.courses.[0].title", CoreMatchers.is("Math")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.courses.[0].teacher.id", CoreMatchers.is(teacherId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.courses.[0].teacher.name", CoreMatchers.is("John Toy")));
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
        Student persistStudent = persistStudent();
        Student student = studentRepository.save(persistStudent);

        StudentUpdateRq rq = studentUpdateRq();

        ResultActions result = mockMvc.perform(put("/api/v1/students/%d".formatted(student.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rq)));

        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(student.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(rq.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is(rq.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.courses", CoreMatchers.is(Collections.emptyList())));
    }

    @Test
    @DisplayName("Test delete student functionality")
    void givenStudentId_whenDelete_thenSuccessResponse() throws Exception {
        Student persistStudent = persistStudent();
        Student student = studentRepository.save(persistStudent);

        ResultActions result = mockMvc.perform(delete("/api/v1/students/%d".formatted(student.getId())));

        Student deletedStudent = studentRepository.findById(student.getId()).orElse(null);

        assertThat(deletedStudent).isNull();
        result
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Student deleted successfully")));
    }

    @Test
    @DisplayName("Test add course to student functionality")
    void givenStudentIdAndCourseId_whenAddCourse_thenSuccessResponse() throws Exception {
        Course persistCourse = persistCourseWithTeacher();
        Student persistStudent = persistStudent();

        Course courseTransient = courseRepository.save(persistCourse);
        Student studentTransient = studentRepository.save(persistStudent);

        ResultActions result = mockMvc.perform(post("/api/v1/students/%d/courses/%d".formatted(studentTransient.getId(), courseTransient.getId())));

        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(studentTransient.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is("John Doe")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is("john.doe@example.com")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.courses.[0].id", CoreMatchers.is(courseTransient.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.courses.[0].title", CoreMatchers.is("Math")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.courses.[0].teacher.id", CoreMatchers.is(courseTransient.getTeacher().getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.courses.[0].teacher.name", CoreMatchers.is("John Toy")));
    }

    @Test
    @DisplayName("Test find all student's courses functionality")
    void givenStudentId_whenFindCourses_thenSuccessResponse() throws Exception {
        Student persistStudent = persistFilledStudent();

        Student student = studentRepository.save(persistStudent);
        int courseId = student.getCourses().iterator().next().getId().intValue();
        int teacherId = student.getCourses().iterator().next().getTeacher().getId().intValue();

        ResultActions result = mockMvc.perform(get("/api/v1/students/%d/courses".formatted(student.getId())));

        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id", CoreMatchers.is(courseId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].title", CoreMatchers.is("Math")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].teacher.id", CoreMatchers.is(teacherId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].teacher.name", CoreMatchers.is("John Toy")));
    }

}
