package ru.ersted.module_1spirngmvc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ersted.module_1spirngmvc.dto.generated.StudentDto;
import ru.ersted.module_1spirngmvc.dto.generated.StudentCreateRq;
import ru.ersted.module_1spirngmvc.dto.generated.StudentUpdateRq;
import ru.ersted.module_1spirngmvc.entity.Course;
import ru.ersted.module_1spirngmvc.entity.Student;
import ru.ersted.module_1spirngmvc.exception.NotFoundException;
import ru.ersted.module_1spirngmvc.mapper.StudentMapper;
import ru.ersted.module_1spirngmvc.repository.StudentRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    private final CourseService courseService;

    @Transactional
    public StudentDto create(StudentCreateRq request) {
        Student newStudent = studentMapper.map(request);
        studentRepository.save(newStudent);

        return studentMapper.map(newStudent);
    }

    public Collection<StudentDto> findAll() {
        return studentRepository.findAll().stream()
                .map(studentMapper::map)
                .collect(Collectors.toList());
    }

    public StudentDto find(Long id) {
        Student foundStudent = findOrElseThrow(id);
        return studentMapper.map(foundStudent);
    }

    public Student findOrElseThrow(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Student with ID %d not found".formatted(id)));
    }

    @Transactional
    public StudentDto update(Long id, StudentUpdateRq request) {
        Student foundStudent = findOrElseThrow(id);

        foundStudent.setName(request.getName());
        foundStudent.setEmail(request.getEmail());

        studentRepository.save(foundStudent);

        return studentMapper.map(foundStudent);
    }

    @Transactional
    public void delete(Long id) {
        studentRepository.deleteById(id);
    }

    @Transactional
    public StudentDto addCourse(Long studentId, Long courseId) {
        Student foundStudent = findOrElseThrow(studentId);
        Course foundCourse = courseService.findOrElseThrow(courseId);

        foundStudent.getCourses().add(foundCourse);
        studentRepository.save(foundStudent);

        return studentMapper.map(foundStudent);
    }

}
