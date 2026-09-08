package unitary.com.david13penalver.foss_training_api.infrastructure.e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

class TrainingProgramE2ETest extends E2EIntegrationTestBase {

    @Test
    void trainingProgram_andScheduleGeneration_e2eFlow() {
        // 1. Create a Session template
        String sessionJson = """
                {
                  "name": "Heavy Push",
                  "sessionStatus": "PLANNED",
                  "sessionExercises": []
                }
                """;
        ResponseEntity<String> sessionResp = post("/api/sessions", sessionJson);
        assertStatus(sessionResp, 201);
        int sessionId = jsonInt(sessionResp.getBody(), "$.id");

        // 2. Create Training Program
        String programJson = """
                {
                  "name": "Powerlifting Block",
                  "description": "Peaking block",
                  "durationWeeks": 3,
                  "periodizationType": "BLOCK",
                  "level": "ADVANCED",
                  "workouts": [
                    {
                      "dayOfWeek": 1,
                      "focus": "Bench & Accessories",
                      "session": {
                        "id": %d,
                        "name": "Heavy Push",
                        "sessionStatus": "PLANNED",
                        "sessionExercises": []
                      }
                    }
                  ]
                }
                """.formatted(sessionId);

        ResponseEntity<String> programResp = post("/api/programs", programJson);
        assertStatus(programResp, 201);
        int programId = jsonInt(programResp.getBody(), "$.id");
        assertTrue(programId > 0);
        assertEquals("Powerlifting Block", jsonString(programResp.getBody(), "$.name"));

        // 3. Generate Schedule
        ResponseEntity<String> scheduleResp = post("/api/programs/" + programId + "/generate-schedule?startDate=2026-10-05", null);
        assertStatus(scheduleResp, 201);
        assertEquals(3, jsonArraySize(scheduleResp.getBody(), "$"));
        assertEquals("Powerlifting Block - W1D1: Heavy Push", jsonString(scheduleResp.getBody(), "$[0].name"));
        assertEquals("2026-10-05", jsonString(scheduleResp.getBody(), "$[0].trainingDate"));
        assertEquals("2026-10-12", jsonString(scheduleResp.getBody(), "$[1].trainingDate"));
        assertEquals("2026-10-19", jsonString(scheduleResp.getBody(), "$[2].trainingDate"));

        // 4. Verify Generated Trainings Exist in Training API
        ResponseEntity<String> trainingsResp = get("/api/trainings");
        assertStatus(trainingsResp, 200);
        assertEquals(3, jsonArraySize(trainingsResp.getBody(), "$"));

        // 5. Update Program
        String updateJson = """
                {
                  "name": "Powerlifting Block V2",
                  "durationWeeks": 4,
                  "periodizationType": "BLOCK",
                  "level": "ADVANCED",
                  "workouts": []
                }
                """;
        ResponseEntity<String> updateResp = put("/api/programs/" + programId, updateJson);
        assertStatus(updateResp, 200);
        assertEquals("Powerlifting Block V2", jsonString(updateResp.getBody(), "$.name"));

        // 6. Delete Program
        ResponseEntity<String> deleteResp = delete("/api/programs/" + programId);
        assertStatus(deleteResp, 204);
        assertStatus(get("/api/programs/" + programId), 404);
    }
}
