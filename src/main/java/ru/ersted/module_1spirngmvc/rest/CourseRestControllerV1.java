package ru.ersted.module_1spirngmvc.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ersted.module_1spirngmvc.dto.generated.CourseDto;
import ru.ersted.module_1spirngmvc.dto.generated.CourseCreateRq;
import ru.ersted.module_1spirngmvc.service.CourseService;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/courses")
public class CourseRestControllerV1 {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<CourseDto> create(@RequestBody CourseCreateRq courseDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(courseService.save(courseDto));
    }

    @GetMapping
    public Collection<CourseDto> getAll(@PageableDefault(page = 0, size = 20) Pageable pageable) {
        return courseService.findAll(pageable).getContent();
    }

}
