package com.david13penalver.foss_training_api.application.usecases.session.impl;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.session.DeleteSessionUseCase;
import com.david13penalver.foss_training_api.domain.ports.out.session.SessionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteSessionService implements DeleteSessionUseCase {

    private final SessionRepository sessionRepository;

    @Override
    public void execute(Integer id) {
        log.debug("Executing DeleteSessionUseCase with id: {}", id);
        if (!sessionRepository.existsById(id)) {
            throw new IllegalArgumentException("Session not found with id: " + id);
        }
        sessionRepository.deleteById(id);
    }
}