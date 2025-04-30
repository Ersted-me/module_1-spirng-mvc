package ru.ersted.module_1spirngmvc.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ersted.module_1spirngmvc.dto.course.CourseDto;
import ru.ersted.module_1spirngmvc.dto.course.rq.CourseCreateRq;
import ru.ersted.module_1spirngmvc.service.CourseService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/courses")
public class CourseRestController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<CourseDto> create(@RequestBody CourseCreateRq courseDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(courseService.save(courseDto));
    }

    @GetMapping
    public List<CourseDto> getAll() {
        return courseService.findAll();
    }

}
