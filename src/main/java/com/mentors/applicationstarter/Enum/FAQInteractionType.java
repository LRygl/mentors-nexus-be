package com.mentors.applicationstarter.Enum;

public enum FAQInteractionType {
    /**
     * User viewed the FAQ (expanded it)
     */
    VIEW,

    /**
     * User voted the FAQ as helpful (first time)
     */
    VOTE_HELPFUL,

    /**
     * User voted the FAQ as not helpful (first time)
     */
    VOTE_NOT_HELPFUL,

    /**
     * User removed their helpful vote (toggle off)
     */
    VOTE_REMOVED_HELPFUL,

    /**
     * User removed their not helpful vote (toggle off)
     */
    VOTE_REMOVED_NOT_HELPFUL,

    /**
     * User changed their vote from not helpful to helpful
     */
    VOTE_CHANGED_TO_HELPFUL,

    /**
     * User changed their vote from helpful to not helpful
     */
    VOTE_CHANGED_TO_NOT_HELPFUL;

    /**
     * Check if this interaction type represents an active vote
     */
    public boolean isActiveVote() {
        return this == VOTE_HELPFUL || this == VOTE_NOT_HELPFUL;
    }

    /**
     * Check if this interaction type represents a vote removal
     */
    public boolean isVoteRemoval() {
        return this == VOTE_REMOVED_HELPFUL || this == VOTE_REMOVED_NOT_HELPFUL;
    }

    /**
     * Check if this interaction type represents a vote change
     */
    public boolean isVoteChange() {
        return this == VOTE_CHANGED_TO_HELPFUL || this == VOTE_CHANGED_TO_NOT_HELPFUL;
    }

    /**
     * Get the resulting vote state after this interaction
     * Returns null for vote removals
     */
    public FAQInteractionType getResultingVoteState() {
        return switch (this) {
            case VOTE_HELPFUL, VOTE_CHANGED_TO_HELPFUL -> VOTE_HELPFUL;
            case VOTE_NOT_HELPFUL, VOTE_CHANGED_TO_NOT_HELPFUL -> VOTE_NOT_HELPFUL;
            case VOTE_REMOVED_HELPFUL, VOTE_REMOVED_NOT_HELPFUL -> null;
            default -> null;
        };
    }
}