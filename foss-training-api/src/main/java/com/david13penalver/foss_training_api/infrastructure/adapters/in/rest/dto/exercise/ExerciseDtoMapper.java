package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.exercise;

import java.util.List;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;

@Component
public class ExerciseDtoMapper {

    public Exercise toEntity(ExerciseRequestDto dto) {
        if (dto == null) {
            return null;
        }
        Exercise exercise = new Exercise();
        exercise.setId(dto.getId());
        exercise.setName(dto.getName());
        exercise.setDescription(dto.getDescription());
        exercise.setImages(dto.getImages());
        exercise.setVideo(dto.getVideo());
        exercise.setPrimaryCategory(dto.getPrimaryCategory());
        exercise.setSecondaryCategories(dto.getSecondaryCategories());
        exercise.setResistanceMetrics(dto.getResistanceMetrics());
        exercise.setEnduranceMetrics(dto.getEnduranceMetrics());
        exercise.setMobilityMetrics(dto.getMobilityMetrics());
        exercise.setEquipmentRequired(dto.getEquipmentRequired());
        exercise.setDifficultyLevel(dto.getDifficultyLevel());
        exercise.setStepByStepInstructions(dto.getStepByStepInstructions());
        exercise.setCommonMistakes(dto.getCommonMistakes());
        exercise.setSafetyTips(dto.getSafetyTips());
        exercise.setAlternativeExercises(dto.getAlternativeExercises());
        exercise.setTags(dto.getTags());
        exercise.setActive(true);
        return exercise;
    }

    public ExerciseResponseDto toResponseDto(Exercise exercise) {
        if (exercise == null) {
            return null;
        }
        ExerciseResponseDto dto = new ExerciseResponseDto();
        dto.setId(exercise.getId());
        dto.setName(exercise.getName());
        dto.setDescription(exercise.getDescription());
        dto.setImages(exercise.getImages());
        dto.setVideo(exercise.getVideo());
        dto.setPrimaryCategory(exercise.getPrimaryCategory());
        dto.setSecondaryCategories(exercise.getSecondaryCategories());
        dto.setResistanceMetrics(exercise.getResistanceMetrics());
        dto.setEnduranceMetrics(exercise.getEnduranceMetrics());
        dto.setMobilityMetrics(exercise.getMobilityMetrics());
        dto.setEquipmentRequired(exercise.getEquipmentRequired());
        dto.setDifficultyLevel(exercise.getDifficultyLevel());
        dto.setStepByStepInstructions(exercise.getStepByStepInstructions());
        dto.setCommonMistakes(exercise.getCommonMistakes());
        dto.setSafetyTips(exercise.getSafetyTips());
        dto.setAlternativeExercises(exercise.getAlternativeExercises());
        dto.setCreatedBy(exercise.getCreatedBy());
        dto.setCreatedAt(exercise.getCreatedAt());
        dto.setUpdatedAt(exercise.getUpdatedAt());
        dto.setActive(exercise.isActive());
        dto.setTags(exercise.getTags());
        return dto;
    }

    public List<ExerciseResponseDto> toResponseDtoList(List<Exercise> exercises) {
        if (exercises == null) {
            return List.of();
        }
        return exercises.stream().map(this::toResponseDto).toList();
    }
}
