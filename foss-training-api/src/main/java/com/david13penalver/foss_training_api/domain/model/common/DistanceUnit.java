package com.david13penalver.foss_training_api.domain.model.common;

public enum DistanceUnit {
    METERS("m", 1.0),
    KILOMETERS("km", 1000.0),
    MILES("mi", 1609.344);

    private final String symbol;
    private final double toMetersFactor;

    DistanceUnit(String symbol, double toMetersFactor) {
        this.symbol = symbol;
        this.toMetersFactor = toMetersFactor;
    }

    public String getSymbol() {
        return symbol;
    }

    public double toMetersFactor() {
        return toMetersFactor;
    }
}
