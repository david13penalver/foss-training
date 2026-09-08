package com.david13penalver.foss_training_api.domain.ports.out.program;

import java.util.List;
import java.util.Optional;

import com.david13penalver.foss_training_api.domain.model.program.TrainingProgram;

public interface TrainingProgramRepository {

    List<TrainingProgram> findAll();

    Optional<TrainingProgram> findById(Integer id);

    TrainingProgram save(TrainingProgram program);

    void deleteById(Integer id);

    boolean existsById(Integer id);
}
