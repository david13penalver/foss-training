package com.david13penalver.foss_training_api.application.usecases.export.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.david13penalver.foss_training_api.application.usecases.export.ImportWorkoutsCsvUseCase;
import com.david13penalver.foss_training_api.domain.model.common.Rpe;
import com.david13penalver.foss_training_api.domain.model.common.Weight;
import com.david13penalver.foss_training_api.domain.model.common.WeightUnit;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.exercise.ExerciseCategory;
import com.david13penalver.foss_training_api.domain.model.export.ImportSummary;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSet;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.model.session.SessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.SessionStatusEnum;
import com.david13penalver.foss_training_api.domain.model.session.SetType;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.model.training.TrainingStatusEnum;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.ExerciseRepository;
import com.david13penalver.foss_training_api.domain.ports.out.training.TrainingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImportWorkoutsCsvService implements ImportWorkoutsCsvUseCase {

    private final TrainingRepository trainingRepository;
    private final ExerciseRepository exerciseRepository;

    @Override
    @Transactional
    public ImportSummary execute(String csvContent) {
        log.info("Starting workout logs import from CSV");
        if (csvContent == null || csvContent.isBlank()) {
            return new ImportSummary(0, 0, 0, 0, 0);
        }

        String[] lines = csvContent.split("\\r?\\n");
        if (lines.length <= 1) {
            return new ImportSummary(0, 0, 0, 0, 0);
        }

        int importedCount = 0;
        Training currentTraining = null;
        String currentKey = null;

        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) {
                continue;
            }
            List<String> tokens = parseCsvLine(line);
            if (tokens.isEmpty()) {
                continue;
            }

            String trainingDateStr = tokens.size() > 1 ? tokens.get(1) : "";
            String trainingName = tokens.size() > 2 ? tokens.get(2) : "Imported Workout";
            String trainingStatus = tokens.size() > 3 ? tokens.get(3) : "COMPLETED";
            String sessionRpeStr = tokens.size() > 4 ? tokens.get(4) : "";
            String exerciseName = tokens.size() > 5 ? tokens.get(5) : "";
            String category = tokens.size() > 6 ? tokens.get(6) : "RESISTANCE";
            String itemNumStr = tokens.size() > 7 ? tokens.get(7) : "1";
            String setTypeStr = tokens.size() > 8 ? tokens.get(8) : "NORMAL";
            String weightStr = tokens.size() > 9 ? tokens.get(9) : "0";
            String repsStr = tokens.size() > 10 ? tokens.get(10) : "0";
            String setRpeStr = tokens.size() > 11 ? tokens.get(11) : "";
            String notes = tokens.size() > 14 ? tokens.get(14) : "";

            String key = trainingDateStr + "|" + trainingName;
            if (currentTraining == null || !key.equals(currentKey)) {
                if (currentTraining != null) {
                    try {
                        trainingRepository.save(currentTraining);
                        importedCount++;
                    } catch (Exception e) {
                        log.warn("Failed to save imported training: {}", currentTraining.getName(), e);
                    }
                }
                currentKey = key;
                LocalDate date = null;
                try {
                    if (!trainingDateStr.isBlank()) {
                        date = LocalDate.parse(trainingDateStr);
                    }
                } catch (Exception ignored) {
                }
                if (date == null) {
                    date = LocalDate.now();
                }

                Session session = new Session();
                session.setName(trainingName);
                session.setSessionStatus(SessionStatusEnum.COMPLETED);
                session.setSessionExercises(new ArrayList<>());

                currentTraining = new Training();
                currentTraining.setName(trainingName);
                currentTraining.setTrainingDate(date);
                currentTraining.setStatus(TrainingStatusEnum.COMPLETED);
                currentTraining.setSession(session);
                if (!sessionRpeStr.isBlank()) {
                    try {
                        currentTraining.setRpe(new Rpe(Integer.parseInt(sessionRpeStr)));
                    } catch (Exception ignored) {
                    }
                }
            }

            if (!exerciseName.isBlank()) {
                Exercise exercise = findOrCreateExercise(exerciseName);
                SessionExercise se = findOrAddSessionExercise(currentTraining.getSession(), exercise);

                if (se instanceof ResistanceSessionExercise rse) {
                    ResistanceSet set = new ResistanceSet();
                    try {
                        set.setSetNumber(Integer.parseInt(itemNumStr));
                    } catch (Exception e) {
                        set.setSetNumber(1);
                    }
                    try {
                        set.setSetType(SetType.valueOf(setTypeStr.toUpperCase()));
                    } catch (Exception e) {
                        set.setSetType(SetType.WORKING);
                    }
                    try {
                        set.setWeight(new Weight(Double.parseDouble(weightStr), WeightUnit.KG));
                    } catch (Exception e) {
                        set.setWeight(new Weight(0.0, WeightUnit.KG));
                    }
                    try {
                        set.setRepetitions(Integer.parseInt(repsStr));
                    } catch (Exception e) {
                        set.setRepetitions(0);
                    }
                    if (!setRpeStr.isBlank()) {
                        try {
                            set.setRpe(new Rpe(Integer.parseInt(setRpeStr)));
                        } catch (Exception ignored) {
                        }
                    }
                    set.setCompleted(true);
                    rse.addSet(set);
                }
            }
        }

        if (currentTraining != null) {
            try {
                trainingRepository.save(currentTraining);
                importedCount++;
            } catch (Exception e) {
                log.warn("Failed to save imported training: {}", currentTraining.getName(), e);
            }
        }

        return new ImportSummary(0, 0, 0, importedCount, 0);
    }

    private Exercise findOrCreateExercise(String exerciseName) {
        return exerciseRepository.findAll().stream()
                .filter(e -> e.getName() != null && e.getName().equalsIgnoreCase(exerciseName))
                .findFirst()
                .orElseGet(() -> {
                    Exercise newEx = new Exercise();
                    newEx.setName(exerciseName);
                    newEx.setPrimaryCategory(ExerciseCategory.RESISTANCE);
                    newEx.setActive(true);
                    try {
                        return exerciseRepository.save(newEx);
                    } catch (Exception e) {
                        return newEx;
                    }
                });
    }

    private SessionExercise findOrAddSessionExercise(Session session, Exercise exercise) {
        if (session.getSessionExercises() == null) {
            session.setSessionExercises(new ArrayList<>());
        }
        for (SessionExercise se : session.getSessionExercises()) {
            if (se.getExercise() != null && se.getExercise().getName() != null
                    && se.getExercise().getName().equalsIgnoreCase(exercise.getName())) {
                return se;
            }
        }
        ResistanceSessionExercise rse = new ResistanceSessionExercise();
        rse.setExercise(exercise);
        rse.setOrderIndex(session.getSessionExercises().size() + 1);
        rse.setSets(new ArrayList<>());
        session.addExercise(rse);
        return rse;
    }

    private List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                    sb.append('\"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                result.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        result.add(sb.toString().trim());
        return result;
    }
}
