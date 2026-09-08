package com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.training;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataTrainingRepository extends JpaRepository<TrainingJpaEntity, Integer> {
}
