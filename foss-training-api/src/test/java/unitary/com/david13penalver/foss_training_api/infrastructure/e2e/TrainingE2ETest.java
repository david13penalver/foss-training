package unitary.com.david13penalver.foss_training_api.infrastructure.e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

class TrainingE2ETest extends E2EIntegrationTestBase {

    private static final String BASE = "/api/trainings";

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
    void createTraining_persistsAndReturns201WithLocation() {
        ResponseEntity<String> response = post(BASE, validTrainingJson("Heavy Leg Day", "2026-09-08"));

        assertStatus(response, 201);
        int id = jsonInt(response.getBody(), "$.id");
        assertTrue(id > 0);
        assertEquals("/api/trainings/" + id, response.getHeaders().getLocation().getPath());
        assertEquals("Heavy Leg Day", jsonString(response.getBody(), "$.name"));
        assertEquals("2026-09-08", jsonString(response.getBody(), "$.trainingDate"));
        assertEquals("Push Routine", jsonString(response.getBody(), "$.session.name"));
    }

    @Test
    void getAllTrainings_returnsCreatedTrainings() {
        post(BASE, validTrainingJson("Workout 1", "2026-09-08"));
        post(BASE, validTrainingJson("Workout 2", "2026-09-09"));

        ResponseEntity<String> response = get(BASE);

        assertStatus(response, 200);
        assertEquals(2, jsonArraySize(response.getBody(), "$"));
    }

    @Test
    void getTrainingById_returnsSavedTraining() {
        String created = post(BASE, validTrainingJson("Chest Day", "2026-09-08")).getBody();
        int id = jsonInt(created, "$.id");

        ResponseEntity<String> response = get(BASE + "/" + id);

        assertStatus(response, 200);
        assertEquals("Chest Day", jsonString(response.getBody(), "$.name"));
    }

    @Test
    void getTrainingById_returns404_whenMissing() {
        ResponseEntity<String> response = get(BASE + "/999");

        assertStatus(response, 404);
    }

    @Test
    void updateTraining_replacesById() {
        int id = jsonInt(post(BASE, validTrainingJson("Chest Day", "2026-09-08")).getBody(), "$.id");
        String update = validTrainingJson("Chest Day - Volume", "2026-09-08");

        ResponseEntity<String> response = put(BASE + "/" + id, update);

        assertStatus(response, 200);
        assertEquals("Chest Day - Volume", jsonString(response.getBody(), "$.name"));

        ResponseEntity<String> fetched = get(BASE + "/" + id);
        assertEquals("Chest Day - Volume", jsonString(fetched.getBody(), "$.name"));
    }

    @Test
    void updateTraining_withUnknownId_returns404() {
        ResponseEntity<String> response = put(BASE + "/999", validTrainingJson("Unknown", "2026-09-08"));

        assertStatus(response, 404);
    }

    @Test
    void deleteTraining_removesAndReturns404OnSecondFetch() {
        int id = jsonInt(post(BASE, validTrainingJson("HIIT Day", "2026-09-08")).getBody(), "$.id");

        ResponseEntity<String> deleteResponse = delete(BASE + "/" + id);

        assertStatus(deleteResponse, 204);
        assertStatus(get(BASE + "/" + id), 404);
    }

    @Test
    void deleteTraining_returns404_whenMissing() {
        assertStatus(delete(BASE + "/777"), 404);
    }

    @Test
    void existsEndpoint_reportsTrueThenFalse() {
        int id = jsonInt(post(BASE, validTrainingJson("Cardio Day", "2026-09-08")).getBody(), "$.id");

        assertTrue(jsonBool(get(BASE + "/" + id + "/exists").getBody(), "$"));

        delete(BASE + "/" + id);

        assertFalse(jsonBool(get(BASE + "/" + id + "/exists").getBody(), "$"));
        assertFalse(jsonBool(get(BASE + "/888/exists").getBody(), "$"));
    }

    @Test
    void createTraining_withBlankName_returns400() {
        String body = """
                {
                  "name": "   ",
                  "trainingDate": "2026-09-08",
                  "session": {
                    "name": "Push Routine"
                  }
                }
                """;

        ResponseEntity<String> response = post(BASE, body);

        assertStatus(response, 400);
    }

