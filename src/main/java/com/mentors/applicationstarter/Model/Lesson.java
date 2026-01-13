package com.mentors.applicationstarter.Model;

import com.mentors.applicationstarter.Enum.LessonCategory;
import com.mentors.applicationstarter.Enum.LessonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Lesson extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "lessonGenerator")
    @SequenceGenerator(name = "lessonGenerator", sequenceName = "application_lesson_sequence", allocationSize = 50)
    @Column(nullable = false, updatable = false)
    private Long id;
    private UUID uuid;
    private String title;
    private String description;
    private String imageUrl;
    private String videoUrl;
    private Integer duration;
    private Integer orderIndex;
    private LessonType type; //PAID or FREE
    private LessonCategory category; //Video/Text/Quiz/Document

    @ManyToOne
    @JoinColumn(name = "section_id")
    private CourseSection section;

}
