package com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.exercise;

import java.util.List;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.domain.model.exercise.DifficultyLevel;
import com.david13penalver.foss_training_api.domain.model.exercise.Equipment;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.exercise.ExerciseCategory;
import com.david13penalver.foss_training_api.domain.model.exercise.endurance.EnduranceMetrics;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MobilityMetrics;
import com.david13penalver.foss_training_api.domain.model.exercise.resistance.ResistanceMetrics;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Component
public class ExercisePersistenceMapper {

    private final ObjectMapper objectMapper;

    public ExercisePersistenceMapper() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public ExerciseJpaEntity toJpaEntity(Exercise domain) {
        if (domain == null) {
            return null;
        }
        return ExerciseJpaEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .imagesJson(toJson(domain.getImages()))
                .video(domain.getVideo())
                .primaryCategory(domain.getPrimaryCategory() != null ? domain.getPrimaryCategory().name() : null)
                .secondaryCategoriesJson(domain.getSecondaryCategories() != null
                        ? toJson(domain.getSecondaryCategories().stream().map(Enum::name).toList())
                        : null)
                .resistanceMetricsJson(toJson(domain.getResistanceMetrics()))
                .enduranceMetricsJson(toJson(domain.getEnduranceMetrics()))
                .mobilityMetricsJson(toJson(domain.getMobilityMetrics()))
                .equipmentRequiredJson(domain.getEquipmentRequired() != null
                        ? toJson(domain.getEquipmentRequired().stream().map(Enum::name).toList())
                        : null)
                .difficultyLevel(domain.getDifficultyLevel() != null ? domain.getDifficultyLevel().name() : null)
                .instructionsJson(toJson(domain.getStepByStepInstructions()))
                .commonMistakesJson(toJson(domain.getCommonMistakes()))
                .safetyTipsJson(toJson(domain.getSafetyTips()))
                .alternativeExercisesJson(toJson(domain.getAlternativeExercises()))
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .isActive(domain.isActive())
                .tagsJson(toJson(domain.getTags()))
                .build();
    }

    public Exercise toDomain(ExerciseJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        Exercise domain = new Exercise();
        domain.setId(entity.getId());
        domain.setName(entity.getName());
        domain.setDescription(entity.getDescription());
        domain.setImages(fromJson(entity.getImagesJson(), new TypeReference<List<String>>() {}));
        domain.setVideo(entity.getVideo());
        domain.setPrimaryCategory(entity.getPrimaryCategory() != null ? ExerciseCategory.valueOf(entity.getPrimaryCategory()) : null);

        List<String> secCats = fromJson(entity.getSecondaryCategoriesJson(), new TypeReference<List<String>>() {});
        if (secCats != null) {
            domain.setSecondaryCategories(secCats.stream().map(ExerciseCategory::valueOf).toList());
        }

        domain.setResistanceMetrics(fromJson(entity.getResistanceMetricsJson(), new TypeReference<ResistanceMetrics>() {}));
        domain.setEnduranceMetrics(fromJson(entity.getEnduranceMetricsJson(), new TypeReference<EnduranceMetrics>() {}));
        domain.setMobilityMetrics(fromJson(entity.getMobilityMetricsJson(), new TypeReference<MobilityMetrics>() {}));

        List<String> equip = fromJson(entity.getEquipmentRequiredJson(), new TypeReference<List<String>>() {});
        if (equip != null) {
            domain.setEquipmentRequired(equip.stream().map(Equipment::valueOf).toList());
        }

        domain.setDifficultyLevel(entity.getDifficultyLevel() != null ? DifficultyLevel.valueOf(entity.getDifficultyLevel()) : null);
        domain.setStepByStepInstructions(fromJson(entity.getInstructionsJson(), new TypeReference<List<String>>() {}));
        domain.setCommonMistakes(fromJson(entity.getCommonMistakesJson(), new TypeReference<List<String>>() {}));
        domain.setSafetyTips(fromJson(entity.getSafetyTipsJson(), new TypeReference<List<String>>() {}));
        domain.setAlternativeExercises(fromJson(entity.getAlternativeExercisesJson(), new TypeReference<List<String>>() {}));
        domain.setCreatedBy(entity.getCreatedBy());
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setUpdatedAt(entity.getUpdatedAt());
        domain.setActive(entity.isActive());
        domain.setTags(fromJson(entity.getTagsJson(), new TypeReference<List<String>>() {}));

        return domain;
    }

    private <T> String toJson(T value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing object to JSON", e);
        }
    }

    private <T> T fromJson(String json, TypeReference<T> typeRef) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, typeRef);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing JSON", e);
        }
    }
}
