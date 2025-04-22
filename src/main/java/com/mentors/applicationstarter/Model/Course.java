package com.mentors.applicationstarter.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
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
    private String labels;
    //TODO Create CRUD for Category Management
    private String category;
    //TODO Separate in public, private, unpublished
    private String status;
    private String price;

    private Instant created;
    private Instant updated;
    private Instant published;

    private String courseOwner;
    private String students;
    private String lessons;


}
