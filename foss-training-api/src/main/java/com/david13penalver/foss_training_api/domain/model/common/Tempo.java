package com.david13penalver.foss_training_api.domain.model.common;

import java.util.Objects;

public final class Tempo {

    private final int eccentricSeconds;
    private final int pauseAfterEccentricSeconds;
    private final int concentricSeconds;
    private final int pauseAfterConcentricSeconds;

    public Tempo(int eccentric, int pauseEccentric, int concentric, int pauseConcentric) {
        if (eccentric < 0 || pauseEccentric < 0 || concentric < 0 || pauseConcentric < 0) {
            throw new IllegalArgumentException("Tempo phase values cannot be negative");
        }
        this.eccentricSeconds = eccentric;
        this.pauseAfterEccentricSeconds = pauseEccentric;
        this.concentricSeconds = concentric;
        this.pauseAfterConcentricSeconds = pauseConcentric;
    }

    public static Tempo parse(String tempoString) {
        Objects.requireNonNull(tempoString, "Tempo string must not be null");
        String[] parts = tempoString.trim().split("-");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid tempo format (expected e.g. 3-1-1-0): " + tempoString);
        }
        try {
            int e = parsePhase(parts[0]);
            int pe = parsePhase(parts[1]);
            int c = parsePhase(parts[2]);
            int pc = parsePhase(parts[3]);
            return new Tempo(e, pe, c, pc);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid tempo values in: " + tempoString, ex);
        }
    }

    private static int parsePhase(String part) {
        String trimmed = part.trim().toUpperCase();
        if (trimmed.equals("X")) {
            return 0; // Explosive
        }
        return Integer.parseInt(trimmed);
    }

    public int getEccentricSeconds() {
        return eccentricSeconds;
    }

    public int getPauseAfterEccentricSeconds() {
        return pauseAfterEccentricSeconds;
    }

    public int getConcentricSeconds() {
        return concentricSeconds;
    }

    public int getPauseAfterConcentricSeconds() {
        return pauseAfterConcentricSeconds;
    }

    public int getTotalRepDuration() {
        return eccentricSeconds + pauseAfterEccentricSeconds + concentricSeconds + pauseAfterConcentricSeconds;
    }

    public String toFormattedString() {
        return String.format("%d-%d-%d-%d", eccentricSeconds, pauseAfterEccentricSeconds, concentricSeconds, pauseAfterConcentricSeconds);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tempo tempo = (Tempo) o;
        return eccentricSeconds == tempo.eccentricSeconds &&
                pauseAfterEccentricSeconds == tempo.pauseAfterEccentricSeconds &&
                concentricSeconds == tempo.concentricSeconds &&
                pauseAfterConcentricSeconds == tempo.pauseAfterConcentricSeconds;
    }

    @Override
    public int hashCode() {
        return Objects.hash(eccentricSeconds, pauseAfterEccentricSeconds, concentricSeconds, pauseAfterConcentricSeconds);
    }

    @Override
    public String toString() {
        return toFormattedString();
    }
}
