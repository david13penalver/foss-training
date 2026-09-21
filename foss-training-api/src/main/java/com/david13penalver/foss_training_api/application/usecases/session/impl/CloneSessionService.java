package com.david13penalver.foss_training_api.application.usecases.session.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.session.CloneSessionUseCase;
import com.david13penalver.foss_training_api.domain.model.session.EnduranceInterval;
import com.david13penalver.foss_training_api.domain.model.session.EnduranceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.MobilitySet;
import com.david13penalver.foss_training_api.domain.model.session.MobilitySessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSet;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.model.session.SessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.SessionStatusEnum;
import com.david13penalver.foss_training_api.domain.ports.out.session.SessionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloneSessionService implements CloneSessionUseCase {

    private final SessionRepository sessionRepository;

    @Override
    public Session execute(Integer sessionId, String newName) {
        log.debug("Executing CloneSessionUseCase with sessionId: {}, newName: {}", sessionId, newName);
        if (sessionId == null) {
            throw new IllegalArgumentException("Session ID must not be null");
        }

        Session original = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found with id: " + sessionId));

        String resolvedName = (newName != null && !newName.isBlank())
                ? newName.trim()
                : original.getName() + " (Copy)";

        Session clone = new Session();
        clone.setName(resolvedName);
        clone.setDescription(original.getDescription());
        clone.setSessionStatus(SessionStatusEnum.PLANNED);
        clone.setNotes(original.getNotes());

        if (original.getSessionExercises() != null) {
            List<SessionExercise> clonedExercises = new ArrayList<>();
            for (SessionExercise origEx : original.getSessionExercises()) {
                clonedExercises.add(cloneExercise(origEx));
            }
            clone.setSessionExercises(clonedExercises);
        }

        return sessionRepository.save(clone);
    }

    private SessionExercise cloneExercise(SessionExercise original) {
        if (original instanceof ResistanceSessionExercise rse) {
            ResistanceSessionExercise clone = new ResistanceSessionExercise();
            clone.setExercise(rse.getExercise());
            clone.setOrderIndex(rse.getOrderIndex());
            clone.setNotes(rse.getNotes());
            if (rse.getSets() != null) {
                List<ResistanceSet> clonedSets = new ArrayList<>();
                for (ResistanceSet s : rse.getSets()) {
                    ResistanceSet clonedSet = new ResistanceSet();
                    clonedSet.setSetNumber(s.getSetNumber());
                    clonedSet.setSetType(s.getSetType());
                    clonedSet.setWeight(s.getWeight());
                    clonedSet.setRepetitions(s.getRepetitions());
                    clonedSet.setRpe(s.getRpe());
                    clonedSet.setRestSeconds(s.getRestSeconds());
                    clonedSet.setCompleted(false);
                    clonedSets.add(clonedSet);
                }
                clone.setSets(clonedSets);
            }
            return clone;
        } else if (original instanceof EnduranceSessionExercise ese) {
            EnduranceSessionExercise clone = new EnduranceSessionExercise();
            clone.setExercise(ese.getExercise());
            clone.setOrderIndex(ese.getOrderIndex());
            clone.setNotes(ese.getNotes());
            if (ese.getIntervals() != null) {
                List<EnduranceInterval> clonedIntervals = new ArrayList<>();
                for (EnduranceInterval i : ese.getIntervals()) {
                    EnduranceInterval clonedInterval = new EnduranceInterval();
                    clonedInterval.setIntervalNumber(i.getIntervalNumber());
                    clonedInterval.setDistance(i.getDistance());
                    clonedInterval.setDuration(i.getDuration());
                    clonedInterval.setPace(i.getPace());
                    clonedInterval.setAvgHeartRate(i.getAvgHeartRate());
                    clonedInterval.setMaxHeartRate(i.getMaxHeartRate());
                    clonedInterval.setAvgPower(i.getAvgPower());
                    clonedInterval.setCadence(i.getCadence());
                    clonedInterval.setRestSeconds(i.getRestSeconds());
                    clonedIntervals.add(clonedInterval);
                }
                clone.setIntervals(clonedIntervals);
            }
            return clone;
        } else if (original instanceof MobilitySessionExercise mse) {
            MobilitySessionExercise clone = new MobilitySessionExercise();
            clone.setExercise(mse.getExercise());
            clone.setOrderIndex(mse.getOrderIndex());
            clone.setNotes(mse.getNotes());
            if (mse.getSets() != null) {
                List<MobilitySet> clonedSets = new ArrayList<>();
                for (MobilitySet s : mse.getSets()) {
                    MobilitySet clonedSet = new MobilitySet();
                    clonedSet.setSetNumber(s.getSetNumber());
                    clonedSet.setHoldDuration(s.getHoldDuration());
                    clonedSet.setRepetitions(s.getRepetitions());
                    clonedSet.setBilateral(s.isBilateral());
                    clonedSets.add(clonedSet);
                }
                clone.setSets(clonedSets);
            }
            return clone;
        }
        return original;
    }
}
