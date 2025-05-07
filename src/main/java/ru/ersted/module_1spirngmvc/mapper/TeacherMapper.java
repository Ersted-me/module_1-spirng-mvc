package ru.ersted.module_1spirngmvc.mapper;

import org.mapstruct.Mapper;
import ru.ersted.module_1spirngmvc.dto.generated.TeacherDto;
import ru.ersted.module_1spirngmvc.dto.generated.TeacherShortDto;
import ru.ersted.module_1spirngmvc.dto.generated.TeacherCreateRq;
import ru.ersted.module_1spirngmvc.entity.Teacher;

@Mapper(componentModel = "spring")
public interface TeacherMapper {

    Teacher map(TeacherCreateRq request);

    TeacherDto map(Teacher teacher);

    TeacherShortDto mapShort(Teacher teacher);

}
