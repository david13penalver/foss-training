package com.david13penalver.foss_training_api.infrastructure.adapters.out.training;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.domain.model.training.Training;

@Component
public class InMemoryTrainingDao {

    private final Map<Integer, Training> store = new LinkedHashMap<>();
    private final AtomicInteger sequence = new AtomicInteger(0);

    public synchronized List<Training> findAll() {
        return new ArrayList<>(store.values());
    }

    public synchronized Optional<Training> findById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(store.get(id));
    }

    public synchronized Training save(Training training) {
        if (training.getId() == null) {
            training.setId(sequence.incrementAndGet());
        } else {
            sequence.accumulateAndGet(training.getId(), Math::max);
        }
        store.put(training.getId(), training);
        return training;
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
