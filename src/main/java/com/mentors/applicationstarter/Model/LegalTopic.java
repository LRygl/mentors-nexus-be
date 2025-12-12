package com.mentors.applicationstarter.Model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;

import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class LegalTopic extends BaseEntity {

    private String name;
    private String subtitle;
    private Instant effectiveAt;
    private Instant publishedAt;
    private UUID publishedBy;
    private Boolean published;
    private Boolean showCta;
    private String footer;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LegalSection> sections = new ArrayList<>();

}
