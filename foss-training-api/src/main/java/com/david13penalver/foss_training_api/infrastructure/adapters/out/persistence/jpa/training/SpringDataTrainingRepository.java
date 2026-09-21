package com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.training;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SpringDataTrainingRepository extends JpaRepository<TrainingJpaEntity, Integer>, JpaSpecificationExecutor<TrainingJpaEntity> {

    List<TrainingJpaEntity> findByProgramIdOrderByTrainingDateAsc(Integer programId);

    List<TrainingJpaEntity> findByNameStartingWithIgnoreCaseOrderByTrainingDateAsc(String prefix);
}
