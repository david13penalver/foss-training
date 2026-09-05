package com.david13penalver.foss_training_api.domain.model.common;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class Duration implements Comparable<Duration> {

    private final int totalSeconds;

    @JsonCreator
    public Duration(@JsonProperty("totalSeconds") int totalSeconds) {
        if (totalSeconds < 0) {
            throw new IllegalArgumentException("Duration cannot be negative: " + totalSeconds);
        }
        this.totalSeconds = totalSeconds;
    }

    public static Duration seconds(int seconds) {
        return new Duration(seconds);
    }

    public static Duration minutes(int minutes) {
        return new Duration(minutes * 60);
    }

    public static Duration hours(int hours) {
        return new Duration(hours * 3600);
    }

    public static Duration zero() {
        return new Duration(0);
    }

    public int getTotalSeconds() {
        return totalSeconds;
    }

    @JsonIgnore
    public int getMinutes() {
        return totalSeconds / 60;
    }

    @JsonIgnore
    public int getHours() {
        return totalSeconds / 3600;
    }

    public Duration plus(Duration other) {
        Objects.requireNonNull(other, "Duration to add must not be null");
        return new Duration(this.totalSeconds + other.totalSeconds);
    }

    public Duration minus(Duration other) {
        Objects.requireNonNull(other, "Duration to subtract must not be null");
        int result = this.totalSeconds - other.totalSeconds;
        if (result < 0) {
            throw new IllegalArgumentException("Duration cannot be negative after subtraction");
        }
        return new Duration(result);
    }

    public String toFormattedString() {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public int compareTo(Duration o) {
        return Integer.compare(this.totalSeconds, o.totalSeconds);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Duration duration = (Duration) o;
        return totalSeconds == duration.totalSeconds;
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalSeconds);
    }

    @Override
    public String toString() {
        return toFormattedString();
    }
}
