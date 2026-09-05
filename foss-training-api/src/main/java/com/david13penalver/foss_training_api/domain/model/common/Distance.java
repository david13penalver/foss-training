package com.david13penalver.foss_training_api.domain.model.common;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class Distance implements Comparable<Distance> {

    private final double value;
    private final DistanceUnit unit;

    @JsonCreator
    public Distance(@JsonProperty("value") double value, @JsonProperty("unit") DistanceUnit unit) {
        if (value < 0) {
            throw new IllegalArgumentException("Distance value cannot be negative: " + value);
        }
        this.value = value;
        this.unit = Objects.requireNonNull(unit, "DistanceUnit must not be null");
    }

    public static Distance meters(double value) {
        return new Distance(value, DistanceUnit.METERS);
    }

    public static Distance kilometers(double value) {
        return new Distance(value, DistanceUnit.KILOMETERS);
    }

    public static Distance miles(double value) {
        return new Distance(value, DistanceUnit.MILES);
    }

    public static Distance zero(DistanceUnit unit) {
        return new Distance(0.0, unit);
    }

    public double getValue() {
        return value;
    }

    public DistanceUnit getUnit() {
        return unit;
    }

    public Distance toMeters() {
        if (unit == DistanceUnit.METERS) {
            return this;
        }
        return new Distance(value * unit.toMetersFactor(), DistanceUnit.METERS);
    }

    public Distance toKilometers() {
        if (unit == DistanceUnit.KILOMETERS) {
            return this;
        }
        return new Distance(toMeters().value / 1000.0, DistanceUnit.KILOMETERS);
    }

    public Distance toMiles() {
        if (unit == DistanceUnit.MILES) {
            return this;
        }
        return new Distance(toMeters().value / 1609.344, DistanceUnit.MILES);
    }

    public Distance plus(Distance other) {
        Objects.requireNonNull(other, "Distance to add must not be null");
        if (this.unit == other.unit) {
            return new Distance(this.value + other.value, this.unit);
        }
        Distance converted = switch (this.unit) {
            case METERS -> other.toMeters();
            case KILOMETERS -> other.toKilometers();
            case MILES -> other.toMiles();
        };
        return new Distance(this.value + converted.value, this.unit);
    }

    @Override
    public int compareTo(Distance o) {
        return Double.compare(this.toMeters().value, o.toMeters().value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Distance distance = (Distance) o;
        return Double.compare(distance.toMeters().value, this.toMeters().value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(toMeters().value);
    }

    @Override
    public String toString() {
        return String.format("%.2f %s", value, unit.getSymbol());
    }
}
