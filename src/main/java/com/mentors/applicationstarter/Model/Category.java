package com.mentors.applicationstarter.Model;

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

    @Column(unique = true, nullable = false)
    private String name;
    private Instant created;
    private Instant updated;

    @ManyToMany(mappedBy = "categories")
    @Builder.Default
    @ToString.Exclude
    private Set<Course> courses = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id != null && id.equals(category.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

}
