package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.session;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.domain.model.common.Distance;
import com.david13penalver.foss_training_api.domain.model.common.Duration;
import com.david13penalver.foss_training_api.domain.model.common.Pace;
import com.david13penalver.foss_training_api.domain.model.common.Rpe;
import com.david13penalver.foss_training_api.domain.model.common.Weight;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.session.EnduranceInterval;
import com.david13penalver.foss_training_api.domain.model.session.EnduranceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.MobilitySessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.MobilitySet;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSet;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.model.session.SessionExercise;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.common.DistanceDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.common.DurationDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.common.PaceDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.common.RpeDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.common.WeightDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.exercise.ExerciseDtoMapper;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.exercise.ExerciseResponseDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SessionDtoMapper {

    private final ExerciseDtoMapper exerciseDtoMapper;

    public Session toEntity(SessionRequestDto dto) {
        if (dto == null) {
            return null;
        }
        Session session = new Session();
        session.setId(dto.getId());
        session.setName(dto.getName());
        session.setDescription(dto.getDescription());
        session.setSessionStatus(dto.getSessionStatus());
        session.setStartTime(dto.getStartTime());
        session.setEndTime(dto.getEndTime());
        session.setNotes(dto.getNotes());
        if (dto.getRpe() != null && dto.getRpe().getValue() != null) {
            session.setRpe(new Rpe(dto.getRpe().getValue()));
        }
        if (dto.getSessionExercises() != null) {
            List<SessionExercise> exercises = new ArrayList<>();
            for (SessionExerciseDto exerciseDto : dto.getSessionExercises()) {
                SessionExercise entity = toExerciseEntity(exerciseDto);
                if (entity != null) {
                    exercises.add(entity);
                }
            }
            session.setSessionExercises(exercises);
        }
        return session;
    }

    public SessionExercise toExerciseEntity(SessionExerciseDto dto) {
        if (dto == null) {
            return null;
        }
        Exercise exercise = toExerciseDomain(dto.getExercise());

        if (dto instanceof ResistanceSessionExerciseDto rDto) {
            ResistanceSessionExercise entity = new ResistanceSessionExercise();
            entity.setId(rDto.getId());
            entity.setExercise(exercise);
            entity.setOrderIndex(rDto.getOrderIndex());
            entity.setNotes(rDto.getNotes());
            if (rDto.getSets() != null) {
                for (ResistanceSetDto setDto : rDto.getSets()) {
                    ResistanceSet set = new ResistanceSet();
                    set.setSetNumber(setDto.getSetNumber());
                    set.setSetType(setDto.getSetType());
                    if (setDto.getWeight() != null && setDto.getWeight().getValue() != null && setDto.getWeight().getUnit() != null) {
                        set.setWeight(new Weight(setDto.getWeight().getValue(), setDto.getWeight().getUnit()));
                    }
                    set.setRepetitions(setDto.getRepetitions());
                    if (setDto.getRpe() != null && setDto.getRpe().getValue() != null) {
                        set.setRpe(new Rpe(setDto.getRpe().getValue()));
                    }
                    set.setRestSeconds(setDto.getRestSeconds());
                    entity.addSet(set);
                }
            }
            return entity;
        } else if (dto instanceof EnduranceSessionExerciseDto eDto) {
            EnduranceSessionExercise entity = new EnduranceSessionExercise();
            entity.setId(eDto.getId());
            entity.setExercise(exercise);
            entity.setOrderIndex(eDto.getOrderIndex());
            entity.setNotes(eDto.getNotes());
            if (eDto.getIntervals() != null) {
                for (EnduranceIntervalDto iDto : eDto.getIntervals()) {
                    EnduranceInterval interval = new EnduranceInterval();
                    interval.setIntervalNumber(iDto.getIntervalNumber());
                    if (iDto.getDistance() != null && iDto.getDistance().getValue() != null && iDto.getDistance().getUnit() != null) {
                        interval.setDistance(new Distance(iDto.getDistance().getValue(), iDto.getDistance().getUnit()));
                    }
                    if (iDto.getDuration() != null && iDto.getDuration().getTotalSeconds() != null) {
                        interval.setDuration(new Duration(iDto.getDuration().getTotalSeconds()));
                    }
                    if (iDto.getPace() != null && iDto.getPace().getSecondsPerUnit() != null && iDto.getPace().getUnit() != null) {
                        interval.setPace(new Pace(iDto.getPace().getSecondsPerUnit(), iDto.getPace().getUnit()));
                    }
                    interval.setAvgHeartRate(iDto.getAvgHeartRate());
                    interval.setMaxHeartRate(iDto.getMaxHeartRate());
                    interval.setAvgPower(iDto.getAvgPower());
                    interval.setCadence(iDto.getCadence());
                    interval.setRestSeconds(iDto.getRestSeconds());
                    entity.addInterval(interval);
                }
            }
            return entity;
        } else if (dto instanceof MobilitySessionExerciseDto mDto) {
            MobilitySessionExercise entity = new MobilitySessionExercise();
            entity.setId(mDto.getId());
            entity.setExercise(exercise);
            entity.setOrderIndex(mDto.getOrderIndex());
            entity.setNotes(mDto.getNotes());
            if (mDto.getSets() != null) {
                for (MobilitySetDto sDto : mDto.getSets()) {
                    MobilitySet set = new MobilitySet();
                    set.setSetNumber(sDto.getSetNumber());
                    if (sDto.getHoldDuration() != null && sDto.getHoldDuration().getTotalSeconds() != null) {
                        set.setHoldDuration(new Duration(sDto.getHoldDuration().getTotalSeconds()));
                    }
                    set.setRepetitions(sDto.getRepetitions());
                    set.setBilateral(sDto.isBilateral());
                    entity.addSet(set);
                }
            }
            return entity;
        }
        return null;
    }

    private Exercise toExerciseDomain(ExerciseResponseDto dto) {
        if (dto == null) {
            return null;
        }
        Exercise ex = new Exercise();
        ex.setId(dto.getId());
        ex.setName(dto.getName());
        ex.setDescription(dto.getDescription());
        ex.setPrimaryCategory(dto.getPrimaryCategory());
        ex.setSecondaryCategories(dto.getSecondaryCategories());
        ex.setResistanceMetrics(dto.getResistanceMetrics());
        ex.setEnduranceMetrics(dto.getEnduranceMetrics());
        ex.setMobilityMetrics(dto.getMobilityMetrics());
        ex.setEquipmentRequired(dto.getEquipmentRequired());
        ex.setDifficultyLevel(dto.getDifficultyLevel());
        return ex;
    }

    public SessionResponseDto toResponseDto(Session session) {
        if (session == null) {
            return null;
        }
        SessionResponseDto dto = new SessionResponseDto();
        dto.setId(session.getId());
        dto.setName(session.getName());
        dto.setDescription(session.getDescription());
        dto.setSessionStatus(session.getSessionStatus());
        dto.setStartTime(session.getStartTime());
        dto.setEndTime(session.getEndTime());
        dto.setNotes(session.getNotes());
        if (session.getRpe() != null) {
            dto.setRpe(new RpeDto(session.getRpe().getValue()));
        }
        if (session.calculateDuration() != null) {
            dto.setDuration(new DurationDto(session.calculateDuration().getTotalSeconds()));
        }
        dto.setTotalVolume(session.calculateTotalVolume());

        if (session.getSessionExercises() != null) {
            List<SessionExerciseDto> exerciseDtos = new ArrayList<>();
            for (SessionExercise exercise : session.getSessionExercises()) {
                SessionExerciseDto exDto = toExerciseResponseDto(exercise);
                if (exDto != null) {
                    exerciseDtos.add(exDto);
                }
            }
            dto.setSessionExercises(exerciseDtos);
        }
        return dto;
    }

    public SessionExerciseDto toExerciseResponseDto(SessionExercise entity) {
        if (entity == null) {
            return null;
        }
        ExerciseResponseDto exDto = exerciseDtoMapper.toResponseDto(entity.getExercise());

        if (entity instanceof ResistanceSessionExercise r) {
            ResistanceSessionExerciseDto dto = new ResistanceSessionExerciseDto();
            dto.setId(r.getId());
            dto.setExercise(exDto);
            dto.setOrderIndex(r.getOrderIndex());
            dto.setNotes(r.getNotes());
            if (r.getSets() != null) {
                List<ResistanceSetDto> setDtos = new ArrayList<>();
                for (ResistanceSet s : r.getSets()) {
                    ResistanceSetDto sDto = new ResistanceSetDto();
                    sDto.setSetNumber(s.getSetNumber());
                    sDto.setSetType(s.getSetType());
                    if (s.getWeight() != null) {
                        sDto.setWeight(new WeightDto(s.getWeight().getValue(), s.getWeight().getUnit()));
                    }
                    sDto.setRepetitions(s.getRepetitions());
                    if (s.getRpe() != null) {
                        sDto.setRpe(new RpeDto(s.getRpe().getValue()));
                    }
                    sDto.setRestSeconds(s.getRestSeconds());
                    setDtos.add(sDto);
                }
                dto.setSets(setDtos);
            }
            return dto;
        } else if (entity instanceof EnduranceSessionExercise e) {
            EnduranceSessionExerciseDto dto = new EnduranceSessionExerciseDto();
            dto.setId(e.getId());
            dto.setExercise(exDto);
            dto.setOrderIndex(e.getOrderIndex());
            dto.setNotes(e.getNotes());
            if (e.getIntervals() != null) {
                List<EnduranceIntervalDto> intervalDtos = new ArrayList<>();
                for (EnduranceInterval i : e.getIntervals()) {
                    EnduranceIntervalDto iDto = new EnduranceIntervalDto();
                    iDto.setIntervalNumber(i.getIntervalNumber());
                    if (i.getDistance() != null) {
                        iDto.setDistance(new DistanceDto(i.getDistance().getValue(), i.getDistance().getUnit()));
                    }
                    if (i.getDuration() != null) {
                        iDto.setDuration(new DurationDto(i.getDuration().getTotalSeconds()));
                    }
                    if (i.getPace() != null) {
                        iDto.setPace(new PaceDto(i.getPace().getSecondsPerUnit(), i.getPace().getUnit()));
                    }
                    iDto.setAvgHeartRate(i.getAvgHeartRate());
                    iDto.setMaxHeartRate(i.getMaxHeartRate());
                    iDto.setAvgPower(i.getAvgPower());
                    iDto.setCadence(i.getCadence());
                    iDto.setRestSeconds(i.getRestSeconds());
                    intervalDtos.add(iDto);
                }
                dto.setIntervals(intervalDtos);
            }
            return dto;
        } else if (entity instanceof MobilitySessionExercise m) {
            MobilitySessionExerciseDto dto = new MobilitySessionExerciseDto();
            dto.setId(m.getId());
            dto.setExercise(exDto);
            dto.setOrderIndex(m.getOrderIndex());
            dto.setNotes(m.getNotes());
            if (m.getSets() != null) {
                List<MobilitySetDto> setDtos = new ArrayList<>();
                for (MobilitySet s : m.getSets()) {
                    MobilitySetDto sDto = new MobilitySetDto();
                    sDto.setSetNumber(s.getSetNumber());
                    if (s.getHoldDuration() != null) {
                        sDto.setHoldDuration(new DurationDto(s.getHoldDuration().getTotalSeconds()));
                    }
                    sDto.setRepetitions(s.getRepetitions());
                    sDto.setBilateral(s.isBilateral());
                    setDtos.add(sDto);
                }
                dto.setSets(setDtos);
            }
            return dto;
        }
        return null;
    }

    public List<SessionResponseDto> toResponseDtoList(List<Session> sessions) {
        if (sessions == null) {
            return List.of();
        }
        return sessions.stream().map(this::toResponseDto).toList();
    }
}
