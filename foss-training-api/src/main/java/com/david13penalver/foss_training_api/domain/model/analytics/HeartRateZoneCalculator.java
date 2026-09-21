package com.david13penalver.foss_training_api.domain.model.analytics;

import java.util.ArrayList;
import java.util.List;

import com.david13penalver.foss_training_api.domain.model.common.HeartRateZone;

/**
 * Pure domain calculator for cardiovascular target heart rate zones (Karvonen HRR and %HRmax).
 */
public class HeartRateZoneCalculator {

    private static final String[] BENEFITS = {
            "Active recovery, warmup, cooldown, and foundational aerobic base building. Low physical stress.",
            "Maximizes lipid oxidation and stimulates mitochondrial biogenesis and capillary network growth. Core foundation for endurance performance.",
            "Elevates aerobic power, cardiac stroke volume, and muscular glycogen storage efficiency. Breathing becomes deeper and conversation more laboured.",
            "Increases lactate clearance capacity, anaerobic threshold, and sustains high-intensity speed-endurance. High mental concentration required.",
            "Develops maximum neuromuscular power, VO2 max capacity, and fast-twitch motor unit recruitment. High intensity intervals."
    };

    public static HeartRateZones compute(Integer maxHr, Integer restingHr, Integer age) {
        if (maxHr == null && age == null) {
            throw new IllegalArgumentException("Either maxHr or age must be provided to calculate heart rate zones");
        }

        int effectiveMaxHr;
        if (maxHr != null) {
            if (maxHr < 60 || maxHr > 240) {
                throw new IllegalArgumentException("maxHr must be between 60 and 240 bpm: " + maxHr);
            }
            effectiveMaxHr = maxHr;
        } else {
            if (age < 10 || age > 110) {
                throw new IllegalArgumentException("age must be between 10 and 110: " + age);
            }
            // Tanaka formula: HRmax = 208 - (0.7 * age)
            effectiveMaxHr = (int) Math.round(208.0 - (0.7 * age));
        }

        HeartRateZoneMethod method;
        Integer heartRateReserve = null;

        if (restingHr != null) {
            if (restingHr < 30 || restingHr >= effectiveMaxHr) {
                throw new IllegalArgumentException(
                        "restingHr must be at least 30 and less than maxHr (" + effectiveMaxHr + "): " + restingHr);
            }
            method = HeartRateZoneMethod.KARVONEN;
            heartRateReserve = effectiveMaxHr - restingHr;
        } else {
            method = HeartRateZoneMethod.PERCENT_MAX_HR;
        }

        List<CalculatedHeartRateZone> calculatedZones = new ArrayList<>();
        HeartRateZone[] enumZones = HeartRateZone.values();

        for (int i = 0; i < enumZones.length; i++) {
            HeartRateZone enumZone = enumZones[i];
            int minBpm;
            int maxBpm;

            if (method == HeartRateZoneMethod.KARVONEN) {
                // Karvonen: HRtarget = HRrest + (percentage * HRR)
                minBpm = (int) Math.round(restingHr + (enumZone.getMinPercentage() * heartRateReserve));
                maxBpm = (int) Math.round(restingHr + (enumZone.getMaxPercentage() * heartRateReserve));
            } else {
                // Percent Max HR: HRtarget = percentage * HRmax
                minBpm = (int) Math.round(enumZone.getMinPercentage() * effectiveMaxHr);
                maxBpm = (int) Math.round(enumZone.getMaxPercentage() * effectiveMaxHr);
            }

            calculatedZones.add(CalculatedHeartRateZone.builder()
                    .zone(enumZone)
                    .zoneNumber(i + 1)
                    .displayName(enumZone.getDisplayName())
                    .minPercentage(Math.round(enumZone.getMinPercentage() * 100.0 * 10.0) / 10.0)
                    .maxPercentage(Math.round(enumZone.getMaxPercentage() * 100.0 * 10.0) / 10.0)
                    .minBpm(minBpm)
                    .maxBpm(maxBpm)
                    .description(enumZone.getDescription())
                    .trainingBenefit(BENEFITS[i])
                    .build());
        }

        return HeartRateZones.builder()
                .maxHr(effectiveMaxHr)
                .restingHr(restingHr)
                .age(age)
                .method(method)
                .heartRateReserve(heartRateReserve)
                .zones(calculatedZones)
                .build();
    }
}
