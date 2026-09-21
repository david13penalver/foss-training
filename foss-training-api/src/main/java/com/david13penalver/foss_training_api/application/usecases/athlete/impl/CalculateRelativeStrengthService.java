package com.david13penalver.foss_training_api.application.usecases.athlete.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.athlete.CalculateRelativeStrengthUseCase;
import com.david13penalver.foss_training_api.domain.model.athlete.Gender;
import com.david13penalver.foss_training_api.domain.model.athlete.RelativeStrengthScore;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CalculateRelativeStrengthService implements CalculateRelativeStrengthUseCase {

    @Override
    public RelativeStrengthScore execute(Double totalWeightKg, Double bodyweightKg, Gender gender) {
        log.debug("Calculating relative strength: totalWeight={}, bodyweight={}, gender={}", totalWeightKg, bodyweightKg, gender);
        if (totalWeightKg == null || totalWeightKg <= 0) {
            throw new IllegalArgumentException("Total lifted weight must be greater than 0");
        }
        if (bodyweightKg == null || bodyweightKg <= 0) {
            throw new IllegalArgumentException("Bodyweight must be greater than 0");
        }
        if (gender == null) {
            gender = Gender.MALE;
        }

        double ratio = round(totalWeightKg / bodyweightKg, 2);
        double dots = calculateDots(totalWeightKg, bodyweightKg, gender);
        double wilks = calculateWilks(totalWeightKg, bodyweightKg, gender);
        String classification = classifyStrength(dots);

        return new RelativeStrengthScore(
                totalWeightKg,
                bodyweightKg,
                gender,
                ratio,
                dots,
                wilks,
                classification
        );
    }

    private double calculateDots(double totalKg, double bwKg, Gender gender) {
        double x = bwKg;
        double denom;
        if (gender == Gender.FEMALE) {
            denom = -0.0000010706 * Math.pow(x, 4)
                    + 0.0005158568 * Math.pow(x, 3)
                    - 0.1126655495 * Math.pow(x, 2)
                    + 13.6175032 * x
                    - 57.96288;
        } else {
            denom = -0.0000010930 * Math.pow(x, 4)
                    + 0.0007391293 * Math.pow(x, 3)
                    - 0.1918759221 * Math.pow(x, 2)
                    + 24.0900786 * x
                    - 307.754178;
        }

        if (denom <= 0) {
            return 0.0;
        }
        return round(totalKg * (500.0 / denom), 2);
    }

    private double calculateWilks(double totalKg, double bwKg, Gender gender) {
        double x = bwKg;
        double denom;
        if (gender == Gender.FEMALE) {
            denom = 594.31747775582
                    - 27.23842536447 * x
                    + 0.82112226871 * Math.pow(x, 2)
                    - 0.00930733913 * Math.pow(x, 3)
                    + 4.731582e-05 * Math.pow(x, 4)
                    - 9.054e-08 * Math.pow(x, 5);
        } else {
            denom = -216.0475144
                    + 16.2606339 * x
                    - 0.002388645 * Math.pow(x, 2)
                    - 0.00113732 * Math.pow(x, 3)
                    + 7.01863e-06 * Math.pow(x, 4)
                    - 1.291e-08 * Math.pow(x, 5);
        }

        if (denom <= 0) {
            return 0.0;
        }
        return round(totalKg * (500.0 / denom), 2);
    }

    private String classifyStrength(double dots) {
        if (dots < 250) {
            return "Novice";
        } else if (dots < 325) {
            return "Intermediate";
        } else if (dots < 400) {
            return "Proficient";
        } else if (dots < 475) {
            return "Advanced";
        } else if (dots < 550) {
            return "Elite";
        } else {
            return "World-Class";
        }
    }

    private double round(double value, int places) {
        return BigDecimal.valueOf(value)
                .setScale(places, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
