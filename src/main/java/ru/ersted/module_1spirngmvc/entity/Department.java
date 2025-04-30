package ru.ersted.module_1spirngmvc.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "department")
public class Department {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "department_id_generator"
    )
    @SequenceGenerator(
            name = "department_id_generator",
            sequenceName = "department_id_seq"
    )
    private Long id;

    @Column
    private String name;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "head_of_department_id", referencedColumnName = "id")
    private Teacher headOfDepartment;

}
