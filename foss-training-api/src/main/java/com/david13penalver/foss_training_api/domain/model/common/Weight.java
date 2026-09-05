package com.david13penalver.foss_training_api.domain.model.common;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class Weight implements Comparable<Weight> {

    private final double value;
    private final WeightUnit unit;

    @JsonCreator
    public Weight(@JsonProperty("value") double value, @JsonProperty("unit") WeightUnit unit) {
        if (value < 0) {
            throw new IllegalArgumentException("Weight value cannot be negative: " + value);
        }
        this.value = value;
        this.unit = Objects.requireNonNull(unit, "WeightUnit must not be null");
    }

    public static Weight kg(double value) {
        return new Weight(value, WeightUnit.KG);
    }

    public static Weight lbs(double value) {
        return new Weight(value, WeightUnit.LBS);
    }

    public static Weight zero(WeightUnit unit) {
        return new Weight(0.0, unit);
    }

    public double getValue() {
        return value;
    }

    public WeightUnit getUnit() {
        return unit;
    }

    public Weight toKg() {
        if (unit == WeightUnit.KG) {
            return this;
        }
        return new Weight(value * unit.toKgFactor(), WeightUnit.KG);
    }

    public Weight toLbs() {
        if (unit == WeightUnit.LBS) {
            return this;
        }
        return new Weight(value / WeightUnit.LBS.toKgFactor(), WeightUnit.LBS);
    }

    public Weight plus(Weight other) {
        Objects.requireNonNull(other, "Weight to add must not be null");
        if (this.unit == other.unit) {
            return new Weight(this.value + other.value, this.unit);
        }
        Weight converted = this.unit == WeightUnit.KG ? other.toKg() : other.toLbs();
        return new Weight(this.value + converted.value, this.unit);
    }

    public Weight times(double multiplier) {
        if (multiplier < 0) {
            throw new IllegalArgumentException("Multiplier cannot be negative: " + multiplier);
        }
        return new Weight(this.value * multiplier, this.unit);
    }

    @Override
    public int compareTo(Weight o) {
        return Double.compare(this.toKg().value, o.toKg().value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Weight weight = (Weight) o;
        return Double.compare(weight.toKg().value, this.toKg().value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(toKg().value);
    }

    @Override
    public String toString() {
        return String.format("%.2f %s", value, unit.getSymbol());
    }
}
