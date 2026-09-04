package com.david13penalver.foss_training_api.domain.model.session;

import lombok.Getter;

@Getter
public enum SetType {

    WARMUP("Warm-up", "Preparatory set at lighter loads to prime muscles and joints"),
    WORKING("Working", "Primary training set at target intensity"),
    DROP_SET("Drop Set", "Immediately reduced weight set performed after a working set without rest"),
    MYOREP("Myo-Rep", "Short rest-pause cluster set to maximise effective reps near failure"),
    FAILURE("Failure", "Set taken to momentary muscular failure");

    private final String displayName;
    private final String description;

    SetType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public boolean countsAsWorkingVolume() {
        return this != WARMUP;
    }

    public static SetType fromString(String text) {
        for (SetType type : SetType.values()) {
            if (type.name().equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No set type with name " + text + " found");
    }
}
