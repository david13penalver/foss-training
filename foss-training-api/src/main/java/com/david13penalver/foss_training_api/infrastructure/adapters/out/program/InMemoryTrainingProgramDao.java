package com.david13penalver.foss_training_api.infrastructure.adapters.out.program;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.domain.model.program.TrainingProgram;

@Component
public class InMemoryTrainingProgramDao {

    private final Map<Integer, TrainingProgram> programs = new LinkedHashMap<>();
    private final AtomicInteger sequence = new AtomicInteger(0);

    public synchronized List<TrainingProgram> findAll() {
        return new ArrayList<>(programs.values());
    }

    public synchronized Optional<TrainingProgram> findById(Integer id) {
        return Optional.ofNullable(programs.get(id));
    }

    public synchronized TrainingProgram save(TrainingProgram program) {
        if (program.getId() == null) {
            program.setId(sequence.incrementAndGet());
        } else {
            sequence.accumulateAndGet(program.getId(), Math::max);
        }
        programs.put(program.getId(), program);
        return program;
    }

    public synchronized void deleteById(Integer id) {
        programs.remove(id);
    }

    public synchronized boolean existsById(Integer id) {
        return programs.containsKey(id);
    }

    public synchronized void clear() {
        programs.clear();
        sequence.set(0);
    }
}
