package com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.athlete;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataBodyweightRepository extends JpaRepository<BodyweightJpaEntity, Integer> {

    List<BodyweightJpaEntity> findAllByOrderByEntryDateAsc();

    Optional<BodyweightJpaEntity> findFirstByOrderByEntryDateDescCreatedAtDesc();
}
