package com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.program;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.domain.model.program.PeriodizationType;
import com.david13penalver.foss_training_api.domain.model.program.ProgramLevel;
import com.david13penalver.foss_training_api.domain.model.program.ProgramWorkout;
import com.david13penalver.foss_training_api.domain.model.program.TrainingProgram;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.session.SessionPersistenceMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.DeserializationFeature;

@Component
public class TrainingProgramPersistenceMapper {

    private final SessionPersistenceMapper sessionPersistenceMapper;
    private final ObjectMapper objectMapper;

    public TrainingProgramPersistenceMapper(SessionPersistenceMapper sessionPersistenceMapper) {
        this.sessionPersistenceMapper = sessionPersistenceMapper;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public TrainingProgramJpaEntity toJpaEntity(TrainingProgram domain) {
        if (domain == null) {
            return null;
        }
        return TrainingProgramJpaEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .durationWeeks(domain.getDurationWeeks())
                .periodizationType(domain.getPeriodizationType() != null ? domain.getPeriodizationType().name() : null)
                .level(domain.getLevel() != null ? domain.getLevel().name() : null)
                .workoutsJson(workoutsToJson(domain.getWorkouts()))
                .isActive(domain.getIsActive() != null ? domain.getIsActive() : true)
                .build();
    }

    public TrainingProgram toDomain(TrainingProgramJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        TrainingProgram domain = new TrainingProgram();
        domain.setId(entity.getId());
        domain.setName(entity.getName());
        domain.setDescription(entity.getDescription());
        domain.setDurationWeeks(entity.getDurationWeeks());
        domain.setPeriodizationType(entity.getPeriodizationType() != null ? PeriodizationType.valueOf(entity.getPeriodizationType()) : null);
        domain.setLevel(entity.getLevel() != null ? ProgramLevel.valueOf(entity.getLevel()) : null);
        domain.setIsActive(entity.isActive());
        domain.setWorkouts(workoutsFromJson(entity.getWorkoutsJson()));
        return domain;
    }

    private String workoutsToJson(List<ProgramWorkout> workouts) {
        if (workouts == null || workouts.isEmpty()) {
            return null;
        }
        List<PersistenceProgramWorkout> list = new ArrayList<>();
        for (ProgramWorkout pw : workouts) {
            list.add(PersistenceProgramWorkout.builder()
                    .dayOfWeek(pw.getDayOfWeek())
                    .focus(pw.getFocus())
                    .sessionJson(sessionPersistenceMapper.sessionToJson(pw.getSession()))
                    .build());
        }
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing workouts to JSON", e);
        }
    }

    private List<ProgramWorkout> workoutsFromJson(String json) {
        if (json == null || json.isBlank()) {
            return new ArrayList<>();
        }
        try {
            List<PersistenceProgramWorkout> list = objectMapper.readValue(json, new TypeReference<List<PersistenceProgramWorkout>>() {});
            List<ProgramWorkout> workouts = new ArrayList<>();
            for (PersistenceProgramWorkout pw : list) {
                workouts.add(ProgramWorkout.builder()
                        .dayOfWeek(pw.getDayOfWeek())
                        .focus(pw.getFocus())
                        .session(sessionPersistenceMapper.sessionFromJson(pw.getSessionJson()))
                        .build());
            }
            return workouts;
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing workouts from JSON", e);
        }
    }
}
