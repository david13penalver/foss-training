package unitary.com.david13penalver.foss_training_api.infrastructure.e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

class CrossEntityWorkflowE2ETest extends E2EIntegrationTestBase {

    @Test
    void fullJourney_createExercisesThenSessionReferenceAndUpdateThenDelete() {
        int squatId = jsonInt(post("/api/exercises", fixture("exercise/resistance-exercise.json")).getBody(), "$.id");
        int runId = jsonInt(post("/api/exercises", fixture("exercise/endurance-exercise.json")).getBody(), "$.id");
        int stretchId = jsonInt(post("/api/exercises", fixture("exercise/mobility-exercise.json")).getBody(), "$.id");

        String sessionBody = """
                {
                  "name": "Full Journey Session",
                  "sessionStatus": "PLANNED",
                  "sessionExercises": [
                    {"exerciseType":"resistance","orderIndex":1,"exercise":{"id":%d},"sets":[
                      {"setNumber":1,"setType":"WORKING","weight":{"value":100.0,"unit":"KG"},"repetitions":5,"rpe":{"value":8.0}}
                    ]},
                    {"exerciseType":"endurance","orderIndex":2,"exercise":{"id":%d},"intervals":[
                      {"intervalNumber":1,"distance":{"value":400.0,"unit":"METERS"},"duration":{"totalSeconds":90}}
                    ]},
                    {"exerciseType":"mobility","orderIndex":3,"exercise":{"id":%d},"sets":[
                      {"setNumber":1,"holdDuration":{"totalSeconds":30},"repetitions":2,"bilateral":true}
                    ]}
                  ]
                }
                """.formatted(squatId, runId, stretchId);

        ResponseEntity<String> created = post("/api/sessions", sessionBody);
        assertStatus(created, 200);
        int sessionId = jsonInt(created.getBody(), "$.id");

        ResponseEntity<String> fetched = get("/api/sessions/" + sessionId);
        assertStatus(fetched, 200);
        assertEquals(squatId, jsonInt(fetched.getBody(), "$.sessionExercises[0].exercise.id"));
        assertEquals(runId, jsonInt(fetched.getBody(), "$.sessionExercises[1].exercise.id"));
        assertEquals(stretchId, jsonInt(fetched.getBody(), "$.sessionExercises[2].exercise.id"));
        assertEquals(100.0, jsonDouble(fetched.getBody(), "$.sessionExercises[0].sets[0].weight.value"));

        String updatedTarget = """
                {"exerciseType":"resistance","orderIndex":1,"exercise":{"id":%d},"sets":[
                  {"setNumber":1,"setType":"WORKING","weight":{"value":120.0,"unit":"KG"},"repetitions":3,"rpe":{"value":9.0}}
                ]}
                """.formatted(squatId);
        String updateBody = """
                {
                  "name": "Full Journey Session - Updated",
                  "sessionStatus": "DRAFT",
                  "sessionExercises": [%s]
                }
                """.formatted(updatedTarget);

        ResponseEntity<String> updated = put("/api/sessions/" + sessionId, updateBody);
        assertStatus(updated, 200);
        assertEquals("Full Journey Session - Updated", jsonString(updated.getBody(), "$.name"));
        assertEquals(120.0, jsonDouble(updated.getBody(), "$.sessionExercises[0].sets[0].weight.value"));
        assertEquals(3, jsonInt(updated.getBody(), "$.sessionExercises[0].sets[0].repetitions"));

        assertStatus(delete("/api/sessions/" + sessionId), 204);
        assertStatus(get("/api/sessions/" + sessionId), 404);

        assertStatus(get("/api/exercises/" + squatId), 200);
        assertStatus(get("/api/exercises/" + runId), 200);
        assertStatus(get("/api/exercises/" + stretchId), 200);
        assertTrue(jsonInt(get("/api/exercises/" + squatId).getBody(), "$.id") == squatId);
    }
}