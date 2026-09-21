package com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.exercise;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SpringDataExerciseRepository extends JpaRepository<ExerciseJpaEntity, Integer>, JpaSpecificationExecutor<ExerciseJpaEntity> {
}
