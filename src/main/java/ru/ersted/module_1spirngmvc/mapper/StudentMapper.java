package ru.ersted.module_1spirngmvc.mapper;


import org.mapstruct.Mapper;
import ru.ersted.module_1spirngmvc.dto.generated.StudentDto;
import ru.ersted.module_1spirngmvc.dto.generated.StudentCreateRq;
import ru.ersted.module_1spirngmvc.entity.Student;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    Student map(StudentCreateRq request);

    StudentDto map(Student entity);

}
