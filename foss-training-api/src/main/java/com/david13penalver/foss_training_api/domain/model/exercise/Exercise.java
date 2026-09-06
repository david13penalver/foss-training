package com.david13penalver.foss_training_api.domain.model.exercise;

import java.time.LocalDateTime;
import java.util.List;

import com.david13penalver.foss_training_api.domain.model.exercise.endurance.EnduranceMetrics;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MobilityMetrics;
import com.david13penalver.foss_training_api.domain.model.exercise.resistance.ResistanceMetrics;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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

    public Exercise(Integer id, String name, String description, List<String> images, String video,
            ExerciseCategory primaryCategory, List<ExerciseCategory> secondaryCategories,
            ResistanceMetrics resistanceMetrics, EnduranceMetrics enduranceMetrics, MobilityMetrics mobilityMetrics,
            List<Equipment> equipmentRequired, DifficultyLevel difficultyLevel,
            List<String> stepByStepInstructions, List<String> commonMistakes, List<String> safetyTips,
            List<String> alternativeExercises, String createdBy, LocalDateTime createdAt, LocalDateTime updatedAt,
            boolean isActive, List<String> tags) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Exercise name cannot be blank");
        }
        if (primaryCategory == null) {
            throw new IllegalArgumentException("Exercise primaryCategory cannot be null");
        }
        if (primaryCategory == ExerciseCategory.RESISTANCE && resistanceMetrics == null) {
            throw new IllegalArgumentException("Exercise with RESISTANCE category requires resistanceMetrics");
        }
        if (primaryCategory == ExerciseCategory.ENDURANCE && enduranceMetrics == null) {
            throw new IllegalArgumentException("Exercise with ENDURANCE category requires enduranceMetrics");
        }
        if (primaryCategory == ExerciseCategory.MOBILITY && mobilityMetrics == null) {
            throw new IllegalArgumentException("Exercise with MOBILITY category requires mobilityMetrics");
        }
        this.id = id;
        this.name = name;
        this.description = description;
        this.images = images;
        this.video = video;
        this.primaryCategory = primaryCategory;
        this.secondaryCategories = secondaryCategories;
        this.resistanceMetrics = resistanceMetrics;
        this.enduranceMetrics = enduranceMetrics;
        this.mobilityMetrics = mobilityMetrics;
        this.equipmentRequired = equipmentRequired;
        this.difficultyLevel = difficultyLevel;
        this.stepByStepInstructions = stepByStepInstructions;
        this.commonMistakes = commonMistakes;
        this.safetyTips = safetyTips;
        this.alternativeExercises = alternativeExercises;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isActive = isActive;
        this.tags = tags;
    }

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
