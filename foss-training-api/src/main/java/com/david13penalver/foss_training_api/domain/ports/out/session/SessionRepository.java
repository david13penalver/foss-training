package com.david13penalver.foss_training_api.domain.ports.out.session;

import java.util.List;
import java.util.Optional;

import com.david13penalver.foss_training_api.domain.model.session.Session;

public interface SessionRepository {

    List<Session> findAll();

    Optional<Session> findById(Integer id);

    Session save(Session session);

    void deleteById(Integer id);

    boolean existsById(Integer id);
}