package com.david13penalver.foss_training_api.domain.model.session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class EnduranceSessionExercise extends SessionExercise {

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
    private Double restMaxCadence;
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

}