    @Test
    void createTraining_withNullDate_returns400() {
        String body = """
                {
                  "name": "Training",
                  "session": {
                    "name": "Push Routine"
                  }
                }
                """;

        ResponseEntity<String> response = post(BASE, body);

        assertStatus(response, 400);
    }

    @Test
    void createTraining_withNullSession_returns400() {
        String body = """
                {
                  "name": "Training",
                  "trainingDate": "2026-09-08"
                }
                """;

        ResponseEntity<String> response = post(BASE, body);

        assertStatus(response, 400);
    }

    @Test
    void createTraining_withInvalidEnum_returns400() {
        String body = """
                {
                  "name": "Training",
                  "trainingDate": "2026-09-08",
                  "status": "NON_EXISTENT_STATUS",
                  "session": {
                    "name": "Push Routine"
                  }
                }
                """;

        ResponseEntity<String> response = post(BASE, body);

        assertStatus(response, 400);
    }

    @Test
    void trainingLifecycle_e2eFlow() {
        String sessionPayload = """
                {
                  "name": "Full Body Template",
                  "description": "Base template for strength",
                  "sessionStatus": "PLANNED",
                  "sessionExercises": []
                }
                """;
        ResponseEntity<String> sessionResp = post("/api/sessions", sessionPayload);
        assertStatus(sessionResp, 201);
        int sessionId = jsonInt(sessionResp.getBody(), "$.id");

        ResponseEntity<String> clonerResp = post(BASE + "/from-session/" + sessionId + "?date=2026-09-08&customName=Morning+Strength", null);
        assertStatus(clonerResp, 201);
        int trainingId = jsonInt(clonerResp.getBody(), "$.id");
        assertEquals("Morning Strength", jsonString(clonerResp.getBody(), "$.name"));
        assertEquals("PLANNED", jsonString(clonerResp.getBody(), "$.status"));

        ResponseEntity<String> startResp = post(BASE + "/" + trainingId + "/start", null);
        assertStatus(startResp, 200);
        assertEquals("IN_PROGRESS", jsonString(startResp.getBody(), "$.status"));

        // Starting again throws IllegalStateException -> 409 Conflict
        ResponseEntity<String> startAgainResp = post(BASE + "/" + trainingId + "/start", null);
        assertStatus(startAgainResp, 409);

        ResponseEntity<String> completeResp = post(BASE + "/" + trainingId + "/complete", null);
        assertStatus(completeResp, 200);
        assertEquals("COMPLETED", jsonString(completeResp.getBody(), "$.status"));

        // Cancelling completed throws IllegalStateException -> 409 Conflict
        ResponseEntity<String> cancelResp = post(BASE + "/" + trainingId + "/cancel", null);
        assertStatus(cancelResp, 409);
    }

    @Test
    void cancelTraining_e2eFlow() {
        String sessionPayload = """
                {
                  "name": "HIIT Template",
                  "sessionStatus": "PLANNED",
                  "sessionExercises": []
                }
                """;
        ResponseEntity<String> sessionResp = post("/api/sessions", sessionPayload);
        int sessionId = jsonInt(sessionResp.getBody(), "$.id");

        ResponseEntity<String> clonerResp = post(BASE + "/from-session/" + sessionId, null);
        assertStatus(clonerResp, 201);
        int trainingId = jsonInt(clonerResp.getBody(), "$.id");

        ResponseEntity<String> cancelResp = post(BASE + "/" + trainingId + "/cancel", null);
        assertStatus(cancelResp, 200);
        assertEquals("CANCELLED", jsonString(cancelResp.getBody(), "$.status"));

        // Starting cancelled throws IllegalStateException -> 409 Conflict
        ResponseEntity<String> startResp = post(BASE + "/" + trainingId + "/start", null);
        assertStatus(startResp, 409);
    }

    @Test
    void lifecycleEndpoints_return404_whenNotFound() {
        assertStatus(post(BASE + "/999/start", null), 404);
        assertStatus(post(BASE + "/999/complete", null), 404);
        assertStatus(post(BASE + "/999/cancel", null), 404);
        assertStatus(post(BASE + "/from-session/999", null), 404);
    }
}
