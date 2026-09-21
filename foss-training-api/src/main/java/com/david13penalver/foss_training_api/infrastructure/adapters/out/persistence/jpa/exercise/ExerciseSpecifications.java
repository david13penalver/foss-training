package com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.exercise;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.david13penalver.foss_training_api.domain.model.exercise.ExerciseSearchCriteria;

import jakarta.persistence.criteria.Predicate;

public final class ExerciseSpecifications {

    private ExerciseSpecifications() {
    }

    public static Specification<ExerciseJpaEntity> withCriteria(ExerciseSearchCriteria criteria) {
        return (root, query, cb) -> {
            if (criteria == null) {
                return cb.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();

            // Search query across name, description, tags
            if (criteria.query() != null && !criteria.query().isBlank()) {
                String pattern = "%" + criteria.query().trim().toLowerCase() + "%";
                Predicate nameLike = cb.like(cb.lower(root.get("name")), pattern);
                Predicate descLike = cb.like(cb.lower(root.get("description")), pattern);
                Predicate tagsLike = cb.like(cb.lower(root.get("tagsJson")), pattern);
                predicates.add(cb.or(nameLike, descLike, tagsLike));
            }

            // Category filter
            if (criteria.category() != null) {
                predicates.add(cb.equal(root.get("primaryCategory"), criteria.category().name()));
            }

            // Difficulty filter
            if (criteria.difficultyLevel() != null) {
                predicates.add(cb.equal(root.get("difficultyLevel"), criteria.difficultyLevel().name()));
            }

            // Muscle group filter (searches within resistance metrics json)
            if (criteria.muscleGroup() != null && !criteria.muscleGroup().isBlank()) {
                String pattern = "%" + criteria.muscleGroup().trim().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("resistanceMetricsJson")), pattern));
            }

            // Equipment filter (searches within equipment required json)
            if (criteria.equipment() != null) {
                String pattern = "%" + criteria.equipment().name().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("equipmentRequiredJson")), pattern));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
