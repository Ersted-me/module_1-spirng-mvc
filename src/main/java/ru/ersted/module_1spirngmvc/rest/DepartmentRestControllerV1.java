package ru.ersted.module_1spirngmvc.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ersted.module_1spirngmvc.dto.generated.DepartmentDto;
import ru.ersted.module_1spirngmvc.dto.generated.DepartmentCreateRq;
import ru.ersted.module_1spirngmvc.service.DepartmentService;

@RestController
@RequestMapping("api/v1/departments")
@RequiredArgsConstructor
public class DepartmentRestControllerV1 {

    private final DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<DepartmentDto> create(@RequestBody DepartmentCreateRq request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(departmentService.save(request));
    }

    @PostMapping("/{departmentId}/teacher/{teacherId}")
    public DepartmentDto assigningHeadOfDepartment(@PathVariable Long departmentId, @PathVariable Long teacherId) {
        return departmentService.assigningHeadOfDepartment(departmentId, teacherId);
    }
}
