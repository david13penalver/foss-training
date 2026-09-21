package com.david13penalver.foss_training_api.domain.model.athlete;

public record RelativeStrengthScore(
        Double totalWeightKg,
        Double bodyweightKg,
        Gender gender,
        Double relativeStrengthRatio,
        Double dotsScore,
        Double wilksScore,
        String classification
) {}
