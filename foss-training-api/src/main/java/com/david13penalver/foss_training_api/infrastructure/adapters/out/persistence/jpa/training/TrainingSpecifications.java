package com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.training;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.david13penalver.foss_training_api.domain.model.training.TrainingSearchCriteria;

import jakarta.persistence.criteria.Predicate;

public final class TrainingSpecifications {

    private TrainingSpecifications() {
    }

    public static Specification<TrainingJpaEntity> withCriteria(TrainingSearchCriteria criteria) {
        return (root, query, cb) -> {
            if (criteria == null) {
                return cb.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();

            // Date range filter
            if (criteria.startDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("trainingDate"), criteria.startDate()));
            }
            if (criteria.endDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("trainingDate"), criteria.endDate()));
            }

            // Status filter
            if (criteria.status() != null) {
                predicates.add(cb.equal(cb.upper(root.get("status")), criteria.status().name().toUpperCase()));
            }

            // Program filter
            if (criteria.programId() != null) {
                predicates.add(cb.equal(root.get("programId"), criteria.programId()));
            }

            // Keyword query across name, description, session json
            if (criteria.query() != null && !criteria.query().isBlank()) {
                String pattern = "%" + criteria.query().trim().toLowerCase() + "%";
                Predicate nameLike = cb.like(cb.lower(root.get("name")), pattern);
                Predicate descLike = cb.like(cb.lower(root.get("description")), pattern);
                Predicate sessionLike = cb.like(cb.lower(root.get("sessionJson")), pattern);
                predicates.add(cb.or(nameLike, descLike, sessionLike));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
