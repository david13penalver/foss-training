package com.david13penalver.foss_training_api.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnduranceMetrics {

    // Common fields
    private int id;

    // Type of endurance activity
    private EnduranceType enduranceType; // CARDIO, AEROBIC, ANAEROBIC, HIIT

    // Metrics tracking
    private boolean trackDistance;
    private boolean trackDuration;
    private boolean trackPace;
    private boolean trackHeartRate;
    private boolean trackPower; // For cycling, rowing

    // Units
    private String distanceUnit; // "km", "miles", "meters"
    private String paceUnit; // "min/km", "min/mile"

    // Performance recommendations
    private Integer recommendedDurationMinutes;
    private Double recommendedDistanceKm;
    private String targetHeartRateZone; // "Zone 2", "Zone 3", etc.
    private Integer targetPaceMinPerKm;
    private Integer recommendedRestSeconds;

}
