package unitary.com.david13penalver.foss_training_api.infrastructure.e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

class AnalyticsE2ETest extends E2EIntegrationTestBase {

    @Test
    void calculate1Rm_e2eFlow() {
        ResponseEntity<String> response = get("/api/analytics/1rm?weight=100.0&reps=5&formula=EPLEY");

        assertStatus(response, 200);
        assertEquals(100.0, jsonDouble(response.getBody(), "$.weight"));
        assertEquals("KG", jsonString(response.getBody(), "$.unit"));
        assertEquals(5, jsonInt(response.getBody(), "$.repetitions"));
        assertEquals("EPLEY", jsonString(response.getBody(), "$.formula"));
        assertEquals(116.67, jsonDouble(response.getBody(), "$.estimated1Rm"));
        assertEquals(105.0, jsonDouble(response.getBody(), "$.percentages['90']"));
    }

    @Test
    void personalRecords_e2eFlow() {
        // 1. Create Exercise
        String exerciseJson = """
                {
                  "name": "Barbell Bench Press",
                  "primaryCategory": "RESISTANCE"
                }
                """;
        ResponseEntity<String> exResp = post("/api/exercises", exerciseJson);
        assertStatus(exResp, 201);
        int exerciseId = jsonInt(exResp.getBody(), "$.id");

        // 2. Create Session with Bench Press Sets
        String sessionJson = """
                {
                  "name": "Chest Workout",
                  "sessionStatus": "PLANNED",
                  "sessionExercises": [
                    {
                      "exerciseType": "resistance",
                      "orderIndex": 1,
                      "exercise": {
                        "id": %d,
                        "name": "Barbell Bench Press",
                        "primaryCategory": "RESISTANCE"
                      },
                      "sets": [
                        {
                          "setNumber": 1,
                          "setType": "WORKING",
                          "weight": {"value": 100.0, "unit": "KG"},
                          "repetitions": 5
                        },
                        {
                          "setNumber": 2,
                          "setType": "WORKING",
                          "weight": {"value": 120.0, "unit": "KG"},
                          "repetitions": 2
                        }
                      ]
                    }
                  ]
                }
                """.formatted(exerciseId);
        ResponseEntity<String> sessionResp = post("/api/sessions", sessionJson);
        assertStatus(sessionResp, 201);
        int sessionId = jsonInt(sessionResp.getBody(), "$.id");

        // 3. Create Training, Start, and Complete
        ResponseEntity<String> trainingResp = post("/api/trainings/from-session/" + sessionId, null);
        assertStatus(trainingResp, 201);
        int trainingId = jsonInt(trainingResp.getBody(), "$.id");

        post("/api/trainings/" + trainingId + "/start", null);
        post("/api/trainings/" + trainingId + "/complete", null);

        // 4. Query All PRs
        ResponseEntity<String> allPrsResp = get("/api/analytics/personal-records");
        assertStatus(allPrsResp, 200);
        assertEquals(1, jsonArraySize(allPrsResp.getBody(), "$"));
        assertEquals("Barbell Bench Press", jsonString(allPrsResp.getBody(), "$[0].exerciseName"));
        assertEquals(120.0, jsonDouble(allPrsResp.getBody(), "$[0].maxWeight.value"));
        assertEquals(128.0, jsonDouble(allPrsResp.getBody(), "$[0].bestEstimated1Rm.estimated1Rm"));
        assertEquals(740.0, jsonDouble(allPrsResp.getBody(), "$[0].maxSessionVolume.volume"));
        assertEquals(5, jsonInt(allPrsResp.getBody(), "$[0].maxReps.repetitions"));

        // 5. Query Specific Exercise PR
        ResponseEntity<String> specificPrResp = get("/api/analytics/personal-records/exercise/" + exerciseId);
        assertStatus(specificPrResp, 200);
        assertEquals(exerciseId, jsonInt(specificPrResp.getBody(), "$.exerciseId"));
        assertEquals(120.0, jsonDouble(specificPrResp.getBody(), "$.maxWeight.value"));

        // 6. Query Missing Exercise PR -> 404
        ResponseEntity<String> missingPrResp = get("/api/analytics/personal-records/exercise/999");
        assertStatus(missingPrResp, 404);
    }

