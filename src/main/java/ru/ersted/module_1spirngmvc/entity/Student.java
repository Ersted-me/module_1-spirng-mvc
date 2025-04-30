package ru.ersted.module_1spirngmvc.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ersted.module_1spirngmvc.dto.student.rq.StudentUpdateRq;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "student")
public class Student {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "student_id_generator"
    )
    @SequenceGenerator(
            name = "student_id_generator",
            sequenceName = "student_id_seq"
    )
    private Long id;

    @Column
    private String name;

    @Column
    private String email;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "students_courses",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> courses = new HashSet<>();


    public void update(StudentUpdateRq request) {
        this.setName(request.name());
        this.setEmail(request.email());
    }

}
