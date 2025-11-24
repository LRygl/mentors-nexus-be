package com.mentors.applicationstarter.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mentors.applicationstarter.DTO.CourseSectionDTO;
import com.mentors.applicationstarter.Enum.CourseLevel;
import com.mentors.applicationstarter.Enum.CourseStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Course extends BaseEntity{

    private String name;
    private String description;
    private String imageUrl;

    //TODO Create CRUD for Category Management
    private String category;
    //TODO Separate in public, private, unpublished
    private CourseStatus status;
    @Column(nullable = false)
    private BigDecimal price;
    private Instant publishedAt;
    private Boolean published;
    private Boolean featured;


    @Enumerated(EnumType.STRING)
    private CourseLevel level;


    // RELATIONS Definitions

    // LABEL JOIN
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "course_label",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "label_id")
    )
    @Builder.Default
    @ToString.Exclude
    private Set<Label> labels = new HashSet<>();

    // CATEGORY JOIN
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "course_category",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Builder.Default
    @ToString.Exclude
    private Set<Category> categories = new HashSet<>();

    //OWNER JOIN - MANYTOONE
    @ManyToOne
    @JoinColumn(name = "owner_id")
    @JsonIgnore
    private User owner;

    //STUDENTS JOIN - MANYTOMANY
    @ManyToMany
    @JoinTable(
            name = "course_student",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private Set<User> students = new HashSet<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CourseRating> ratings = new HashSet<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<CourseSection> sections = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course course)) return false;
        return getId() != null && getId().equals(course.getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }

    public int getTotalDuration() {
        return sections == null ? 0 :
                sections.stream()
                        .filter(sec -> sec.getLessons() != null)
                        .flatMap(sec -> sec.getLessons().stream())
                        .mapToInt(Lesson::getDuration)
                        .sum();
    }

    public double getAverageRating() {
        return ratings.isEmpty()
                ? 0
                : ratings.stream().mapToInt(CourseRating::getRating).average().orElse(0);
    }
}
