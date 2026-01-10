package com.david13penalver.foss_training_api.domain.model.session;

public enum SessionStatusEnum {

    PLANNED(
            "Planned",
            "Session is scheduled but not yet started. Part of the training program awaiting execution.",
            true,
            false),

    DRAFT(
            "Draft",
            "Session is being created or edited. Not yet finalized or scheduled for execution.",
            true,
            false),

    READY(
            "Ready to Start",
            "Session is finalized, scheduled, and ready to begin. All preparation is complete.",
            true,
            false),

    IN_PROGRESS(
            "In Progress",
            "Session is actively being performed. Real-time tracking of exercises, sets, and reps.",
            false,
            false),

    PAUSED(
            "Paused",
            "Session temporarily halted but can be resumed. Timer stopped, progress saved automatically.",
            false,
            false),

    COMPLETED(
            "Completed",
            "Session successfully finished with all planned exercises logged. Data available for analysis and progress tracking.",
            false,
            true),

    PARTIALLY_COMPLETED(
            "Partially Completed",
            "Session ended early with some exercises completed. Counts toward training volume but marked as incomplete.",
            false,
            true),

    SKIPPED(
            "Skipped",
            "Planned session intentionally not performed. Tracked for adherence metrics and program adjustments.",
            false,
            true),

    MISSED(
            "Missed",
            "Scheduled session not performed without intentional decision. Indicates potential adherence issues.",
            false,
            true),

    CANCELLED(
            "Cancelled",
            "Session permanently cancelled and removed from schedule. Does not count toward training adherence.",
            false,
            true),

    FAILED(
            "Failed/Abandoned",
            "Session started but abandoned due to injury, illness, equipment failure, or other unforeseen circumstances.",
            false,
            true);

    private final String name;
    private final String description;
    private final boolean isEditable;
    private final boolean isFinal;

    SessionStatusEnum(String name, String description, boolean isEditable,
            boolean isFinal) {
        this.name = name;
        this.description = description;
        this.isEditable = isEditable;
        this.isFinal = isFinal;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public boolean isFinal() {
        return isFinal;
    }

    // Helper methods for session lifecycle management
    public boolean canStart() {
        return this == PLANNED || this == DRAFT || this == READY;
    }

    public boolean canResume() {
        return this == PAUSED;
    }

    public boolean canPause() {
        return this == IN_PROGRESS;
    }

    public boolean canComplete() {
        return this == IN_PROGRESS || this == PAUSED;
    }

    public boolean canCancel() {
        return this == PLANNED || this == DRAFT || this == READY || this == PAUSED;
    }

    public boolean countsTowardAdherence() {
        return this == COMPLETED || this == PARTIALLY_COMPLETED;
    }

    public boolean countsAsNonAdherence() {
        return this == SKIPPED || this == MISSED;
    }

    public boolean requiresRecoveryTracking() {
        return this == COMPLETED || this == PARTIALLY_COMPLETED;
    }

    public boolean allowsDataModification() {
        return this == DRAFT || this == PLANNED || this == READY;
    }

    public String getDisplayColor() {
        return switch (this) {
            case PLANNED, READY -> "#4CAF50"; // Green
            case DRAFT -> "#9E9E9E"; // Gray
            case IN_PROGRESS -> "#2196F3"; // Blue
            case PAUSED -> "#FF9800"; // Orange
            case COMPLETED -> "#00C853"; // Success Green
            case PARTIALLY_COMPLETED -> "#FFC107"; // Amber
            case SKIPPED, MISSED -> "#F44336"; // Red
            case CANCELLED -> "#757575"; // Dark Gray
            case FAILED -> "#D32F2F"; // Dark Red
        };
    }

    public static SessionStatusEnum fromString(String text) {
        for (SessionStatusEnum status : SessionStatusEnum.values()) {
            if (status.name().equalsIgnoreCase(text)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No session status with name " + text + " found");
    }

    @Override
    public String toString() {
        return this.name;
    }
}
