package com.david13penalver.foss_training_api.application.usecases.session;

import java.util.List;

import com.david13penalver.foss_training_api.domain.model.session.Session;

public interface FindAllSessionsUseCase {

    List<Session> execute();
}