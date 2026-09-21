package com.david13penalver.foss_training_api.application.usecases.program.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.program.CloneTrainingProgramUseCase;
import com.david13penalver.foss_training_api.domain.model.program.ProgramWorkout;
import com.david13penalver.foss_training_api.domain.model.program.TrainingProgram;
import com.david13penalver.foss_training_api.domain.ports.out.program.TrainingProgramRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloneTrainingProgramService implements CloneTrainingProgramUseCase {

    private final TrainingProgramRepository trainingProgramRepository;

    @Override
    public TrainingProgram execute(Integer programId, String newName) {
        log.debug("Executing CloneTrainingProgramUseCase with programId: {}, newName: {}", programId, newName);
        if (programId == null) {
            throw new IllegalArgumentException("Program ID must not be null");
        }

        TrainingProgram original = trainingProgramRepository.findById(programId)
                .orElseThrow(() -> new IllegalArgumentException("Training program not found with id: " + programId));

        String resolvedName = (newName != null && !newName.isBlank())
                ? newName.trim()
                : original.getName() + " (Copy)";

        TrainingProgram clone = new TrainingProgram();
        clone.setName(resolvedName);
        clone.setDescription(original.getDescription());
        clone.setDurationWeeks(original.getDurationWeeks());
        clone.setPeriodizationType(original.getPeriodizationType());
        clone.setLevel(original.getLevel());
        clone.setIsActive(true);

        if (original.getWorkouts() != null) {
            List<ProgramWorkout> clonedWorkouts = new ArrayList<>();
            for (ProgramWorkout pw : original.getWorkouts()) {
                clonedWorkouts.add(ProgramWorkout.builder()
                        .dayOfWeek(pw.getDayOfWeek())
                        .focus(pw.getFocus())
                        .session(pw.getSession())
                        .build());
            }
            clone.setWorkouts(clonedWorkouts);
        }

        return trainingProgramRepository.save(clone);
    }
}
