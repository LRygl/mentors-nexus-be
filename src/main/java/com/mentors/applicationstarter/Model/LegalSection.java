package com.mentors.applicationstarter.Model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class LegalSection extends BaseEntity {
    private String name;
    private String icon;
    private Integer orderIndex;
}
