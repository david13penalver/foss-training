package com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.session;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.domain.model.common.Rpe;
import com.david13penalver.foss_training_api.domain.model.session.EnduranceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.MobilitySessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.model.session.SessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.SessionStatusEnum;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.common.DomainValueObjectMapperModule;
import com.fasterxml.jackson.databind.DeserializationFeature;

@Component
public class SessionPersistenceMapper {

    private final ObjectMapper objectMapper;

    public SessionPersistenceMapper() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.registerModule(new DomainValueObjectMapperModule());
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public SessionJpaEntity toJpaEntity(Session domain) {
        if (domain == null) {
            return null;
        }
        return SessionJpaEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .sessionStatus(domain.getSessionStatus() != null ? domain.getSessionStatus().name() : null)
                .startTime(domain.getStartTime())
                .endTime(domain.getEndTime())
                .notes(domain.getNotes())
                .rpeValue(domain.getRpe() != null ? domain.getRpe().getValue() : null)
                .sessionExercisesJson(exercisesToJson(domain.getSessionExercises()))
                .build();
    }

    public Session toDomain(SessionJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        Session domain = new Session();
        domain.setId(entity.getId());
        domain.setName(entity.getName());
        domain.setDescription(entity.getDescription());
        domain.setSessionStatus(entity.getSessionStatus() != null ? SessionStatusEnum.valueOf(entity.getSessionStatus()) : null);
        domain.setStartTime(entity.getStartTime());
        domain.setEndTime(entity.getEndTime());
        domain.setNotes(entity.getNotes());
        if (entity.getRpeValue() != null) {
            domain.setRpe(new Rpe(entity.getRpeValue()));
        }
        domain.setSessionExercises(exercisesFromJson(entity.getSessionExercisesJson()));
        return domain;
    }

    public String sessionToJson(Session session) {
        if (session == null) {
            return null;
        }
        SessionJpaEntity entity = toJpaEntity(session);
        try {
            return objectMapper.writeValueAsString(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing Session to JSON", e);
        }
    }

    public Session sessionFromJson(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            SessionJpaEntity entity = objectMapper.readValue(json, SessionJpaEntity.class);
            return toDomain(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing Session from JSON", e);
        }
    }

    private String exercisesToJson(List<SessionExercise> exercises) {
        if (exercises == null) {
            return null;
        }
        List<PersistenceSessionExercise> list = new ArrayList<>();
        for (SessionExercise se : exercises) {
            PersistenceSessionExercise pe = new PersistenceSessionExercise();
            pe.setId(se.getId());
            pe.setExercise(se.getExercise());
            pe.setOrderIndex(se.getOrderIndex());
            pe.setNotes(se.getNotes());

            if (se instanceof ResistanceSessionExercise r) {
                pe.setType("RESISTANCE");
                pe.setResistanceSets(r.getSets());
            } else if (se instanceof EnduranceSessionExercise e) {
                pe.setType("ENDURANCE");
                pe.setEnduranceIntervals(e.getIntervals());
            } else if (se instanceof MobilitySessionExercise m) {
                pe.setType("MOBILITY");
                pe.setMobilitySets(m.getSets());
            }
            list.add(pe);
        }
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing session exercises to JSON", e);
        }
    }

    private List<SessionExercise> exercisesFromJson(String json) {
        if (json == null || json.isBlank()) {
            return new ArrayList<>();
        }
        try {
            List<PersistenceSessionExercise> list = objectMapper.readValue(json, new TypeReference<List<PersistenceSessionExercise>>() {});
            List<SessionExercise> result = new ArrayList<>();
            for (PersistenceSessionExercise pe : list) {
                if ("RESISTANCE".equalsIgnoreCase(pe.getType())) {
                    ResistanceSessionExercise r = new ResistanceSessionExercise();
                    r.setId(pe.getId());
                    r.setExercise(pe.getExercise());
                    r.setOrderIndex(pe.getOrderIndex());
                    r.setNotes(pe.getNotes());
                    if (pe.getResistanceSets() != null) {
                        r.setSets(new ArrayList<>(pe.getResistanceSets()));
                    }
                    result.add(r);
                } else if ("ENDURANCE".equalsIgnoreCase(pe.getType())) {
                    EnduranceSessionExercise e = new EnduranceSessionExercise();
                    e.setId(pe.getId());
                    e.setExercise(pe.getExercise());
                    e.setOrderIndex(pe.getOrderIndex());
                    e.setNotes(pe.getNotes());
                    if (pe.getEnduranceIntervals() != null) {
                        e.setIntervals(new ArrayList<>(pe.getEnduranceIntervals()));
                    }
                    result.add(e);
                } else if ("MOBILITY".equalsIgnoreCase(pe.getType())) {
                    MobilitySessionExercise m = new MobilitySessionExercise();
                    m.setId(pe.getId());
                    m.setExercise(pe.getExercise());
                    m.setOrderIndex(pe.getOrderIndex());
                    m.setNotes(pe.getNotes());
                    if (pe.getMobilitySets() != null) {
                        m.setSets(new ArrayList<>(pe.getMobilitySets()));
                    }
                    result.add(m);
                }
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing session exercises from JSON", e);
        }
    }
}
