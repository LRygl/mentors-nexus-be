package com.mentors.applicationstarter.Model;

import jakarta.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class LegalTopic extends BaseEntity {

    private String name;
    private String subtitle;
    private Instant effectiveAt;
    private Boolean showCta;
    private String footer;

}
