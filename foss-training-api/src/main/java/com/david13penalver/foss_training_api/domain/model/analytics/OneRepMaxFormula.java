package com.david13penalver.foss_training_api.domain.model.analytics;

public enum OneRepMaxFormula {
    EPLEY {
        @Override
        protected double calculateRaw(double weight, int reps) {
            return weight * (1.0 + reps / 30.0);
        }
    },
    BRZYCKI {
        @Override
        protected double calculateRaw(double weight, int reps) {
            if (reps >= 37) {
                throw new IllegalArgumentException("Repetitions must be less than 37 for Brzycki formula");
            }
            return weight * (36.0 / (37.0 - reps));
        }
    },
    LOMBARDI {
        @Override
        protected double calculateRaw(double weight, int reps) {
            return weight * Math.pow(reps, 0.10);
        }
    },
    MAYHEW {
        @Override
        protected double calculateRaw(double weight, int reps) {
            return (100.0 * weight) / (52.2 + 41.9 * Math.exp(-0.055 * reps));
        }
    },
    OCONNER {
        @Override
        protected double calculateRaw(double weight, int reps) {
            return weight * (1.0 + 0.025 * reps);
        }
    },
    WATHEN {
        @Override
        protected double calculateRaw(double weight, int reps) {
            return (100.0 * weight) / (48.8 + 53.8 * Math.exp(-0.075 * reps));
        }
    };

    protected abstract double calculateRaw(double weight, int reps);

    public double calculate(double weight, int reps) {
        if (weight <= 0) {
            throw new IllegalArgumentException("Weight must be greater than zero");
        }
        if (reps < 1) {
            throw new IllegalArgumentException("Repetitions must be at least 1");
        }
        if (reps == 1) {
            return Math.round(weight * 100.0) / 100.0;
        }
        double raw = calculateRaw(weight, reps);
        return Math.round(raw * 100.0) / 100.0;
    }

    public static OneRepMaxFormula fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Formula value cannot be null or blank");
        }
        for (OneRepMaxFormula formula : values()) {
            if (formula.name().equalsIgnoreCase(value.trim())) {
                return formula;
            }
        }
        throw new IllegalArgumentException("Unknown 1RM formula: " + value);
    }
}
