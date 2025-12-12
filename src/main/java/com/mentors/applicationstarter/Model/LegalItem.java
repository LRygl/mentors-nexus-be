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
public class LegalItem extends BaseEntity {
    @Column(columnDefinition = "TEXT")
    private String content;
    private Integer orderIndex;

    @ManyToOne
    @JoinColumn(name = "section_id")
    private LegalSection section;

    @ManyToOne
    @JoinColumn(name = "parent_item_id")
    private LegalItem parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LegalItem> subItems = new ArrayList<>();

}
