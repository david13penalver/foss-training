package com.david13penalver.foss_training_api.domain.model.analytics;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.david13penalver.foss_training_api.domain.model.common.Weight;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OneRepMaxEstimate {

    private Weight weight;
    private int reps;
    private OneRepMaxFormula formula;
    private double estimated1Rm;
    private Map<Integer, Double> percentages = new LinkedHashMap<>();

    public static OneRepMaxEstimate calculate(Weight weight, int reps, OneRepMaxFormula formula) {
        if (weight == null) {
            throw new IllegalArgumentException("Weight must not be null");
        }
        if (formula == null) {
            throw new IllegalArgumentException("Formula must not be null");
        }
        double est = formula.calculate(weight.getValue(), reps);

        Map<Integer, Double> pcts = new LinkedHashMap<>();
        int[] standardPercentages = {50, 55, 60, 65, 70, 75, 80, 85, 90, 95};
        for (int p : standardPercentages) {
            double val = Math.round(est * (p / 100.0) * 100.0) / 100.0;
            pcts.put(p, val);
        }

        return new OneRepMaxEstimate(weight, reps, formula, est, pcts);
    }

    public Map<Integer, Double> getPercentages() {
        return percentages != null ? Collections.unmodifiableMap(percentages) : Collections.emptyMap();
    }
}
