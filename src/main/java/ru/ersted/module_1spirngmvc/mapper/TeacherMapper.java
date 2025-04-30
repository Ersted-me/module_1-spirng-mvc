package ru.ersted.module_1spirngmvc.mapper;

import org.mapstruct.Mapper;
import ru.ersted.module_1spirngmvc.dto.teacher.TeacherDto;
import ru.ersted.module_1spirngmvc.dto.teacher.TeacherShortDto;
import ru.ersted.module_1spirngmvc.dto.teacher.rq.TeacherCreateRq;
import ru.ersted.module_1spirngmvc.entity.Teacher;

@Mapper(componentModel = "spring")
public interface TeacherMapper {

    Teacher map(TeacherCreateRq request);

    TeacherDto map(Teacher teacher);

    TeacherShortDto mapShort(Teacher teacher);

}
