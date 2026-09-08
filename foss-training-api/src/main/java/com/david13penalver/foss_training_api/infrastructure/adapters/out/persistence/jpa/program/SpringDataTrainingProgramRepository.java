package com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.program;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataTrainingProgramRepository extends JpaRepository<TrainingProgramJpaEntity, Integer> {
}
