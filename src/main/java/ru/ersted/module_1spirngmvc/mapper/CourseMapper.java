package ru.ersted.module_1spirngmvc.mapper;

import org.mapstruct.Mapper;
import ru.ersted.module_1spirngmvc.dto.generated.CourseDto;
import ru.ersted.module_1spirngmvc.dto.generated.CourseShortDto;
import ru.ersted.module_1spirngmvc.dto.generated.CourseCreateRq;
import ru.ersted.module_1spirngmvc.entity.Course;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    CourseDto map(Course entity);

    CourseShortDto mapShort(Course entity);

    Course map(CourseCreateRq request);

}
