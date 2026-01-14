package com.mentors.applicationstarter.Model;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "themes")
public class Theme extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb", nullable = false)
    private String configuration;

    private Boolean isActive = false;

    private Boolean isSystemTheme = false;


}
