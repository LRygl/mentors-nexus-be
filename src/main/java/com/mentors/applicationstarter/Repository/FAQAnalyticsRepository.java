package com.mentors.applicationstarter.Repository;

import com.mentors.applicationstarter.Enum.FAQInteractionType;
import com.mentors.applicationstarter.Model.FAQAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FAQAnalyticsRepository extends JpaRepository<FAQAnalytics, Long> {


    // Find user's most recent vote on this FAQ
    Optional<FAQAnalytics> findFirstByFaqUuidAndSessionIdAndIpAddressAndInteractionTypeInOrderByTimestampDesc(
            UUID faqUuid,
            String sessionId,
            String ipAddress,
            List<FAQInteractionType> interactionTypes
    );

    // Count votes by type for analytics
    long countByFaqUuidAndInteractionType(UUID faqUuid, FAQInteractionType interactionType);

    // Optional: Get all interactions for a FAQ (for analytics dashboard)
    List<FAQAnalytics> findByFaqUuidOrderByTimestampDesc(UUID faqUuid);

    /**
     * Find the most recent vote for a FAQ by session + IP combination
     * Only looks back 24 hours to avoid stale data
     */
    @Query("""
        SELECT fa FROM FAQAnalytics fa 
        WHERE fa.sessionId = :sessionId 
        AND fa.ipAddress = :ipAddress 
        AND fa.faqUuid = :faqUuid 
        AND fa.timestamp > :since
        AND fa.interactionType IN (
            'VOTE_HELPFUL', 
            'VOTE_NOT_HELPFUL',
            'VOTE_CHANGED_TO_HELPFUL',
            'VOTE_CHANGED_TO_NOT_HELPFUL',
            'VOTE_REMOVED_HELPFUL',
            'VOTE_REMOVED_NOT_HELPFUL'
        )
        ORDER BY fa.timestamp DESC 
        LIMIT 1
    """)
    Optional<FAQAnalytics> findLatestVoteBySessionAndIpAndFaq(
            @Param("sessionId") String sessionId,
            @Param("ipAddress") String ipAddress,
            @Param("faqUuid") UUID faqUuid,
            @Param("since") Instant since
    );

    /**
     * Find the most recent view for a FAQ by session + IP combination
     * Used to prevent duplicate view counting within a time window
     */
    @Query("""
        SELECT fa FROM FAQAnalytics fa 
        WHERE fa.sessionId = :sessionId 
        AND fa.ipAddress = :ipAddress 
        AND fa.faqUuid = :faqUuid 
        AND fa.interactionType = 'VIEW'
        AND fa.timestamp > :since
        ORDER BY fa.timestamp DESC 
        LIMIT 1
    """)
    Optional<FAQAnalytics> findRecentViewBySessionAndIpAndFaq(
            @Param("sessionId") String sessionId,
            @Param("ipAddress") String ipAddress,
            @Param("faqUuid") UUID faqUuid,
            @Param("since") Instant since
    );




}
