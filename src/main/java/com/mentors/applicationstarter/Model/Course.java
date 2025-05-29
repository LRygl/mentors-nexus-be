package com.mentors.applicationstarter.Model;

import com.mentors.applicationstarter.Enum.CourseStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "courseGenerator")
    @SequenceGenerator(name = "courseGenerator", sequenceName = "application_course_sequence", allocationSize = 50)
    @Column(nullable = false, updatable = false)
    private Long id;
    private UUID UUID;
    private String name;

    //TODO Create CRUD for Category Management
    private String category;
    //TODO Separate in public, private, unpublished
    private CourseStatus status;
    private String price;

    private Instant created;
    private Instant updated;
    private Instant published;

    private String courseOwner;
    private String students;
    private String lessons;

    // RELATIONS Definitions

    // LABEL JOIN
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(
            name = "course_label",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "label_id")
    )
    @Builder.Default
    @ToString.Exclude
    private Set<Label> labels = new HashSet<>();

    // CATEGORY JOIN
    @ManyToMany
    @JoinTable(
            name = "course_category",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Builder.Default
    @ToString.Exclude
    private Set<Category> categories = new HashSet<>();

    //OWNER JOIN - MANYTOONE

    //STUDENTS JOIN - MANYTOMANY

    //LESSONS JOIN - ONETOMANY


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return id != null && id.equals(course.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
