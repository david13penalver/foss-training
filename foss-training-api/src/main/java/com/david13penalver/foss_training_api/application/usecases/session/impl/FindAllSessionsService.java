package com.david13penalver.foss_training_api.application.usecases.session.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.session.FindAllSessionsUseCase;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.ports.out.session.SessionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FindAllSessionsService implements FindAllSessionsUseCase {

    private final SessionRepository sessionRepository;

    @Override
    public List<Session> execute() {
        log.debug("Executing FindAllSessionsUseCase");
        return sessionRepository.findAll();
    }
}