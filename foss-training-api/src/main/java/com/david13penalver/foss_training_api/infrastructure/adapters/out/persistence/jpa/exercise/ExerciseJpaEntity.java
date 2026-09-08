package com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.exercise;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "exercises")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(length = 65535)
    private String description;

    @Column(name = "images_json", length = 65535)
    private String imagesJson;

    @Column(length = 500)
    private String video;

    @Column(name = "primary_category", nullable = false)
    private String primaryCategory;

    @Column(name = "secondary_categories_json", length = 65535)
    private String secondaryCategoriesJson;

    @Column(name = "resistance_metrics_json", length = 65535)
    private String resistanceMetricsJson;

    @Column(name = "endurance_metrics_json", length = 65535)
    private String enduranceMetricsJson;

    @Column(name = "mobility_metrics_json", length = 65535)
    private String mobilityMetricsJson;

    @Column(name = "equipment_required_json", length = 65535)
    private String equipmentRequiredJson;

    @Column(name = "difficulty_level")
    private String difficultyLevel;

    @Column(name = "instructions_json", length = 65535)
    private String instructionsJson;

    @Column(name = "common_mistakes_json", length = 65535)
    private String commonMistakesJson;

    @Column(name = "safety_tips_json", length = 65535)
    private String safetyTipsJson;

    @Column(name = "alternative_exercises_json", length = 65535)
    private String alternativeExercisesJson;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "tags_json", length = 65535)
    private String tagsJson;
}
