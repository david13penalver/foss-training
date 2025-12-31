package com.david13penalver.foss_training_api.domain.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResistanceMetrics {

    // Common fields
    private int id;

    // Muscle groups and biomechanics
    private List<MuscleGroup> primaryMuscles;
    private List<MuscleGroup> secondaryMuscles;
    private MovementPattern movementPattern; // e.g., PUSH, PULL, SQUAT, HINGE

    // Exercise type characteristics
    private boolean isWeighted;
    private boolean isBodyweight;
    private boolean isTimed; // For isometric holds
    private String defaultWeightUnit; // "kg", "lbs"

    // Performance recommendations
    private Integer recommendedSets;
    private Integer recommendedRepsMin;
    private Integer recommendedRepsMax;
    private Integer recommendedRestSeconds;
    private String tempoRecommendation; // e.g., "3-0-1-0" (eccentric-pause-concentric-pause)

    // Load progression
    private Double minWeight;
    private Double maxWeight;
    private Double weightIncrement; // Suggested progression increment
}
