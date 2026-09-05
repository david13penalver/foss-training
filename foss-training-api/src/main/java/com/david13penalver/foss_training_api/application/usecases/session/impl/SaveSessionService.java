package com.david13penalver.foss_training_api.application.usecases.session.impl;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.session.SaveSessionUseCase;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.ports.out.session.SessionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SaveSessionService implements SaveSessionUseCase {

    private final SessionRepository sessionRepository;

    @Override
    public Session execute(Session session) {
        log.debug("Executing SaveSessionUseCase with session name: {}", session.getName());
        return sessionRepository.save(session);
    }
}