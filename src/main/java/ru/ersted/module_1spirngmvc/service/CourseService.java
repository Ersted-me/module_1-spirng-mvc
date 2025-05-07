package ru.ersted.module_1spirngmvc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ersted.module_1spirngmvc.dto.generated.CourseDto;
import ru.ersted.module_1spirngmvc.dto.generated.CourseShortDto;
import ru.ersted.module_1spirngmvc.dto.generated.CourseCreateRq;
import ru.ersted.module_1spirngmvc.entity.Course;
import ru.ersted.module_1spirngmvc.entity.Teacher;
import ru.ersted.module_1spirngmvc.exception.NotFoundException;
import ru.ersted.module_1spirngmvc.mapper.CourseMapper;
import ru.ersted.module_1spirngmvc.repository.CourseRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final TeacherService teacherService;

    @Transactional
    public CourseDto save(CourseCreateRq request) {
        Course newCourse = courseMapper.map(request);
        Course savedCourse = save(newCourse);

        return courseMapper.map(savedCourse);
    }

    @Transactional
    public Course save(Course course) {
        return courseRepository.save(course);
    }

    public Slice<CourseDto> findAll(Pageable pageable) {
        return courseRepository.findAll(pageable)
                .map(courseMapper::map);
    }


    public Course findOrElseThrow(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course with ID %d not found".formatted(id)));
    }

    public Slice<CourseShortDto> findAllByStudentId(Long studentId, Pageable pageable) {
        return courseRepository.findStudentCourses(studentId, pageable)
                .map(courseMapper::mapShort);
    }

    @Transactional
    public CourseDto assigningTeacher(Long coursesId, Long teacherId) {
        Course foundCourse = findOrElseThrow(coursesId);
        Teacher foundTeacher = teacherService.findOrElseThrow(teacherId);

        foundCourse.setTeacher(foundTeacher);
        save(foundCourse);

        return courseMapper.map(foundCourse);
    }

}
