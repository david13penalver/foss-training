package com.david13penalver.foss_training_api.infrastructure.adapters.out.exercise;

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
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.exercise.ExerciseSearchCriteria;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.ExerciseRepository;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.exercise.ExerciseJpaEntity;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.exercise.ExercisePersistenceMapper;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.exercise.ExerciseSpecifications;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.exercise.SpringDataExerciseRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ExerciseRepositoryImpl implements ExerciseRepository {

    private final SpringDataExerciseRepository exerciseRepository;
    private final ExercisePersistenceMapper mapper;

    @Override
    public List<Exercise> findAll() {
        return exerciseRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Exercise> findById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return exerciseRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Exercise save(Exercise exercise) {
        ExerciseJpaEntity entity = mapper.toJpaEntity(exercise);
        ExerciseJpaEntity saved = exerciseRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void deleteById(Integer id) {
        if (id != null) {
            exerciseRepository.deleteById(id);
        }
    }

    @Override
    public boolean existsById(Integer id) {
        return id != null && exerciseRepository.existsById(id);
    }

    @Override
    public List<Exercise> findByCriteria(ExerciseSearchCriteria criteria) {
        Specification<ExerciseJpaEntity> spec = ExerciseSpecifications.withCriteria(criteria);
        return exerciseRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "name")).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public PagedResult<Exercise> findByCriteria(ExerciseSearchCriteria criteria, PageQuery pageQuery) {
        Specification<ExerciseJpaEntity> spec = ExerciseSpecifications.withCriteria(criteria);
        Sort sort = resolveSort(pageQuery, "name", Sort.Direction.ASC);
        Pageable pageable = PageRequest.of(pageQuery.page(), pageQuery.size(), sort);

        Page<ExerciseJpaEntity> page = exerciseRepository.findAll(spec, pageable);
        List<Exercise> content = page.getContent().stream()
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
        Sort.Direction direction = "desc".equalsIgnoreCase(pageQuery.sortDirection())
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        return Sort.by(direction, pageQuery.sortBy());
    }
}
