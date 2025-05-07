package ru.ersted.module_1spirngmvc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ersted.module_1spirngmvc.dto.generated.TeacherDto;
import ru.ersted.module_1spirngmvc.dto.generated.TeacherCreateRq;
import ru.ersted.module_1spirngmvc.entity.Teacher;
import ru.ersted.module_1spirngmvc.exception.NotFoundException;
import ru.ersted.module_1spirngmvc.mapper.TeacherMapper;
import ru.ersted.module_1spirngmvc.repository.TeacherRepository;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;

    private final TeacherMapper teacherMapper;


    @Transactional
    public TeacherDto create(TeacherCreateRq request) {
        Teacher newTeacher = teacherMapper.map(request);
        Teacher savedTeacher = teacherRepository.save(newTeacher);

        return teacherMapper.map(savedTeacher);
    }

    public Teacher findOrElseThrow(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Teacher with ID %d not found".formatted(id)));
    }

    public Slice<TeacherDto> findAll(Pageable pageable) {
        return teacherRepository.findAll(pageable)
                .map(teacherMapper::map);
    }

}
