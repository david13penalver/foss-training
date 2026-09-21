package com.david13penalver.foss_training_api.infrastructure.adapters.out.training;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import com.david13penalver.foss_training_api.domain.model.common.PageQuery;
import com.david13penalver.foss_training_api.domain.model.common.PagedResult;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.model.training.TrainingSearchCriteria;
import com.david13penalver.foss_training_api.domain.ports.out.training.TrainingRepository;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.training.SpringDataTrainingRepository;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.training.TrainingJpaEntity;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.training.TrainingPersistenceMapper;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.training.TrainingSpecifications;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TrainingRepositoryImpl implements TrainingRepository {

    private final SpringDataTrainingRepository trainingRepository;
    private final TrainingPersistenceMapper mapper;

    @Override
    public List<Training> findAll() {
        return trainingRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Training> findById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return trainingRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Training save(Training training) {
        TrainingJpaEntity entity = mapper.toJpaEntity(training);
        TrainingJpaEntity saved = trainingRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void deleteById(Integer id) {
        if (id != null) {
            trainingRepository.deleteById(id);
        }
    }

    @Override
    public boolean existsById(Integer id) {
        return id != null && trainingRepository.existsById(id);
    }

    @Override
    public List<Training> findByProgramId(Integer programId) {
        if (programId == null) {
            return List.of();
        }
        return trainingRepository.findByProgramIdOrderByTrainingDateAsc(programId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Training> findByCriteria(TrainingSearchCriteria criteria) {
        Specification<TrainingJpaEntity> spec = TrainingSpecifications.withCriteria(criteria);
        return trainingRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "trainingDate")).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public PagedResult<Training> findByCriteria(TrainingSearchCriteria criteria, PageQuery pageQuery) {
        Specification<TrainingJpaEntity> spec = TrainingSpecifications.withCriteria(criteria);
        Sort sort = resolveSort(pageQuery, "trainingDate", Sort.Direction.DESC);
        Pageable pageable = PageRequest.of(pageQuery.page(), pageQuery.size(), sort);

        Page<TrainingJpaEntity> page = trainingRepository.findAll(spec, pageable);
        List<Training> content = page.getContent().stream()
                .map(mapper::toDomain)
                .toList();

        return new PagedResult<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    private Sort resolveSort(PageQuery pageQuery, String defaultProperty, Sort.Direction defaultDirection) {
        if (pageQuery == null || pageQuery.sortBy() == null || pageQuery.sortBy().isBlank()) {
            return Sort.by(defaultDirection, defaultProperty);
        }
        Sort.Direction direction = "asc".equalsIgnoreCase(pageQuery.sortDirection())
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        return Sort.by(direction, pageQuery.sortBy());
    }
}
