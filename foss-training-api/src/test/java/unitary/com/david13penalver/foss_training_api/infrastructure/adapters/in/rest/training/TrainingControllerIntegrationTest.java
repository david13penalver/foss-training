package unitary.com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.training;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.david13penalver.foss_training_api.FossTrainingApiApplication;
import org.springframework.context.annotation.Import;
import unitary.com.david13penalver.foss_training_api.testutil.TestDatabaseCleaner;

@SpringBootTest(classes = FossTrainingApiApplication.class)
@AutoConfigureMockMvc
@Import(TestDatabaseCleaner.class)
class TrainingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestDatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        databaseCleaner.clearAll();
    }

    private String validTrainingJson(String name, String date) {
        return """
                {
                  "name": "%s",
                  "trainingDate": "%s",
                  "status": "PLANNED",
                  "session": {
                    "name": "Push Routine",
                    "sessionStatus": "PLANNED",
                    "sessionExercises": []
                  }
                }
                """.formatted(name, date);
    }

    @Test
    void createTraining_returns201WithLocation() throws Exception {
        mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validTrainingJson("Morning Push", "2026-09-08")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/trainings/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Morning Push"))
                .andExpect(jsonPath("$.trainingDate").value("2026-09-08"))
                .andExpect(jsonPath("$.session.name").value("Push Routine"));
    }

    @Test
    void getTrainingById_whenExists_returns200() throws Exception {
        createTraining("Morning Push", "2026-09-08");

        mockMvc.perform(get("/api/trainings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Morning Push"));
    }

    @Test
    void getTrainingById_whenMissing_returns404() throws Exception {
        mockMvc.perform(get("/api/trainings/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllTrainings_returnsAllSaved() throws Exception {
        createTraining("Morning Push", "2026-09-08");
        createTraining("Evening Pull", "2026-09-08");

        mockMvc.perform(get("/api/trainings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Morning Push"))
                .andExpect(jsonPath("$[1].name").value("Evening Pull"));
    }

    @Test
    void updateTraining_whenExists_returns200() throws Exception {
        createTraining("Morning Push", "2026-09-08");

        String update = validTrainingJson("Morning Push - Heavy", "2026-09-08");

        mockMvc.perform(put("/api/trainings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(update))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Morning Push - Heavy"));

        mockMvc.perform(get("/api/trainings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Morning Push - Heavy"));
    }

    @Test
    void updateTraining_whenNotFound_returns404() throws Exception {
        mockMvc.perform(put("/api/trainings/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validTrainingJson("Missing", "2026-09-08")))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTraining_removesAndReturns204() throws Exception {
        createTraining("Morning Push", "2026-09-08");

        mockMvc.perform(delete("/api/trainings/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/trainings/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTraining_whenNotFound_returns404() throws Exception {
        mockMvc.perform(delete("/api/trainings/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void trainingExists_returnsTrueThenFalse() throws Exception {
        createTraining("Morning Push", "2026-09-08");

        mockMvc.perform(get("/api/trainings/1/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        mockMvc.perform(delete("/api/trainings/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/trainings/1/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void createTraining_withBlankName_returns400() throws Exception {
        String invalid = """
                {
                  "name": "",
                  "trainingDate": "2026-09-08",
                  "session": {
                    "name": "Valid Routine"
                  }
                }
                """;

        mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalid))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.invalidParameters.name").exists());
    }

    @Test
    void createTraining_withNullDate_returns400() throws Exception {
        String invalid = """
                {
                  "name": "Workout",
                  "session": {
                    "name": "Valid Routine"
                  }
                }
                """;

        mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalid))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.invalidParameters.trainingDate").exists());
    }

    @Test
    void createTraining_withNullSession_returns400() throws Exception {
        String invalid = """
                {
                  "name": "Workout",
                  "trainingDate": "2026-09-08"
                }
                """;

        mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalid))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.invalidParameters.session").exists());
    }

    @Test
    void startTraining_whenExists_returns200AndStartedStatus() throws Exception {
        createTraining("Morning Push", "2026-09-08");

        mockMvc.perform(post("/api/trainings/1/start"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.startTime").isNotEmpty());
    }

    @Test
    void startTraining_whenNotFound_returns404() throws Exception {
        mockMvc.perform(post("/api/trainings/999/start"))
                .andExpect(status().isNotFound());
    }

    @Test
    void completeTraining_whenInProgress_returns200AndCompletedStatus() throws Exception {
        createTraining("Morning Push", "2026-09-08");
        mockMvc.perform(post("/api/trainings/1/start")).andExpect(status().isOk());

        mockMvc.perform(post("/api/trainings/1/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.endTime").isNotEmpty());
    }

    @Test
    void completeTraining_whenNotFound_returns404() throws Exception {
        mockMvc.perform(post("/api/trainings/999/complete"))
                .andExpect(status().isNotFound());
    }

    @Test
    void completeTraining_whenIllegalState_returns409Conflict() throws Exception {
        createTraining("Morning Push", "2026-09-08");
        // Status is PLANNED, completing directly throws IllegalStateException

        mockMvc.perform(post("/api/trainings/1/complete"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Operation Conflict"));
    }

    @Test
    void cancelTraining_whenExists_returns200AndCancelledStatus() throws Exception {
        createTraining("Morning Push", "2026-09-08");

        mockMvc.perform(post("/api/trainings/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void cancelTraining_whenNotFound_returns404() throws Exception {
        mockMvc.perform(post("/api/trainings/999/cancel"))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelTraining_whenIllegalState_returns409Conflict() throws Exception {
        createTraining("Morning Push", "2026-09-08");
        mockMvc.perform(post("/api/trainings/1/start")).andExpect(status().isOk());
        mockMvc.perform(post("/api/trainings/1/complete")).andExpect(status().isOk());

        // Completing a completed training or cancelling a completed training throws IllegalStateException
        mockMvc.perform(post("/api/trainings/1/cancel"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Operation Conflict"));
    }

    @Test
    void createFromSession_whenSessionExists_returns201WithLocation() throws Exception {
        String sessionJson = """
                {
                  "name": "Template Routine",
                  "description": "Base session",
                  "sessionStatus": "PLANNED",
                  "sessionExercises": []
                }
                """;
        mockMvc.perform(post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sessionJson))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/trainings/from-session/1"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/trainings/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Template Routine"))
                .andExpect(jsonPath("$.description").value("Base session"))
                .andExpect(jsonPath("$.status").value("PLANNED"))
                .andExpect(jsonPath("$.trainingDate").isNotEmpty());
    }

    @Test
    void createFromSession_withCustomDateAndName_returns201() throws Exception {
        String sessionJson = """
                {
                  "name": "Template Routine",
                  "description": "Base session",
                  "sessionStatus": "PLANNED",
                  "sessionExercises": []
                }
                """;
        mockMvc.perform(post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sessionJson))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/trainings/from-session/1")
                        .param("date", "2026-10-15")
                        .param("customName", "Custom Workout"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/trainings/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Custom Workout"))
                .andExpect(jsonPath("$.trainingDate").value("2026-10-15"));
    }

    @Test
    void createFromSession_whenSessionNotFound_returns404() throws Exception {
        mockMvc.perform(post("/api/trainings/from-session/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void pauseAndResumeTraining_lifecycleFlow() throws Exception {
        createTraining("Morning Push", "2026-09-08");

        // Cannot pause planned training
        mockMvc.perform(post("/api/trainings/1/pause"))
                .andExpect(status().isConflict());

        // Start -> IN_PROGRESS
        mockMvc.perform(post("/api/trainings/1/start"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        // Pause -> PAUSED
        mockMvc.perform(post("/api/trainings/1/pause"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAUSED"));

        // Cannot start already started/paused training
        mockMvc.perform(post("/api/trainings/1/start"))
                .andExpect(status().isConflict());

        // Resume -> IN_PROGRESS
        mockMvc.perform(post("/api/trainings/1/resume"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void completeTraining_withOptionalRpeAndNotes_savesMetrics() throws Exception {
        createTraining("Morning Push", "2026-09-08");
        mockMvc.perform(post("/api/trainings/1/start")).andExpect(status().isOk());

        String completeBody = """
                {
                  "rpe": { "value": 8.5 },
                  "notes": "Felt very strong today on all lifts"
                }
                """;

        mockMvc.perform(post("/api/trainings/1/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(completeBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.rpe.value").value(8.5))
                .andExpect(jsonPath("$.notes").value("Felt very strong today on all lifts"));
    }

    @Test
    void logSet_updateSet_and_deleteSet_flow() throws Exception {
        String json = """
                {
                  "name": "Heavy Push",
                  "trainingDate": "2026-09-08",
                  "status": "PLANNED",
                  "session": {
                    "name": "Push Routine",
                    "sessionStatus": "PLANNED",
                    "sessionExercises": [
                      {
                        "exerciseType": "resistance",
                        "id": 1,
                        "orderIndex": 1,
                        "exercise": {
                          "id": 101,
                          "name": "Barbell Bench Press",
                          "primaryCategory": "RESISTANCE"
                        },
                        "sets": []
                      }
                    ]
                  }
                }
                """;
        mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        // Start workout
        mockMvc.perform(post("/api/trainings/1/start")).andExpect(status().isOk());

        // 1. Log set 1
        String set1 = """
                {
                  "setType": "WORKING",
                  "weight": { "value": 100.0, "unit": "KG" },
                  "repetitions": 8,
                  "rpe": { "value": 8.0 },
                  "restSeconds": 120,
                  "completed": true
                }
                """;
        mockMvc.perform(post("/api/trainings/1/exercises/101/sets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(set1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.session.sessionExercises[0].sets.length()").value(1))
                .andExpect(jsonPath("$.session.sessionExercises[0].sets[0].setNumber").value(1))
                .andExpect(jsonPath("$.session.sessionExercises[0].sets[0].repetitions").value(8))
                .andExpect(jsonPath("$.session.sessionExercises[0].sets[0].completed").value(true));

        // 2. Log set 2
        String set2 = """
                {
                  "setType": "WORKING",
                  "weight": { "value": 105.0, "unit": "KG" },
                  "repetitions": 6,
                  "rpe": { "value": 9.0 },
                  "restSeconds": 120,
                  "completed": true
                }
                """;
        mockMvc.perform(post("/api/trainings/1/exercises/101/sets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(set2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.session.sessionExercises[0].sets.length()").value(2))
                .andExpect(jsonPath("$.session.sessionExercises[0].sets[1].setNumber").value(2));

        // 3. Update set 1 (change reps to 10)
        String updateSet1 = """
                {
                  "setType": "WORKING",
                  "weight": { "value": 100.0, "unit": "KG" },
                  "repetitions": 10,
                  "rpe": { "value": 8.5 },
                  "restSeconds": 120,
                  "completed": true
                }
                """;
        mockMvc.perform(put("/api/trainings/1/exercises/101/sets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateSet1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.session.sessionExercises[0].sets[0].repetitions").value(10));

        // 4. Delete set 1
        mockMvc.perform(delete("/api/trainings/1/exercises/101/sets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.session.sessionExercises[0].sets.length()").value(1))
                .andExpect(jsonPath("$.session.sessionExercises[0].sets[0].setNumber").value(1));
    }

    @Test
    void getWorkoutSummary_computesLiveSummary() throws Exception {
        String json = """
                {
                  "name": "Summary Workout",
                  "trainingDate": "2026-09-08",
                  "status": "PLANNED",
                  "session": {
                    "name": "Push Routine",
                    "sessionStatus": "PLANNED",
                    "sessionExercises": [
                      {
                        "exerciseType": "resistance",
                        "id": 1,
                        "orderIndex": 1,
                        "exercise": {
                          "id": 101,
                          "name": "Barbell Bench Press",
                          "primaryCategory": "RESISTANCE"
                        },
                        "sets": []
                      }
                    ]
                  }
                }
                """;
        mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/trainings/1/start")).andExpect(status().isOk());

        String set1 = """
                {
                  "setType": "WORKING",
                  "weight": { "value": 100.0, "unit": "KG" },
                  "repetitions": 8,
                  "completed": true
                }
                """;
        mockMvc.perform(post("/api/trainings/1/exercises/101/sets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(set1))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/trainings/1/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainingId").value(1))
                .andExpect(jsonPath("$.trainingName").value("Summary Workout"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.totalVolumeKg").value(800.0))
                .andExpect(jsonPath("$.totalWorkingSets").value(1))
                .andExpect(jsonPath("$.totalReps").value(8))
                .andExpect(jsonPath("$.exerciseSummaries.length()").value(1))
                .andExpect(jsonPath("$.exerciseSummaries[0].exerciseName").value("Barbell Bench Press"))
                .andExpect(jsonPath("$.exerciseSummaries[0].topWeightKg").value(100.0))
                .andExpect(jsonPath("$.exerciseSummaries[0].estimated1RmKg").value(126.67));
    }

    private void createTraining(String name, String date) throws Exception {
        mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validTrainingJson(name, date)))
                .andExpect(status().isCreated());
    }
}
