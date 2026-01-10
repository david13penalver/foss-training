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
    private int blocks;
    private int repetitions;
    private int restBetweenBlocks; // In seconds
    private int restBetweenRepetitions; // In seconds

    // Metrics per each repetition
    private double trackDistance;
    private int trackDuration; // In seconds
    private int trackPace; // In seconds

    private int trackAverageHeartRate;
    private int trackMinHeartRate;
    private int trackMaxHeartRate;
    private int trackTargetHeartRate;

    private int trackAveragePace; // In seconds
    private int trackMinPace; // In seconds
    private int trackMaxPace; // In seconds
    private int trackTargetPace; // In seconds

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

}
