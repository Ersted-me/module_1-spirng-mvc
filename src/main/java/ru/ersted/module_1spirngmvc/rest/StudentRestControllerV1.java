package ru.ersted.module_1spirngmvc.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ersted.module_1spirngmvc.dto.generated.CourseShortDto;
import ru.ersted.module_1spirngmvc.dto.generated.StudentDto;
import ru.ersted.module_1spirngmvc.dto.generated.StudentCreateRq;
import ru.ersted.module_1spirngmvc.dto.generated.StudentUpdateRq;
import ru.ersted.module_1spirngmvc.service.CourseService;
import ru.ersted.module_1spirngmvc.service.StudentService;

import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/students")
public class StudentRestControllerV1 {

    private final StudentService studentService;
    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<StudentDto> create(@RequestBody StudentCreateRq request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(studentService.create(request));
    }

    @GetMapping
    public Collection<StudentDto> findAll() {
        return studentService.findAll();
    }

    @GetMapping("/{id}")
    public StudentDto findById(@PathVariable Long id) {
        return studentService.find(id);
    }

    @PutMapping("/{id}")
    public StudentDto update(@PathVariable Long id, @RequestBody StudentUpdateRq request) {
        return studentService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        studentService.delete(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(Map.of("message", "Student deleted successfully"));
    }

    @PostMapping("/{studentId}/courses/{courseId}")
    public StudentDto addCourse(@PathVariable Long studentId, @PathVariable Long courseId) {
        return studentService.addCourse(studentId, courseId);
    }

    @GetMapping("/{studentId}/courses")
    public Collection<CourseShortDto> findCourses(@PageableDefault(page = 0, size = 20) Pageable pageable,
                                             @PathVariable Long studentId) {
        return courseService.findAllByStudentId(studentId, pageable).getContent();
    }


}
