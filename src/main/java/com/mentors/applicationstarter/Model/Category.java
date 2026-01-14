package com.mentors.applicationstarter.Model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Category extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String name;
    private String description;
    private String color;

    @ManyToMany(mappedBy = "categories")
    @Builder.Default
    @ToString.Exclude
    private Set<Course> courses = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return getId() != null && getId().equals(category.getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }


}
