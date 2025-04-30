package ru.ersted.module_1spirngmvc.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ersted.module_1spirngmvc.dto.course.CourseDto;
import ru.ersted.module_1spirngmvc.dto.teacher.TeacherDto;
import ru.ersted.module_1spirngmvc.dto.teacher.rq.TeacherCreateRq;
import ru.ersted.module_1spirngmvc.service.CourseService;
import ru.ersted.module_1spirngmvc.service.TeacherService;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/teachers")
public class TeacherRestController {

    private final TeacherService teacherService;
    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<TeacherDto> create(@RequestBody TeacherCreateRq request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(teacherService.create(request));
    }

    @PostMapping("/{teacherId}/courses/{coursesId}")
    public CourseDto assigningTeacherToCourse(@PathVariable Long teacherId, @PathVariable Long coursesId) {
        return courseService.assigningTeacher(coursesId, teacherId);
    }

    @GetMapping
    public Collection<TeacherDto> findAll() {
        return teacherService.findAll();
    }

}
