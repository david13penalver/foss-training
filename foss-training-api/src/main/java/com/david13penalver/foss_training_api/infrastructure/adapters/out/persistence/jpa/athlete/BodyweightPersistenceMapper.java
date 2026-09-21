package com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.athlete;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.domain.model.athlete.BodyweightEntry;

@Component
public class BodyweightPersistenceMapper {

    public BodyweightJpaEntity toJpaEntity(BodyweightEntry domain) {
        if (domain == null) {
            return null;
        }
        return BodyweightJpaEntity.builder()
                .id(domain.getId())
                .entryDate(domain.getEntryDate())
                .weightKg(domain.getWeightKg())
                .bodyFatPercentage(domain.getBodyFatPercentage())
                .notes(domain.getNotes())
                .createdAt(domain.getCreatedAt() != null ? domain.getCreatedAt() : LocalDateTime.now())
                .build();
    }

    public BodyweightEntry toDomain(BodyweightJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return BodyweightEntry.builder()
                .id(entity.getId())
                .entryDate(entity.getEntryDate())
                .weightKg(entity.getWeightKg())
                .bodyFatPercentage(entity.getBodyFatPercentage())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
