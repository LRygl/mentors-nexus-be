package com.mentors.applicationstarter.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Lesson {

    @Id
    @Column(nullable = false, updatable = false)
    private Long id;
    private UUID UUID;
    private String title;
    private String description;
    private String videoUrl;
    private Duration length;
    private String course;
    private Integer orderIndex;
    //TODO
    //CourseSection

}
