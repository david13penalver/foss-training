package com.david13penalver.foss_training_api.domain.model.common;

public enum WeightUnit {
    KG("kg", 1.0),
    LBS("lbs", 0.45359237);

    private final String symbol;
    private final double toKgFactor;

    WeightUnit(String symbol, double toKgFactor) {
        this.symbol = symbol;
        this.toKgFactor = toKgFactor;
    }

    public String getSymbol() {
        return symbol;
    }

    public double toKgFactor() {
        return toKgFactor;
    }
}
