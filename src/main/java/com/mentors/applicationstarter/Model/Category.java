package com.mentors.applicationstarter.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "categoryGenerator")
    @SequenceGenerator(name = "categoryGenerator", sequenceName = "application_category_sequence", allocationSize = 50)
    @Column(nullable = false, updatable = false)
    private Long id;
    private UUID UUID;
    private String name;
    private Instant created;
    private Instant updated;

    //

}
