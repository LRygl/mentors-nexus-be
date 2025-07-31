package com.mentors.applicationstarter.Enum;

public enum FAQStatus {
    DRAFT("Draft"),
    REVIEW("Under Review"),
    PUBLISHED("Published"),
    ARCHIVED("Archived");

    private final String displayName;

    FAQStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
