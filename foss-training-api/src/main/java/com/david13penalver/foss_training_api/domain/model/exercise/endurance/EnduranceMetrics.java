package com.david13penalver.foss_training_api.domain.model.exercise.endurance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnduranceMetrics {

// Common fields
    private Integer id;

    // Type of endurance activity
    private EnduranceType enduranceType; // CARDIO, AEROBIC, ANAEROBIC, HIIT Enum

    // Metrics tracking
    private Integer blocks;
    private Integer repetitions;
    private Integer restBetweenBlocks; // In seconds
    private Integer restBetweenRepetitions; // In seconds

    // Metrics per each repetition
    private Double trackDistance;
    private Integer trackDuration; // In seconds
    private Integer trackPace; // In seconds

    private Integer trackAverageHeartRate;
    private Integer trackMinHeartRate;
    private Integer trackMaxHeartRate;
    private Integer trackTargetHeartRate;

    private Integer trackAveragePace; // In seconds
    private Integer trackMinPace; // In seconds
    private Integer trackMaxPace; // In seconds
    private Integer trackTargetPace; // In seconds

    private Double trackAveragePower;
    private Double trackMinPower;
    private Double trackMaxPower;
    private Double trackTargetPower;

    private Double trackAverageCadence;
    private Double trackMinCadence;
    private Double trackMaxCadence;
    private Double trackTargetCadence;

    private Double trackAverageSpeed;
    private Double trackMinSpeed;
    private Double trackMaxSpeed;
    private Double trackTargetSpeed;

    // Metrics per each rest per repetition
    private Double restTrackDistance;
    private Integer restTrackDuration; // In seconds
    private Integer restTrackPace; // In seconds

    private Integer restTrackAverageHeartRate;
    private Integer restTrackMinHeartRate;
    private Integer restTrackMaxHeartRate;
    private Integer restTrackTargetHeartRate;

    private Double restTrackAveragePower;
    private Double restTrackMinPower;
    private Double restTrackMaxPower;
    private Double restTrackTargetPower;

    private Double restTrackAverageCadence;
    private Double restTrackMinCadence;
    private Double restTrackMaxCadence;
    private Double restTrackTargetCadence;

    private Double restTrackAverageSpeed;
    private Double restTrackMinSpeed;
    private Double restTrackMaxSpeed;
    private Double restTrackTargetSpeed;

    // Units
    private String distanceUnit; // "miles", "meters"
    private String paceUnit; // "min/km", "min/mile" (to convert from trackPace in seconds)
    private String heartRateUnit; // "bpm"
    private String powerUnit; // "W"

    // Performance recommendations
    private Integer recommendedBlocks;
    private Integer recommendedRepetitions;
    private Integer recommendedRestBetweenBlocks;
    private Integer recommendedRestBetweenRepetitions;

    private Double recommendedTrackDistance;
    private Integer recommendedTrackDuration; // In seconds
    private Integer recommendedTrackPace; // In seconds

    private Integer recommendedTrackAverageHeartRate;
    private Integer recommendedTrackMinHeartRate;
    private Integer recommendedTrackMaxHeartRate;
    private Integer recommendedTrackTargetHeartRate;

    private Double recommendedTrackAveragePower;
    private Double recommendedTrackMinPower;
    private Double recommendedTrackMaxPower;
    private Double recommendedTrackTargetPower;

    private Double recommendedTrackAverageCadence;
    private Double recommendedTrackMinCadence;
    private Double recommendedTrackMaxCadence;
    private Double recommendedTrackTargetCadence;

    private Double recommendedTrackAverageSpeed;
    private Double recommendedTrackMinSpeed;
    private Double recommendedTrackMaxSpeed;
    private Double recommendedTrackTargetSpeed;

    private Double recommendedRestTrackDistance;
    private Integer recommendedRestTrackDuration; // In seconds
    private Integer recommendedRestTrackPace; // In seconds

    private Integer recommendedRestTrackAverageHeartRate;
    private Integer recommendedRestTrackMinHeartRate;
    private Integer recommendedRestTrackMaxHeartRate;
    private Integer recommendedRestTrackTargetHeartRate;

    private Double recommendedRestTrackAveragePower;
    private Double recommendedRestTrackMinPower;
    private Double recommendedRestTrackMaxPower;
    private Double recommendedRestTrackTargetPower;

    private Double recommendedRestTrackAverageCadence;
    private Double recommendedRestTrackMinCadence;
    private Double recommendedRestTrackMaxCadence;
    private Double recommendedRestTrackTargetCadence;

    private Double recommendedRestTrackAverageSpeed;
    private Double recommendedRestTrackMinSpeed;
    private Double recommendedRestTrackMaxSpeed;
    private Double recommendedRestTrackTargetSpeed;

}
