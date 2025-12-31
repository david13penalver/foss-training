package com.david13penalver.foss_training_api.domain.model;

public enum EquipmentCategory {
    BODYWEIGHT("Bodyweight Only"),
    FREE_WEIGHTS("Free Weights"),
    RESISTANCE("Resistance Equipment"),
    MACHINES("Weight Machines"),
    CARDIO("Cardio Equipment"),
    FUNCTIONAL("Functional Training"),
    ACCESSORIES("Accessories & Tools");

    private final String displayName;

    EquipmentCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
