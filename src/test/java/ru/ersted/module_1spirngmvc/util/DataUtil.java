package ru.ersted.module_1spirngmvc.util;

import ru.ersted.module_1spirngmvc.dto.generated.CourseDto;
import ru.ersted.module_1spirngmvc.dto.generated.CourseShortDto;
import ru.ersted.module_1spirngmvc.dto.generated.CourseBasicDto;
import ru.ersted.module_1spirngmvc.dto.generated.CourseCreateRq;
import ru.ersted.module_1spirngmvc.dto.generated.DepartmentDto;
import ru.ersted.module_1spirngmvc.dto.generated.DepartmentShortDto;
import ru.ersted.module_1spirngmvc.dto.generated.DepartmentCreateRq;
import ru.ersted.module_1spirngmvc.dto.generated.StudentDto;
import ru.ersted.module_1spirngmvc.dto.generated.StudentShortDto;
import ru.ersted.module_1spirngmvc.dto.generated.StudentCreateRq;
import ru.ersted.module_1spirngmvc.dto.generated.StudentUpdateRq;
import ru.ersted.module_1spirngmvc.dto.generated.TeacherDto;
import ru.ersted.module_1spirngmvc.dto.generated.TeacherShortDto;
import ru.ersted.module_1spirngmvc.dto.generated.TeacherCreateRq;
import ru.ersted.module_1spirngmvc.entity.Course;
import ru.ersted.module_1spirngmvc.entity.Department;
import ru.ersted.module_1spirngmvc.entity.Student;
import ru.ersted.module_1spirngmvc.entity.Teacher;

import java.util.Set;

public class DataUtil {
    private DataUtil() {
    }

    public static Teacher transientTeacher() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setName("John Toy");

