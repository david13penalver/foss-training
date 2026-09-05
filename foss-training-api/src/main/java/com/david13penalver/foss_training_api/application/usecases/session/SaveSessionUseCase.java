package com.david13penalver.foss_training_api.application.usecases.session;

import com.david13penalver.foss_training_api.domain.model.session.Session;

public interface SaveSessionUseCase {

    Session execute(Session session);
}