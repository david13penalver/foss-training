package com.david13penalver.foss_training_api.domain.model.athlete;

import lombok.Getter;

@Getter
public enum Gender {

    MALE("Male"),
    FEMALE("Female");

    private final String displayName;

    Gender(String displayName) {
        this.displayName = displayName;
    }

    public static Gender fromString(String text) {
        if (text == null) {
            return null;
        }
        for (Gender g : Gender.values()) {
            if (g.name().equalsIgnoreCase(text) || g.displayName.equalsIgnoreCase(text)) {
                return g;
            }
        }
        throw new IllegalArgumentException("No gender found for value: " + text);
    }
}
