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
import org.springframework.transaction.annotation.Transactional;
import ru.ersted.module_1spirngmvc.config.DatabaseConfig;
import ru.ersted.module_1spirngmvc.dto.generated.TeacherCreateRq;
import ru.ersted.module_1spirngmvc.entity.Student;
import ru.ersted.module_1spirngmvc.entity.Teacher;
import ru.ersted.module_1spirngmvc.repository.DepartmentRepository;
import ru.ersted.module_1spirngmvc.repository.TeacherRepository;
import ru.ersted.module_1spirngmvc.repository.jpa.CourseJpaRepository;
import ru.ersted.module_1spirngmvc.repository.jpa.StudentJpaRepository;
import ru.ersted.module_1spirngmvc.repository.jpa.TeacherJpaRepository;

import java.util.Collections;

import static ru.ersted.module_1spirngmvc.util.DataUtil.*;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import({DatabaseConfig.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class ItTeacherRestControllerV1Test {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private TeacherJpaRepository teacherJpaRepository;

    @Autowired
    private CourseJpaRepository courseRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private StudentJpaRepository studentRepository;

    @BeforeEach
    public void setUp() {
        teacherRepository.deleteAll();
        courseRepository.deleteAll();
        departmentRepository.deleteAll();
    }

    @Test
    @DisplayName("Test create teacher functionality")
    void givenTeacherCreateRq_whenCreate_thenSuccessResponse() throws Exception {
        TeacherCreateRq rq = teacherCreateRq();

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rq)));

        int teacherId = teacherJpaRepository.findAll().iterator().next().getId().intValue();

        result
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(teacherId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is("John Toy")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.courses", CoreMatchers.is(Collections.emptyList())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.department", CoreMatchers.nullValue()));
    }

    @Test
    @DisplayName("Test assigning teacher to course functionality")
    void givenTeacherIdAndCourseId_whenAssigningTeacherToCourse_thenSuccessResponse() throws Exception {
        Student persistStudent = persistStudentWithCourse();
        Student student = studentRepository.saveAndFlush(persistStudent);

        Teacher persistTeacher = persistTeacher();
        Teacher teacher = teacherRepository.save(persistTeacher);

        int teacherId = teacher.getId().intValue();
        int courseId = student.getCourses().iterator().next().getId().intValue();
        int studentId = student.getId().intValue();

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/teachers/%d/courses/%d".formatted(teacher.getId(), courseId)));

        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(courseId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", CoreMatchers.is("Math")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.teacher.id", CoreMatchers.is(teacherId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.teacher.name", CoreMatchers.is("John Toy")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.students.[0].id", CoreMatchers.is(studentId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.students.[0].name", CoreMatchers.is("John Doe")));
    }

    @Test
    @Transactional
    @DisplayName("Test find all teachers functionality")
    void whenFindAll_thenSuccessResponse() throws Exception {
        Teacher persistFilledTeacher = persistFilledTeacher();
        Teacher teacher = teacherRepository.save(persistFilledTeacher);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/teachers"));

        int teacherId = teacher.getId().intValue();
        int departmentId = teacher.getDepartment().getId().intValue();
        int courseId = teacher.getCourses().iterator().next().getId().intValue();

        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id", CoreMatchers.is(teacherId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].name", CoreMatchers.is("John Toy")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].courses.[0].id", CoreMatchers.is(courseId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].courses.[0].title", CoreMatchers.is("Math")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].department.id", CoreMatchers.is(departmentId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].department.name", CoreMatchers.is("Math Department")));
    }

}
