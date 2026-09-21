package com.david13penalver.foss_training_api.application.usecases.program.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.program.GetProgramAdherenceUseCase;
import com.david13penalver.foss_training_api.domain.model.program.ProgramAdherence;
import com.david13penalver.foss_training_api.domain.model.program.ProgramAdherenceCalculator;
import com.david13penalver.foss_training_api.domain.model.program.TrainingProgram;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.ports.out.program.TrainingProgramRepository;
import com.david13penalver.foss_training_api.domain.ports.out.training.TrainingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetProgramAdherenceService implements GetProgramAdherenceUseCase {

    private final TrainingProgramRepository trainingProgramRepository;
    private final TrainingRepository trainingRepository;

    @Override
    public ProgramAdherence execute(Integer programId) {
        log.debug("Executing GetProgramAdherenceUseCase with programId: {}", programId);
        if (programId == null) {
            throw new IllegalArgumentException("Program ID must not be null");
        }

        TrainingProgram program = trainingProgramRepository.findById(programId)
                .orElseThrow(() -> new IllegalArgumentException("Training program not found with id: " + programId));

        List<Training> trainings = trainingRepository.findByProgramId(programId);

        // Fallback for trainings generated prior to programId tracking
        if (trainings.isEmpty()) {
            String prefix = program.getName() + " - W";
            trainings = trainingRepository.findAll().stream()
                    .filter(t -> t.getName() != null && t.getName().startsWith(prefix))
                    .toList();
        }

        return ProgramAdherenceCalculator.calculate(program, trainings);
    }
}