        return teacher;
    }

    public static Teacher persistTeacher() {
        Teacher teacher = new Teacher();
        teacher.setName("John Toy");

        return teacher;
    }

    public static Teacher persistFilledTeacher() {
        Teacher teacher = new Teacher();
        teacher.setName("John Toy");
        teacher.setDepartment(persistDepartment());
        teacher.setCourses(Set.of(persistCourse()));
        return teacher;
    }

    public static TeacherDto transientTeacherDto() {
        TeacherDto dto = new TeacherDto();
        dto.setId(1L);
        dto.setName("John Toy");
        return dto;
    }

    public static TeacherDto transientFilledTeacherDto() {
        TeacherDto dto = new TeacherDto();
        dto.setId(1L);
        dto.setName("John Toy");
        dto.courses(Set.of(transientCourseBasicDto()));
        dto.department(departmentShortDto());

        return dto;
    }

    public static TeacherShortDto transientTeacherShortDto() {
        TeacherShortDto dto = new TeacherShortDto();
        dto.setId(1L);
        dto.setName("John Toy");
        return dto;
    }

    public static TeacherCreateRq teacherCreateRq() {
        TeacherCreateRq dto = new TeacherCreateRq();
        dto.setName("John Toy");
        return dto;
    }

    public static Student transientStudent() {
        Student student = new Student();
        student.setId(1L);
        student.setName("John Doe");
        student.setEmail("john.doe@example.com");
        return student;
    }

    public static Student persistStudent() {
        Student student = new Student();
        student.setName("John Doe");
        student.setEmail("john.doe@example.com");
        student.setCourses(Set.of());
        return student;
    }

    public static Student persistFilledStudent() {
        Student student = new Student();
        student.setName("John Doe");
        student.setEmail("john.doe@example.com");
        student.setCourses(Set.of(persistCourseWithTeacher()));
        return student;
    }

    public static Student persistStudentWithCourse() {
        Student student = new Student();
        student.setName("John Doe");
        student.setEmail("john.doe@example.com");
        student.setCourses(Set.of(persistCourse()));
        return student;
    }

    public static StudentDto transientStudentDto() {
        StudentDto dto = new StudentDto();
        dto.setId(1L);
        dto.setName("John Doe");
        dto.setEmail("john.doe@example.com");
        return dto;
    }

    public static StudentDto transientFilledStudentDto() {
        StudentDto dto = new StudentDto();
        dto.setId(1L);
        dto.setName("John Doe");
        dto.setEmail("john.doe@example.com");
        dto.courses(Set.of(transientCourseShortDto()));
        return dto;
    }

    public static StudentDto transientStudentDtoJohnathan() {
        StudentDto dto = new StudentDto();
        dto.setId(1L);
        dto.setName("Johnathan Doe");
        dto.setEmail("johnathan.doe@example.com");
        return dto;
    }

    public static StudentCreateRq studentCreateRq() {
        StudentCreateRq dto = new StudentCreateRq();
        dto.setName("John Doe");
        dto.setEmail("john.doe@example.com");
        return dto;
    }

    public static StudentUpdateRq studentUpdateRq() {
        StudentUpdateRq dto = new StudentUpdateRq();
        dto.setName("Johnathan Doe");
        dto.setEmail("johnathan.doe@example.com");
        return dto;
    }

    public static StudentShortDto transientStudentShortDto() {
        StudentShortDto dto = new StudentShortDto();
        dto.setId(1L);
        dto.setName("John Doe");
        return dto;
    }

    public static Course transientCourse() {
        return new Course(1L, "Math", null, null);
    }

    public static Course persistCourse() {
        return new Course(null, "Math", null, null);
    }

    public static Course persistCourseWithTeacher() {
        return new Course(null, "Math", persistTeacher(), null);
    }

    public static Course persistCourseWithStudents() {
        return new Course(null, "Math", null, Set.of(persistStudent()));
    }

    public static CourseShortDto transientCourseShortDto() {
        CourseShortDto dto = new CourseShortDto();
        dto.setId(1L);
        dto.setTitle("Math");
        dto.teacher(transientTeacherShortDto());
        return dto;
    }

    public static CourseBasicDto transientCourseBasicDto() {
        CourseBasicDto dto = new CourseBasicDto();
        dto.setId(1L);
        dto.setTitle("Math");
        return dto;
    }

    public static CourseDto transientCourseDto() {
        CourseDto dto = new CourseDto();
        dto.setId(1L);
        dto.setTitle("Math");
        return dto;
    }

    public static CourseDto transientCourseDtoWithTeacher() {
        CourseDto dto = new CourseDto();
        dto.setId(1L);
        dto.setTitle("Math");
        dto.teacher(transientTeacherShortDto());
        return dto;
    }


    public static CourseDto transientFilledCourseDto() {
        CourseDto dto = new CourseDto();
        dto.setId(1L);
        dto.setTitle("Math");
        dto.teacher(transientTeacherShortDto());
        dto.students(Set.of(transientStudentShortDto()));
        return dto;
    }

    public static CourseCreateRq courseCreateRq() {
        CourseCreateRq dto = new CourseCreateRq();
        dto.setTitle("Math");
        return dto;
    }

    static public Department transientDepartment() {
        return new Department(1L, "Math Department", null);
    }

    static public Department persistDepartment() {
        return new Department(null, "Math Department", null);
    }

    static public DepartmentDto transientDepartmentDto() {
        DepartmentDto dto = new DepartmentDto();
        dto.setId(1L);
        dto.setName("Math Department");
        return dto;
    }

    static public DepartmentDto transientFilledDepartmentDto() {
        DepartmentDto dto = new DepartmentDto();
        dto.setId(1L);
        dto.setName("Math Department");
        dto.setHeadOfDepartment(transientTeacherShortDto());
        return dto;
    }

    static public DepartmentCreateRq departmentCreateRq() {
        DepartmentCreateRq dto = new DepartmentCreateRq();
        dto.setName("Math Department");
        return dto;
    }

    static public DepartmentShortDto departmentShortDto() {
        DepartmentShortDto dto = new DepartmentShortDto();
        dto.setId(1L);
        dto.setName("Math Department");
        return dto;
    }

}
