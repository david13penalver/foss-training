package com.david13penalver.foss_training_api.domain.model.exercise;

public record ExerciseSearchCriteria(
        String query,
        ExerciseCategory category,
        String muscleGroup,
        Equipment equipment,
        DifficultyLevel difficultyLevel
) {

    public static ExerciseSearchCriteria empty() {
        return new ExerciseSearchCriteria(null, null, null, null, null);
    }
}
