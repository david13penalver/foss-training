package com.david13penalver.foss_training_api.domain.ports.out.athlete;

import java.util.List;
import java.util.Optional;

import com.david13penalver.foss_training_api.domain.model.athlete.BodyweightEntry;

public interface BodyweightRepository {

    BodyweightEntry save(BodyweightEntry entry);

    List<BodyweightEntry> findAll();

    Optional<BodyweightEntry> findById(Integer id);

    Optional<BodyweightEntry> findLatest();

    void deleteById(Integer id);

    boolean existsById(Integer id);
}
