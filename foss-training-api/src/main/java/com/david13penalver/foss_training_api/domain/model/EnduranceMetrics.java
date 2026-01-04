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
    private EnduranceType enduranceType; // CARDIO, AEROBIC, ANAEROBIC, HIIT Enum

    // Metrics tracking
    private int blocks;
    private int repetitions;
    private int restBetweenBlocks;
    private int restBetweenRepetitions;

    // Metrics per each repetition
    private double trackDistance;
    private int trackDuration; // In seconds
    private int trackPace; // In seconds

    private int trackAverageHeartRate;
    private int trackMinHeartRate;
    private int trackMaxHeartRate;
    private int trackTargetHeartRate;

    private double trackAveragePower;
    private double trackMinPower;
    private double trackMaxPower;
    private double trackTargetPower;

    private double trackAverageCadence;
    private double trackMinCadence;
    private double trackMaxCadence;
    private double trackTargetCadence;

    private double trackAverageSpeed;
    private double trackMinSpeed;
    private double trackMaxSpeed;
    private double trackTargetSpeed;

    // Metrics per each rest per repetition
    private double restTrackDistance;
    private int restTrackDuration; // In seconds
    private int restTrackPace; // In seconds

    private int restTrackAverageHeartRate;
    private int restTrackMinHeartRate;
    private int restTrackMaxHeartRate;
    private int restTrackTargetHeartRate;

    private double restTrackAveragePower;
    private double restTrackMinPower;
    private double restTrackMaxPower;
    private double restTrackTargetPower;

    private double restTrackAverageCadence;
    private double restTrackMinCadence;
    private double restTrackMaxCadence;
    private double restTrackTargetCadence;

    private double restTrackAverageSpeed;
    private double restTrackMinSpeed;
    private double restTrackMaxSpeed;
    private double restTrackTargetSpeed;

    // Units
    private String distanceUnit; // "miles", "meters"
    private String paceUnit; // "min/km", "min/mile" (to convert from trackPace in seconds)
    private String heartRateUnit; // "bpm"
    private String powerUnit; // "W"

    // Performance recommendations
    private int recommendedDuration; // seconds
    private double recommendedDistance;
    private double recommendedRest; // seconds
    private String recommendedTargetHeartRateZone; // "Zone 2", "Zone 3", etc.

    private int recommendedBlocks;
    private int recommendedRepetitions;
    private int recommendedRestBetweenBlocks;
    private int recommendedRestBetweenRepetitions;

}
