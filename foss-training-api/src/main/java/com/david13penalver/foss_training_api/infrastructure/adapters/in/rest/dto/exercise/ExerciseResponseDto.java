package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.exercise;

import java.time.LocalDateTime;
import java.util.List;

import com.david13penalver.foss_training_api.domain.model.exercise.DifficultyLevel;
import com.david13penalver.foss_training_api.domain.model.exercise.Equipment;
import com.david13penalver.foss_training_api.domain.model.exercise.ExerciseCategory;
import com.david13penalver.foss_training_api.domain.model.exercise.endurance.EnduranceMetrics;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MobilityMetrics;
import com.david13penalver.foss_training_api.domain.model.exercise.resistance.ResistanceMetrics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "Exercise")
public class ExerciseResponseDto {

    private Integer id;
    private String name;
    private String description;
    private List<String> images;
    private String video;

    private ExerciseCategory primaryCategory;
    private List<ExerciseCategory> secondaryCategories;

    private ResistanceMetrics resistanceMetrics;
    private EnduranceMetrics enduranceMetrics;
    private MobilityMetrics mobilityMetrics;

    private List<Equipment> equipmentRequired;
    private DifficultyLevel difficultyLevel;

    private List<String> stepByStepInstructions;
    private List<String> commonMistakes;
    private List<String> safetyTips;
    private List<String> alternativeExercises;

    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isActive;
    private List<String> tags;

}
