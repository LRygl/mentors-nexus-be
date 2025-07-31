package com.mentors.applicationstarter.Model;

import com.mentors.applicationstarter.Enum.EventCategory;
import com.mentors.applicationstarter.Enum.EventType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "eventGenerator")
    @SequenceGenerator(name = "eventGenerator", sequenceName = "application_event_sequence", allocationSize = 1)
    @Column(nullable = false, updatable = false)
    private Long id;

    private UUID UUID;
    private UUID resourceUUID;
    private String name;
    private String value;
    @Enumerated(EnumType.STRING)
    private EventCategory category;
    @Enumerated(EnumType.STRING)
    private EventType type;
    private String origin;
    @CreationTimestamp
    private Instant timestamp;
}
