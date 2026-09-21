package com.david13penalver.foss_training_api.domain.model.athlete;

import lombok.Getter;

@Getter
public enum ScoringFormula {

    DOTS("DOTS"),
    WILKS("Wilks");

    private final String displayName;

    ScoringFormula(String displayName) {
        this.displayName = displayName;
    }

    public static ScoringFormula fromString(String text) {
        if (text == null) {
            return null;
        }
        for (ScoringFormula f : ScoringFormula.values()) {
            if (f.name().equalsIgnoreCase(text) || f.displayName.equalsIgnoreCase(text)) {
                return f;
            }
        }
        throw new IllegalArgumentException("No scoring formula found for value: " + text);
    }
}
