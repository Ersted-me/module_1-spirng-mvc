package ru.ersted.module_1spirngmvc.mapper;

import org.mapstruct.Mapper;
import ru.ersted.module_1spirngmvc.dto.generated.DepartmentDto;
import ru.ersted.module_1spirngmvc.dto.generated.DepartmentShortDto;
import ru.ersted.module_1spirngmvc.dto.generated.DepartmentCreateRq;
import ru.ersted.module_1spirngmvc.entity.Department;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    Department map(DepartmentCreateRq request);

    DepartmentDto map(Department department);

    DepartmentShortDto mapShort(Department department);

}
