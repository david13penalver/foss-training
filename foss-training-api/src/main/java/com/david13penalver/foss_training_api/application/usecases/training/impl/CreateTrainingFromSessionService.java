package com.david13penalver.foss_training_api.application.usecases.training.impl;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.training.CreateTrainingFromSessionUseCase;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.model.training.TrainingStatusEnum;
import com.david13penalver.foss_training_api.domain.ports.out.session.SessionRepository;
import com.david13penalver.foss_training_api.domain.ports.out.training.TrainingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateTrainingFromSessionService implements CreateTrainingFromSessionUseCase {

    private final SessionRepository sessionRepository;
    private final TrainingRepository trainingRepository;

    @Override
    public Training execute(Integer sessionId, LocalDate date, String customName) {
        log.debug("Executing CreateTrainingFromSessionUseCase with sessionId: {}, date: {}, customName: {}",
                sessionId, date, customName);
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found with id: " + sessionId));

        Training training = new Training();
        String trainingName = (customName != null && !customName.isBlank()) ? customName.trim() : session.getName();
        training.setName(trainingName);
        training.setDescription(session.getDescription());
        training.setSession(session);
        training.setTrainingDate(date != null ? date : LocalDate.now());
        training.setStatus(TrainingStatusEnum.PLANNED);

        return trainingRepository.save(training);
    }
}
