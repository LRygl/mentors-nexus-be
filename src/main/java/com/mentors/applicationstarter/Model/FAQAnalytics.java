package com.mentors.applicationstarter.Model;

import com.mentors.applicationstarter.Enum.FAQInteractionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "faq_analytics", indexes = {
        @Index(name = "idx_faq_uuid", columnList = "faq_uuid"),
        @Index(name = "idx_interaction_type", columnList = "interaction_type"),
        @Index(name = "idx_timestamp", columnList = "timestamp"),
        @Index(name = "idx_client_fingerprint", columnList = "client_fingerprint"),
        @Index(name = "idx_user_id", columnList = "user_id")
})
public class FAQAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false, unique = true)
    private UUID uuid;

    @Column(name = "faq_uuid", nullable = false)
    private UUID faqUuid;

    @Enumerated(EnumType.STRING)
    @Column(name = "interaction_type", nullable = false)
    private FAQInteractionType interactionType; // VIEW, VOTE_HELPFUL, VOTE_NOT_HELPFUL

    @Column(name = "session_id")
    private String sessionId; // For tracking unique users

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "ip_address")
    private String ipAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_interaction_type", length = 50)
    private FAQInteractionType previousInteractionType;

    @CreationTimestamp
    @Column(nullable = false)
    private Instant timestamp;
}