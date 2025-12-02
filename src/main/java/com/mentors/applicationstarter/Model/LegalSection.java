package com.mentors.applicationstarter.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class LegalSection extends BaseEntity {
    private String name;
    private String icon;
    private Integer orderIndex;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private LegalTopic topic;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LegalItem> items = new ArrayList<>();
}