    @Test
    void acwr_e2eFlow() {
        // 1. Initial ACWR query before any training
        ResponseEntity<String> initialResp = get("/api/analytics/acwr");
        assertStatus(initialResp, 200);
        assertEquals(0.0, jsonDouble(initialResp.getBody(), "$.acuteWorkload"));
        assertEquals(0.0, jsonDouble(initialResp.getBody(), "$.chronicWorkload"));
        assertEquals(0.0, jsonDouble(initialResp.getBody(), "$.acwr"));
        assertEquals("UNDERTRAINING", jsonString(initialResp.getBody(), "$.riskZone"));
        assertEquals(28, jsonArraySize(initialResp.getBody(), "$.dailyWorkloads"));

        // 3. Create exercise, session, and completed training
        String exerciseJson = """
                {
                  "name": "Barbell Squat",
                  "primaryCategory": "RESISTANCE"
                }
                """;
        ResponseEntity<String> exResp = post("/api/exercises", exerciseJson);
        assertStatus(exResp, 201);
        int exerciseId = jsonInt(exResp.getBody(), "$.id");

        String sessionJson = """
                {
                  "name": "Leg Day",
                  "sessionStatus": "PLANNED",
                  "sessionExercises": [
                    {
                      "exerciseType": "resistance",
                      "orderIndex": 1,
                      "exercise": {
                        "id": %d,
                        "name": "Barbell Squat",
                        "primaryCategory": "RESISTANCE"
                      },
                      "sets": [
                        {
                          "setNumber": 1,
                          "setType": "WORKING",
                          "weight": {"value": 120.0, "unit": "KG"},
                          "repetitions": 5
                        }
                      ]
                    }
                  ]
                }
                """.formatted(exerciseId);
        ResponseEntity<String> sessionResp = post("/api/sessions", sessionJson);
        assertStatus(sessionResp, 201);
        int sessionId = jsonInt(sessionResp.getBody(), "$.id");

        ResponseEntity<String> trainingResp = post("/api/trainings/from-session/" + sessionId, null);
        assertStatus(trainingResp, 201);
        int trainingId = jsonInt(trainingResp.getBody(), "$.id");

        post("/api/trainings/" + trainingId + "/start", null);
        post("/api/trainings/" + trainingId + "/complete", null);

        // 4. Query ACWR after completed training
        ResponseEntity<String> updatedResp = get("/api/analytics/acwr");
        assertStatus(updatedResp, 200);
        assertTrue(jsonDouble(updatedResp.getBody(), "$.acuteWorkload") > 0.0);
        assertTrue(jsonDouble(updatedResp.getBody(), "$.chronicWorkload") > 0.0);
        assertEquals(1, jsonInt(updatedResp.getBody(), "$.dailyWorkloads[27].completedSessions"));
    }

    @Test
    void muscleVolume_e2eFlow() {
        // 1. Initial muscle volume before any workouts
        ResponseEntity<String> initialResp = get("/api/analytics/muscle-volume");
        assertStatus(initialResp, 200);
        assertEquals(0, jsonInt(initialResp.getBody(), "$.totalWorkingSets"));
        assertEquals(0.0, jsonDouble(initialResp.getBody(), "$.totalVolumeKg"));

        // 2. Create Exercise with Resistance Metrics
        String exerciseJson = """
                {
                  "name": "Barbell Bench Press",
                  "primaryCategory": "RESISTANCE",
                  "resistanceMetrics": {
                    "primaryMuscles": ["CHEST"],
                    "secondaryMuscles": ["TRICEPS"]
                  }
                }
                """;
        ResponseEntity<String> exResp = post("/api/exercises", exerciseJson);
        assertStatus(exResp, 201);
        int exerciseId = jsonInt(exResp.getBody(), "$.id");

        // 3. Create Session with 3 working sets
        String sessionJson = """
                {
                  "name": "Push Day",
                  "sessionStatus": "PLANNED",
                  "sessionExercises": [
                    {
                      "exerciseType": "resistance",
                      "orderIndex": 1,
                      "exercise": {
                        "id": %d,
                        "name": "Barbell Bench Press",
                        "primaryCategory": "RESISTANCE",
                        "resistanceMetrics": {
                          "primaryMuscles": ["CHEST"],
                          "secondaryMuscles": ["TRICEPS"]
                        }
                      },
                      "sets": [
                        {
                          "setNumber": 1,
                          "setType": "WORKING",
                          "weight": {"value": 100.0, "unit": "KG"},
                          "repetitions": 8
                        },
                        {
                          "setNumber": 2,
                          "setType": "WORKING",
                          "weight": {"value": 100.0, "unit": "KG"},
                          "repetitions": 8
                        },
                        {
                          "setNumber": 3,
                          "setType": "WORKING",
                          "weight": {"value": 100.0, "unit": "KG"},
                          "repetitions": 8
                        }
                      ]
                    }
                  ]
                }
                """.formatted(exerciseId);
        ResponseEntity<String> sessionResp = post("/api/sessions", sessionJson);
        assertStatus(sessionResp, 201);
        int sessionId = jsonInt(sessionResp.getBody(), "$.id");

        // 4. Create, Start, and Complete Training
        ResponseEntity<String> trainingResp = post("/api/trainings/from-session/" + sessionId, null);
        assertStatus(trainingResp, 201);
        int trainingId = jsonInt(trainingResp.getBody(), "$.id");

        post("/api/trainings/" + trainingId + "/start", null);
        post("/api/trainings/" + trainingId + "/complete", null);

        // 5. Query Muscle Volume
        ResponseEntity<String> volumeResp = get("/api/analytics/muscle-volume");
        assertStatus(volumeResp, 200);
        assertEquals(3, jsonInt(volumeResp.getBody(), "$.totalWorkingSets"));
        assertEquals(2400.0, jsonDouble(volumeResp.getBody(), "$.totalVolumeKg"));
        assertEquals(4.5, jsonDouble(volumeResp.getBody(), "$.categoryVolumes.UPPER_BODY"));
    }

