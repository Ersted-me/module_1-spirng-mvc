package ru.ersted.module_1spirngmvc.mapper;

import org.mapstruct.Mapper;
import ru.ersted.module_1spirngmvc.dto.department.DepartmentDto;
import ru.ersted.module_1spirngmvc.dto.department.DepartmentShortDto;
import ru.ersted.module_1spirngmvc.dto.department.rq.DepartmentCreateRq;
import ru.ersted.module_1spirngmvc.entity.Department;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    Department map(DepartmentCreateRq request);

    DepartmentDto map(Department department);

    DepartmentShortDto mapShort(Department department);

}
