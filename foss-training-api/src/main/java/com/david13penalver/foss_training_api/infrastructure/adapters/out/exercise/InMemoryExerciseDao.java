package com.david13penalver.foss_training_api.infrastructure.adapters.out.exercise;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;

@Component
public class InMemoryExerciseDao {

    private final Map<Integer, Exercise> store = new LinkedHashMap<>();
    private final AtomicInteger sequence = new AtomicInteger(0);

    public synchronized List<Exercise> findAll() {
        return new ArrayList<>(store.values());
    }

    public synchronized Optional<Exercise> findById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(store.get(id));
    }

    public synchronized Exercise save(Exercise exercise) {
        if (exercise.getId() == null) {
            exercise.setId(sequence.incrementAndGet());
        }
        store.put(exercise.getId(), exercise);
        return exercise;
    }

    public synchronized void deleteById(Integer id) {
        if (id != null) {
            store.remove(id);
        }
    }

    public synchronized boolean existsById(Integer id) {
        return id != null && store.containsKey(id);
    }

    public synchronized void clear() {
        store.clear();
        sequence.set(0);
    }
}