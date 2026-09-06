package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.exercise;

import java.util.List;

import com.david13penalver.foss_training_api.domain.model.exercise.DifficultyLevel;
import com.david13penalver.foss_training_api.domain.model.exercise.Equipment;
import com.david13penalver.foss_training_api.domain.model.exercise.ExerciseCategory;
import com.david13penalver.foss_training_api.domain.model.exercise.endurance.EnduranceMetrics;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MobilityMetrics;
import com.david13penalver.foss_training_api.domain.model.exercise.resistance.ResistanceMetrics;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ExerciseRequest")
public class ExerciseRequestDto {

    private Integer id;

    @NotBlank(message = "Exercise name cannot be blank")
    private String name;

    private String description;
    private List<String> images;
    private String video;

    @NotNull(message = "Exercise primaryCategory cannot be null")
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
    private List<String> tags;

}
