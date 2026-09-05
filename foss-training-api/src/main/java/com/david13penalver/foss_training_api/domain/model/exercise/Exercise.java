package com.david13penalver.foss_training_api.domain.model.exercise;

import java.time.LocalDateTime;
import java.util.List;

import com.david13penalver.foss_training_api.domain.model.exercise.endurance.EnduranceMetrics;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MobilityMetrics;
import com.david13penalver.foss_training_api.domain.model.exercise.resistance.ResistanceMetrics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Exercise {

    // Common fields
    private Integer id;
    private String name;
    private String description;
    private List<String> images;
    private String video;

    // Category classification
    private ExerciseCategory primaryCategory; // RESISTANCE, ENDURANCE, MOBILITY
    private List<ExerciseCategory> secondaryCategories;

    // Category-specific metrics (nullable based on category)
    private ResistanceMetrics resistanceMetrics;
    private EnduranceMetrics enduranceMetrics;
    private MobilityMetrics mobilityMetrics;

    // Equipment and difficulty
    private List<Equipment> equipmentRequired;
    private DifficultyLevel difficultyLevel; // BEGINNER, INTERMEDIATE, ADVANCED

    // Instructions and safety
    private List<String> stepByStepInstructions;
    private List<String> commonMistakes;
    private List<String> safetyTips;
    private List<String> alternativeExercises; // Alternative exercise IDs

    // Metadata and organization
    private String createdBy; // User ID if custom exercise
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isActive; // Soft delete flag
    private List<String> tags; // For search and filtering

    public boolean isResistance() {
        return primaryCategory == ExerciseCategory.RESISTANCE;
    }

    public boolean isEndurance() {
        return primaryCategory == ExerciseCategory.ENDURANCE;
    }

    public boolean isMobility() {
        return primaryCategory == ExerciseCategory.MOBILITY;
    }

    public boolean hasMatchingMetrics() {
        if (primaryCategory == null) {
            return false;
        }
        return switch (primaryCategory) {
            case RESISTANCE -> resistanceMetrics != null;
            case ENDURANCE -> enduranceMetrics != null;
            case MOBILITY -> mobilityMetrics != null;
            default -> false;
        };
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }
}
