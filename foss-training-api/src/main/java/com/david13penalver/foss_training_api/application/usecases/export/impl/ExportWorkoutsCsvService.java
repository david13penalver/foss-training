package com.david13penalver.foss_training_api.application.usecases.export.impl;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.export.ExportWorkoutsCsvUseCase;
import com.david13penalver.foss_training_api.domain.model.session.EnduranceInterval;
import com.david13penalver.foss_training_api.domain.model.session.EnduranceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.MobilitySessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.MobilitySet;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSet;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.model.session.SessionExercise;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.ports.out.training.TrainingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExportWorkoutsCsvService implements ExportWorkoutsCsvUseCase {

    private final TrainingRepository trainingRepository;

    private static final String CSV_HEADER = "training_id,training_date,training_name,training_status,session_rpe,exercise_name,exercise_category,item_number,set_type,weight_kg,reps,set_rpe,distance_m,duration_s,notes\n";

    @Override
    public String execute() {
        log.info("Exporting all workout logs to CSV");
        List<Training> trainings = trainingRepository.findAll().stream()
                .sorted(Comparator.comparing(
                        t -> t.getTrainingDate() != null ? t.getTrainingDate().toString() : "",
                        Comparator.naturalOrder()))
                .toList();

        StringBuilder sb = new StringBuilder();
        sb.append(CSV_HEADER);

        for (Training t : trainings) {
            String trainingId = t.getId() != null ? String.valueOf(t.getId()) : "";
            String trainingDate = t.getTrainingDate() != null ? t.getTrainingDate().toString() : "";
            String trainingName = escape(t.getName());
            String trainingStatus = t.getStatus() != null ? t.getStatus().name() : "";
            String sessionRpe = t.getRpe() != null ? String.valueOf(t.getRpe().getValue()) : "";

            Session session = t.getSession();
            if (session == null || session.getSessionExercises() == null || session.getSessionExercises().isEmpty()) {
                sb.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                        trainingId, trainingDate, trainingName, trainingStatus, sessionRpe,
                        "", "", "", "", "", "", "", "", "", escape(t.getNotes())));
                continue;
            }

            for (SessionExercise se : session.getSessionExercises()) {
                String exerciseName = se.getExercise() != null ? escape(se.getExercise().getName()) : "";

                if (se instanceof ResistanceSessionExercise rse) {
                    List<ResistanceSet> sets = rse.getSets();
                    if (sets == null || sets.isEmpty()) {
                        sb.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                                trainingId, trainingDate, trainingName, trainingStatus, sessionRpe,
                                exerciseName, "RESISTANCE", "", "", "", "", "", "", "", escape(se.getNotes())));
                    } else {
                        for (ResistanceSet set : sets) {
                            String setNum = set.getSetNumber() != null ? String.valueOf(set.getSetNumber()) : "";
                            String setType = set.getSetType() != null ? set.getSetType().name() : "";
                            String weight = set.getWeight() != null ? String.valueOf(set.getWeight().getValue()) : "";
                            String reps = set.getRepetitions() != null ? String.valueOf(set.getRepetitions()) : "";
                            String setRpe = set.getRpe() != null ? String.valueOf(set.getRpe().getValue()) : "";

                            sb.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                                    trainingId, trainingDate, trainingName, trainingStatus, sessionRpe,
                                    exerciseName, "RESISTANCE", setNum, setType, weight, reps, setRpe, "", "", escape(se.getNotes())));
                        }
                    }
                } else if (se instanceof EnduranceSessionExercise ese) {
                    List<EnduranceInterval> intervals = ese.getIntervals();
                    if (intervals == null || intervals.isEmpty()) {
                        sb.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                                trainingId, trainingDate, trainingName, trainingStatus, sessionRpe,
                                exerciseName, "ENDURANCE", "", "", "", "", "", "", "", escape(se.getNotes())));
                    } else {
                        for (EnduranceInterval interval : intervals) {
                            String intNum = interval.getIntervalNumber() != null ? String.valueOf(interval.getIntervalNumber()) : "";
                            String dist = interval.getDistance() != null ? String.valueOf(interval.getDistance().getValue()) : "";
                            String dur = interval.getDuration() != null ? String.valueOf(interval.getDuration().getTotalSeconds()) : "";

                            sb.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                                    trainingId, trainingDate, trainingName, trainingStatus, sessionRpe,
                                    exerciseName, "ENDURANCE", intNum, "", "", "", "", dist, dur, escape(se.getNotes())));
                        }
                    }
                } else if (se instanceof MobilitySessionExercise mse) {
                    List<MobilitySet> sets = mse.getSets();
                    if (sets == null || sets.isEmpty()) {
                        sb.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                                trainingId, trainingDate, trainingName, trainingStatus, sessionRpe,
                                exerciseName, "MOBILITY", "", "", "", "", "", "", "", escape(se.getNotes())));
                    } else {
                        for (MobilitySet set : sets) {
                            String setNum = set.getSetNumber() != null ? String.valueOf(set.getSetNumber()) : "";
                            String reps = set.getRepetitions() != null ? String.valueOf(set.getRepetitions()) : "";
                            String dur = set.getHoldDuration() != null ? String.valueOf(set.getHoldDuration().getTotalSeconds()) : "";

                            sb.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                                    trainingId, trainingDate, trainingName, trainingStatus, sessionRpe,
                                    exerciseName, "MOBILITY", setNum, "", "", reps, "", "", dur, escape(se.getNotes())));
                        }
                    }
                }
            }
        }

        return sb.toString();
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
