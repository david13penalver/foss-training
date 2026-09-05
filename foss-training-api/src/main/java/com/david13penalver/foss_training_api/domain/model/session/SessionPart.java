package com.david13penalver.foss_training_api.domain.model.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionPart {

    private Integer id;
    private Session session;
    private SessionPartEnum sessionPartEnum;
    private List<SessionExercise> exercises = new ArrayList<>();

    public void addExercise(SessionExercise exercise) {
        if (exercise.getOrderIndex() == null) {
            exercise.setOrderIndex(exercises.size() + 1);
        }
        exercises.add(exercise);
    }

    public boolean removeExercise(Integer exerciseId) {
        return exercises.removeIf(e -> e.getId() != null && e.getId().equals(exerciseId));
    }

    public void reorderExercises(List<Integer> orderedIds) {
        orderedIds.stream()
                .sorted(Comparator.comparingInt(orderedIds::indexOf))
                .forEach(id -> exercises.stream()
                        .filter(e -> e.getId() != null && e.getId().equals(id))
                        .findFirst()
                        .ifPresent(e -> e.setOrderIndex(orderedIds.indexOf(id) + 1)));
        exercises.sort(Comparator.comparingInt(e -> e.getOrderIndex() != null ? e.getOrderIndex() : Integer.MAX_VALUE));
    }

    public List<SessionExercise> getExercises() {
        return Collections.unmodifiableList(exercises);
    }
}