    @Test
    void exerciseProgression_fullFlow_calculatesProgressionAndTrend() {
        // 1. Create Exercise
        String exerciseJson = """
                {
                  "name": "Overhead Press",
                  "primaryCategory": "RESISTANCE",
                  "resistanceMetrics": {
                    "primaryMuscles": ["SHOULDERS"],
                    "secondaryMuscles": ["TRICEPS"]
                  }
                }
                """;
        ResponseEntity<String> exResp = post("/api/exercises", exerciseJson);
        assertStatus(exResp, 201);
        int exerciseId = jsonInt(exResp.getBody(), "$.id");

        // 2. Create Session with sets
        String sessionJson = """
                {
                  "name": "Shoulder Day",
                  "sessionStatus": "PLANNED",
                  "sessionExercises": [
                    {
                      "exerciseType": "resistance",
                      "orderIndex": 1,
                      "exercise": {
                        "id": %d,
                        "name": "Overhead Press",
                        "primaryCategory": "RESISTANCE"
                      },
                      "sets": [
                        {
                          "setNumber": 1,
                          "setType": "WORKING",
                          "weight": {"value": 60.0, "unit": "KG"},
                          "repetitions": 5,
                          "rpe": {"value": 8.0}
                        }
                      ]
                    }
                  ]
                }
                """.formatted(exerciseId);
        ResponseEntity<String> sessionResp = post("/api/sessions", sessionJson);
        assertStatus(sessionResp, 201);
        int sessionId = jsonInt(sessionResp.getBody(), "$.id");

        // 3. Create, start, complete training
        ResponseEntity<String> trainingResp = post("/api/trainings/from-session/" + sessionId, null);
        assertStatus(trainingResp, 201);
        int trainingId = jsonInt(trainingResp.getBody(), "$.id");

        post("/api/trainings/" + trainingId + "/start", null);
        post("/api/trainings/" + trainingId + "/complete", null);

        // 4. Query Progression
        ResponseEntity<String> progResp = get("/api/analytics/progression/" + exerciseId);
        assertStatus(progResp, 200);
        assertEquals(exerciseId, jsonInt(progResp.getBody(), "$.exerciseId"));
        assertEquals("Overhead Press", jsonString(progResp.getBody(), "$.exerciseName"));
        assertEquals(1, jsonInt(progResp.getBody(), "$.totalSessions"));
        assertEquals(70.0, jsonDouble(progResp.getBody(), "$.initial1RmKg")); // 60 * (1 + 5/30) = 70.0
        assertEquals(70.0, jsonDouble(progResp.getBody(), "$.latest1RmKg"));
        assertEquals("INSUFFICIENT_DATA", jsonString(progResp.getBody(), "$.trend"));
        assertEquals(1, jsonInt(progResp.getBody(), "$.dataPoints.length()"));
        assertEquals(60.0, jsonDouble(progResp.getBody(), "$.dataPoints[0].topWeightKg"));
        assertEquals(70.0, jsonDouble(progResp.getBody(), "$.dataPoints[0].estimated1RmKg"));
    }
}
