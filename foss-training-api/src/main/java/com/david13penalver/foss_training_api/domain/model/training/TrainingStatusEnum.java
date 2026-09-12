package com.david13penalver.foss_training_api.domain.model.training;

import lombok.Getter;

@Getter
public enum TrainingStatusEnum {

    PLANNED(
            "Planned",
            "Training is scheduled but not yet started.",
            true,
            false),

    IN_PROGRESS(
            "In Progress",
            "Training is actively being performed.",
            false,
            false),

    PAUSED(
            "Paused",
            "Training temporarily halted but can be resumed.",
            false,
            false),

    COMPLETED(
            "Completed",
            "Training successfully finished.",
            false,
            true),

    PARTIALLY_COMPLETED(
            "Partially Completed",
            "Training ended early with some exercises completed.",
            false,
            true),

    SKIPPED(
            "Skipped",
            "Scheduled training intentionally not performed.",
            false,
            true),

    CANCELLED(
            "Cancelled",
            "Training permanently cancelled and removed from schedule.",
            false,
            true);

    private final String name;
    private final String description;
    private final boolean isEditable;
    private final boolean isFinal;

    TrainingStatusEnum(String name, String description, boolean isEditable, boolean isFinal) {
        this.name = name;
        this.description = description;
        this.isEditable = isEditable;
        this.isFinal = isFinal;
    }

    public boolean canStart() {
        return this == PLANNED;
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
        return this == PLANNED || this == PAUSED;
    }

    @com.fasterxml.jackson.annotation.JsonCreator
    public static TrainingStatusEnum fromString(String text) {
        if (text == null) {
            return null;
        }
        for (TrainingStatusEnum status : TrainingStatusEnum.values()) {
            if (status.name().equalsIgnoreCase(text) || status.name.equalsIgnoreCase(text)) {
                return status;
            }
        }
        String normalized = text.trim().toUpperCase().replace(" ", "_");
        for (TrainingStatusEnum status : TrainingStatusEnum.values()) {
            if (status.name().equalsIgnoreCase(normalized)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No training status with name " + text + " found");
    }

    @Override
    public String toString() {
        return this.name;
    }
}
