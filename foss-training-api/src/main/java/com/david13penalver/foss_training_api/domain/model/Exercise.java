package com.david13penalver.foss_training_api.domain.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Exercise {

    // Common fields
    private int id;
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
}
