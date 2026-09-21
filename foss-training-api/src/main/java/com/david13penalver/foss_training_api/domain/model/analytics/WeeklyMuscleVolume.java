package com.david13penalver.foss_training_api.domain.model.analytics;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleCategory;
import com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleGroup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeeklyMuscleVolume {

    private LocalDate startDate;
    private LocalDate endDate;
    private int totalWorkingSets;
    private double totalVolumeKg;
    @Builder.Default
    private List<MuscleGroupVolume> muscleVolumes = new ArrayList<>();
    @Builder.Default
    private Map<MuscleCategory, Double> categoryVolumes = new LinkedHashMap<>();
    private double pushPullRatio;
    private double upperLowerRatio;
    @Builder.Default
    private List<MuscleGroup> neglectedMuscleGroups = new ArrayList<>();
    @Builder.Default
    private List<MuscleGroup> optimalMuscleGroups = new ArrayList<>();
    @Builder.Default
    private List<MuscleGroup> overtrainedMuscleGroups = new ArrayList<>();
    @Builder.Default
    private List<String> recommendations = new ArrayList<>();

    public List<MuscleGroupVolume> getMuscleVolumes() {
        return muscleVolumes != null ? Collections.unmodifiableList(muscleVolumes) : Collections.emptyList();
    }

    public Map<MuscleCategory, Double> getCategoryVolumes() {
        return categoryVolumes != null ? Collections.unmodifiableMap(categoryVolumes) : Collections.emptyMap();
    }

    public List<MuscleGroup> getNeglectedMuscleGroups() {
        return neglectedMuscleGroups != null ? Collections.unmodifiableList(neglectedMuscleGroups) : Collections.emptyList();
    }

    public List<MuscleGroup> getOptimalMuscleGroups() {
        return optimalMuscleGroups != null ? Collections.unmodifiableList(optimalMuscleGroups) : Collections.emptyList();
    }

    public List<MuscleGroup> getOvertrainedMuscleGroups() {
        return overtrainedMuscleGroups != null ? Collections.unmodifiableList(overtrainedMuscleGroups) : Collections.emptyList();
    }

    public List<String> getRecommendations() {
        return recommendations != null ? Collections.unmodifiableList(recommendations) : Collections.emptyList();
    }
}
