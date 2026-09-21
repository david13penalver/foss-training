package com.david13penalver.foss_training_api.application.usecases.program.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.program.GenerateProgramScheduleUseCase;
import com.david13penalver.foss_training_api.domain.model.program.ProgramWorkout;
import com.david13penalver.foss_training_api.domain.model.program.TrainingProgram;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.model.training.TrainingStatusEnum;
import com.david13penalver.foss_training_api.domain.ports.out.program.TrainingProgramRepository;
import com.david13penalver.foss_training_api.domain.ports.out.training.TrainingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenerateProgramScheduleService implements GenerateProgramScheduleUseCase {

    private final TrainingProgramRepository trainingProgramRepository;
    private final TrainingRepository trainingRepository;

    @Override
    public List<Training> execute(Integer programId, LocalDate startDate) {
        log.debug("Executing GenerateProgramScheduleUseCase with programId: {}, startDate: {}", programId, startDate);
        TrainingProgram program = trainingProgramRepository.findById(programId)
                .orElseThrow(() -> new IllegalArgumentException("Training program not found with id: " + programId));

        if (program.getWorkouts() == null || program.getWorkouts().isEmpty()) {
            throw new IllegalArgumentException("Program has no workout days configured");
        }

        LocalDate baseDate = (startDate != null) ? startDate : LocalDate.now();
        List<Training> createdTrainings = new ArrayList<>();

        for (int week = 0; week < program.getDurationWeeks(); week++) {
            for (ProgramWorkout workout : program.getWorkouts()) {
                int dayOffset = workout.getDayOfWeek() - 1;
                LocalDate scheduledDate = baseDate.plusWeeks(week).plusDays(dayOffset);

                Training training = new Training();
                String sessionName = (workout.getSession() != null && workout.getSession().getName() != null)
                        ? workout.getSession().getName()
                        : "Workout";
                training.setName(program.getName() + " - W" + (week + 1) + "D" + workout.getDayOfWeek() + ": " + sessionName);
                training.setDescription(workout.getFocus());
                training.setSession(workout.getSession());
                training.setTrainingDate(scheduledDate);
                training.setStatus(TrainingStatusEnum.PLANNED);
                training.setProgramId(program.getId());

                Training saved = trainingRepository.save(training);
                createdTrainings.add(saved);
            }
        }

        return createdTrainings;
    }
}
