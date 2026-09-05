package com.david13penalver.foss_training_api.domain.model.common;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class Rpe implements Comparable<Rpe> {

    private final double value;

    @JsonCreator
    public Rpe(@JsonProperty("value") double value) {
        if (value < 1.0 || value > 10.0) {
            throw new IllegalArgumentException("RPE must be between 1.0 and 10.0: " + value);
        }
        this.value = value;
    }

    public static Rpe of(double value) {
        return new Rpe(value);
    }

    public double getValue() {
        return value;
    }

    public double getRir() {
        return Math.max(0.0, 10.0 - value);
    }

    public boolean isWarmup() {
        return value < 6.0;
    }

    public boolean isEffectiveSet() {
        return value >= 7.0;
    }

    public boolean isMaxEffort() {
        return value >= 9.5;
    }

    @Override
    public int compareTo(Rpe o) {
        return Double.compare(this.value, o.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rpe rpe = (Rpe) o;
        return Double.compare(rpe.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.format("@RPE %.1f (RIR %.1f)", value, getRir());
    }
}
