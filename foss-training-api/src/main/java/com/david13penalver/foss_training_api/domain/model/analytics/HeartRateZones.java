package com.david13penalver.foss_training_api.domain.model.analytics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HeartRateZones {

    private int maxHr;
    private Integer restingHr;
    private Integer age;
    private HeartRateZoneMethod method;
    private Integer heartRateReserve;
    @Builder.Default
    private List<CalculatedHeartRateZone> zones = new ArrayList<>();

    public List<CalculatedHeartRateZone> getZones() {
        return zones != null ? Collections.unmodifiableList(zones) : Collections.emptyList();
    }

    public CalculatedHeartRateZone findZoneForBpm(int bpm) {
        if (zones == null || zones.isEmpty()) {
            return null;
        }
        for (CalculatedHeartRateZone z : zones) {
            if (bpm >= z.getMinBpm() && bpm <= z.getMaxBpm()) {
                return z;
            }
        }
        if (bpm < zones.get(0).getMinBpm()) {
            return zones.get(0);
        }
        return zones.get(zones.size() - 1);
    }
}
