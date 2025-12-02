package com.mentors.applicationstarter.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private Instant archivedAt;
    @Column(nullable = false)
    private boolean deleted = false;
    @Column(nullable = false)
    private boolean archived = false;
    private UUID createdBy;
    private UUID updatedBy;
    private UUID deletedBy;
    private UUID archivedBy;
}
