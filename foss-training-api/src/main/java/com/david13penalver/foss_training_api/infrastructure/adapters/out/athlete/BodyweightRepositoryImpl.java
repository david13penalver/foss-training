package com.david13penalver.foss_training_api.infrastructure.adapters.out.athlete;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.david13penalver.foss_training_api.domain.model.athlete.BodyweightEntry;
import com.david13penalver.foss_training_api.domain.ports.out.athlete.BodyweightRepository;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.athlete.BodyweightJpaEntity;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.athlete.BodyweightPersistenceMapper;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.athlete.SpringDataBodyweightRepository;

import lombok.RequiredArgsConstructor;

@Repository
public class BodyweightRepositoryImpl implements BodyweightRepository {

    private final SpringDataBodyweightRepository repository;
    private final BodyweightPersistenceMapper mapper;

    public BodyweightRepositoryImpl(SpringDataBodyweightRepository repository, BodyweightPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public BodyweightEntry save(BodyweightEntry entry) {
        BodyweightJpaEntity entity = mapper.toJpaEntity(entry);
        BodyweightJpaEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<BodyweightEntry> findAll() {
        return repository.findAllByOrderByEntryDateAsc().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<BodyweightEntry> findById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<BodyweightEntry> findLatest() {
        return repository.findFirstByOrderByEntryDateDescCreatedAtDesc()
                .map(mapper::toDomain);
    }

    @Override
    public void deleteById(Integer id) {
        if (id != null) {
            repository.deleteById(id);
        }
    }

    @Override
    public boolean existsById(Integer id) {
        return id != null && repository.existsById(id);
    }
}
