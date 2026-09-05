package com.david13penalver.foss_training_api.infrastructure.adapters.out.session;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.ports.out.session.SessionRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SessionRepositoryImpl implements SessionRepository {

    private final InMemorySessionDao sessionDao;

    @Override
    public List<Session> findAll() {
        return sessionDao.findAll();
    }

    @Override
    public Optional<Session> findById(Integer id) {
        return sessionDao.findById(id);
    }

    @Override
    public Session save(Session session) {
        return sessionDao.save(session);
    }

    @Override
    public void deleteById(Integer id) {
        sessionDao.deleteById(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return sessionDao.existsById(id);
    }
}