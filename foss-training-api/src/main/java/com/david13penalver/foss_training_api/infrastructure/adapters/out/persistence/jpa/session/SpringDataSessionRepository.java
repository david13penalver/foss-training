package com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.session;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataSessionRepository extends JpaRepository<SessionJpaEntity, Integer> {
}
