package com.skillverse.academy.model;

public enum EventCategory {
    DEPARTMENT("Department Events", "Academic departments can publish seminars, workshops, and subject-focused sessions."),
    CLUB("Club Events", "Student clubs can showcase activities, community meetups, and special interest programs."),
    TECH_FEST("Tech Fest Events", "Flagship competitions, innovation challenges, and marquee fest experiences."),
    COLLEGE("Campus Events", "Open campus-wide events for the entire college community.");

    private final String displayName;
    private final String description;

    EventCategory(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
