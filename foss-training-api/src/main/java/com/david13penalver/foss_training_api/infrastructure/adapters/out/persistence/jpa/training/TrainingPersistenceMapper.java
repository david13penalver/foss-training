package com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.training;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.domain.model.common.Rpe;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.model.training.TrainingStatusEnum;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.session.SessionPersistenceMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TrainingPersistenceMapper {

    private final SessionPersistenceMapper sessionPersistenceMapper;

    public TrainingJpaEntity toJpaEntity(Training domain) {
        if (domain == null) {
            return null;
        }
        return TrainingJpaEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .trainingDate(domain.getTrainingDate())
                .startTime(domain.getStartTime())
                .endTime(domain.getEndTime())
                .status(domain.getStatus() != null ? domain.getStatus().name() : null)
                .notes(domain.getNotes())
                .rpeValue(domain.getRpe() != null ? domain.getRpe().getValue() : null)
                .sessionJson(sessionPersistenceMapper.sessionToJson(domain.getSession()))
                .programId(domain.getProgramId())
                .build();
    }

    public Training toDomain(TrainingJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        Training domain = new Training();
        domain.setId(entity.getId());
        domain.setName(entity.getName());
        domain.setDescription(entity.getDescription());
        domain.setTrainingDate(entity.getTrainingDate());
        domain.setStartTime(entity.getStartTime());
        domain.setEndTime(entity.getEndTime());
        domain.setStatus(entity.getStatus() != null ? TrainingStatusEnum.valueOf(entity.getStatus()) : null);
        domain.setNotes(entity.getNotes());
        if (entity.getRpeValue() != null) {
            domain.setRpe(new Rpe(entity.getRpeValue()));
        }
        domain.setSession(sessionPersistenceMapper.sessionFromJson(entity.getSessionJson()));
        domain.setProgramId(entity.getProgramId());
        return domain;
    }
}
