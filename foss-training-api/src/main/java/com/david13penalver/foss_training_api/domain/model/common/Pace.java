package com.david13penalver.foss_training_api.domain.model.common;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class Pace {

    private final int secondsPerUnit;
    private final DistanceUnit unit;

    @JsonCreator
    public Pace(@JsonProperty("secondsPerUnit") int secondsPerUnit,
            @JsonProperty("unit") DistanceUnit unit) {
        if (secondsPerUnit < 0) {
            throw new IllegalArgumentException("Pace seconds per unit cannot be negative");
        }
        this.secondsPerUnit = secondsPerUnit;
        this.unit = Objects.requireNonNull(unit, "DistanceUnit must not be null");
    }

    public static Pace from(Distance distance, Duration duration) {
        Objects.requireNonNull(distance, "Distance must not be null");
        Objects.requireNonNull(duration, "Duration must not be null");
        if (distance.getValue() == 0) {
            return new Pace(0, distance.getUnit());
        }
        int secondsPerUnit = (int) Math.round(duration.getTotalSeconds() / distance.getValue());
        return new Pace(secondsPerUnit, distance.getUnit());
    }

    public int getSecondsPerUnit() {
        return secondsPerUnit;
    }

    public DistanceUnit getUnit() {
        return unit;
    }

    public String toFormattedString() {
        int minutes = secondsPerUnit / 60;
        int seconds = secondsPerUnit % 60;
        return String.format("%02d:%02d min/%s", minutes, seconds, unit.getSymbol());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pace pace = (Pace) o;
        return secondsPerUnit == pace.secondsPerUnit && unit == pace.unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(secondsPerUnit, unit);
    }

    @Override
    public String toString() {
        return toFormattedString();
    }
}
