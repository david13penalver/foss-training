package com.david13penalver.foss_training_api.application.usecases.session;

import java.util.Optional;

import com.david13penalver.foss_training_api.domain.model.session.Session;

public interface FindSessionByIdUseCase {

    Optional<Session> execute(Integer id);
}