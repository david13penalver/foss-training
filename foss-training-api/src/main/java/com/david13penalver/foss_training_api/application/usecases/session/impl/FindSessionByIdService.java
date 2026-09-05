package com.david13penalver.foss_training_api.application.usecases.session.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.session.FindSessionByIdUseCase;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.ports.out.session.SessionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FindSessionByIdService implements FindSessionByIdUseCase {

    private final SessionRepository sessionRepository;

    @Override
    public Optional<Session> execute(Integer id) {
        log.debug("Executing FindSessionByIdUseCase with id: {}", id);
        return sessionRepository.findById(id);
    }
}