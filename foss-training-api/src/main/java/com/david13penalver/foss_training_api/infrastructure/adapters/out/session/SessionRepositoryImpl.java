package com.david13penalver.foss_training_api.infrastructure.adapters.out.session;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.ports.out.session.SessionRepository;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.session.SessionJpaEntity;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.session.SessionPersistenceMapper;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.session.SpringDataSessionRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SessionRepositoryImpl implements SessionRepository {

    private final SpringDataSessionRepository sessionRepository;
    private final SessionPersistenceMapper mapper;

    @Override
    public List<Session> findAll() {
        return sessionRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Session> findById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return sessionRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Session save(Session session) {
        SessionJpaEntity entity = mapper.toJpaEntity(session);
        SessionJpaEntity saved = sessionRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void deleteById(Integer id) {
        if (id != null) {
            sessionRepository.deleteById(id);
        }
    }

    @Override
    public boolean existsById(Integer id) {
        return id != null && sessionRepository.existsById(id);
    }
}
