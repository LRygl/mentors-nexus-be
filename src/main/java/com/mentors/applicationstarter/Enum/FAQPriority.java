package com.mentors.applicationstarter.Enum;

public enum FAQPriority {
    LOW("Low Priority"),
    NORMAL("Normal Priority"),
    HIGH("High Priority"),
    URGENT("Urgent");

    private final String displayName;

    FAQPriority(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
