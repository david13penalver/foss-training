package com.david13penalver.foss_training_api.domain.model;

public enum Joint {

    // Upper Body Joints
    NECK("Neck", "Cervical spine joints"),
    SHOULDER("Shoulder", "Glenohumeral joint"),
    ELBOW("Elbow", "Humeroulnar and humeroradial joints"),
    WRIST("Wrist", "Radiocarpal joint"),

    // Spine
    THORACIC_SPINE("Thoracic Spine", "Mid-back vertebrae"),
    LUMBAR_SPINE("Lumbar Spine", "Lower back vertebrae"),

    // Lower Body Joints
    HIP("Hip", "Acetabulofemoral joint"),
    KNEE("Knee", "Tibiofemoral joint"),
    ANKLE("Ankle", "Talocrural joint"),

    // Other
    FULL_BODY("Full Body", "Multiple joints involved");

    private final String displayName;
    private final String description;

    Joint(String displayName, String description) {
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
