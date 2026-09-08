package com.david13penalver.foss_training_api.domain.model.analytics;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.david13penalver.foss_training_api.domain.model.common.Weight;
import com.david13penalver.foss_training_api.domain.model.common.WeightUnit;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSet;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.model.session.SessionExercise;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.model.training.TrainingStatusEnum;

public class PersonalRecordCalculator {

    public static Optional<PersonalRecord> computeForExercise(Exercise exercise, List<Training> trainings) {
        if (exercise == null || exercise.getId() == null) {
            return Optional.empty();
        }
        PersonalRecord.MaxWeightRecord bestMaxWeight = null;
        PersonalRecord.BestEstimated1RmRecord best1Rm = null;
        PersonalRecord.MaxSessionVolumeRecord bestVolume = null;
        PersonalRecord.MaxRepsRecord bestReps = null;

        if (trainings != null) {
            for (Training training : trainings) {
                if (training == null || training.getStatus() != TrainingStatusEnum.COMPLETED) {
                    continue;
                }
                Session session = training.getSession();
                if (session == null || session.getSessionExercises() == null) {
                    continue;
                }
                for (SessionExercise se : session.getSessionExercises()) {
                    if (se instanceof ResistanceSessionExercise rse && se.getExercise() != null
                            && exercise.getId().equals(se.getExercise().getId())) {

                        double sessionVolume = rse.calculateVolume();
                        if (sessionVolume > 0 && (bestVolume == null || sessionVolume > bestVolume.getVolume())) {
                            bestVolume = new PersonalRecord.MaxSessionVolumeRecord(
                                    sessionVolume, WeightUnit.KG, training.getId(), training.getTrainingDate());
                        }

                        if (rse.getSets() != null) {
                            for (ResistanceSet set : rse.getSets()) {
                                Weight weight = set.getWeight();
                                Integer reps = set.getRepetitions();

                                if (reps != null && reps > 0) {
                                    if (bestReps == null || reps > bestReps.getRepetitions()) {
                                        bestReps = new PersonalRecord.MaxRepsRecord(
                                                reps,
                                                weight != null ? weight.getValue() : 0.0,
                                                weight != null ? weight.getUnit() : WeightUnit.KG,
                                                training.getId(),
                                                training.getTrainingDate());
                                    }
                                }

                                if (weight != null && weight.getValue() > 0 && reps != null && reps > 0) {
                                    double wKg = weight.toKg().getValue();
                                    double currentMaxWKg = bestMaxWeight != null
                                            ? (bestMaxWeight.getUnit() == WeightUnit.KG ? bestMaxWeight.getValue() : bestMaxWeight.getValue() * 0.45359237)
                                            : -1.0;
                                    if (wKg > currentMaxWKg) {
                                        bestMaxWeight = new PersonalRecord.MaxWeightRecord(
                                                weight.getValue(),
                                                weight.getUnit(),
                                                reps,
                                                training.getId(),
                                                training.getTrainingDate());
                                    }

                                    double est1Rm = OneRepMaxFormula.EPLEY.calculate(weight.getValue(), reps);
                                    double est1RmKg = weight.getUnit() == WeightUnit.KG ? est1Rm : est1Rm * 0.45359237;
                                    double currentBest1RmKg = best1Rm != null
                                            ? (best1Rm.getUnit() == WeightUnit.KG ? best1Rm.getEstimated1Rm() : best1Rm.getEstimated1Rm() * 0.45359237)
                                            : -1.0;

                                    if (est1RmKg > currentBest1RmKg) {
                                        best1Rm = new PersonalRecord.BestEstimated1RmRecord(
                                                est1Rm,
                                                weight.getUnit(),
                                                weight.getValue(),
                                                reps,
                                                OneRepMaxFormula.EPLEY,
                                                training.getId(),
                                                training.getTrainingDate());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return Optional.of(PersonalRecord.builder()
                .exerciseId(exercise.getId())
                .exerciseName(exercise.getName())
                .maxWeight(bestMaxWeight)
                .bestEstimated1Rm(best1Rm)
                .maxSessionVolume(bestVolume)
                .maxReps(bestReps)
                .build());
    }

    public static List<PersonalRecord> computeAll(List<Exercise> exercises, List<Training> trainings) {
        List<PersonalRecord> results = new ArrayList<>();
        if (exercises == null) {
            return results;
        }
        for (Exercise exercise : exercises) {
            computeForExercise(exercise, trainings).ifPresent(results::add);
        }
        return results;
    }
}
