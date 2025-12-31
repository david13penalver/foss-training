package com.david13penalver.foss_training_api.domain.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MobilityMetrics {

    // Common fields
    private int id;

    // Type of mobility work
    private MobilityType mobilityType; // STRETCHING, DYNAMIC_MOBILITY, FOAM_ROLLING, YOGA
    private StretchType stretchType; // STATIC, DYNAMIC, PNF, BALLISTIC

    // Range of Motion tracking
    private List<Joint> targetJoints; // Hip, shoulder, ankle, etc.
    private String romMeasurementMethod; // "degrees", "visual scale", "distance"

    // Performance recommendations
    private Integer recommendedHoldTimeSeconds; // For static stretches
    private Integer recommendedRepetitions;
    private Integer recommendedSets;
    private boolean performBilaterally; // Both sides or unilateral

    // Timing recommendations
    private RecommendedTiming timing; // PRE_WORKOUT, POST_WORKOUT, STANDALONE
}
