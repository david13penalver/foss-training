package unitary.com.david13penalver.foss_training_api.infrastructure.e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

class SessionE2ETest extends E2EIntegrationTestBase {

    private static final String BASE = "/api/sessions";

    @Test
    void createSession_withAllExerciseTypes_roundTrips() {
        ResponseEntity<String> response = post(BASE, fixture("session/full-session.json"));

        assertStatus(response, 201);
        int id = jsonInt(response.getBody(), "$.id");
        assertTrue(id > 0);
        assertEquals("/api/sessions/" + id, response.getHeaders().getLocation().getPath());
        assertEquals("Push Day", jsonString(response.getBody(), "$.name"));
        assertEquals(3, jsonArraySize(response.getBody(), "$.sessionExercises"));
        assertEquals("resistance", jsonString(response.getBody(), "$.sessionExercises[0].exerciseType"));
        assertEquals("endurance", jsonString(response.getBody(), "$.sessionExercises[1].exerciseType"));
        assertEquals("mobility", jsonString(response.getBody(), "$.sessionExercises[2].exerciseType"));
        assertEquals(2, jsonArraySize(response.getBody(), "$.sessionExercises[0].sets"));
        assertEquals(100.0, jsonDouble(response.getBody(), "$.sessionExercises[0].sets[1].weight.value"));
        assertEquals(5, jsonInt(response.getBody(), "$.sessionExercises[0].sets[1].repetitions"));
        assertEquals(8.0, jsonDouble(response.getBody(), "$.sessionExercises[0].sets[1].rpe.value"));
        assertEquals(90, jsonInt(response.getBody(), "$.sessionExercises[1].intervals[0].duration.totalSeconds"));
        assertEquals(30, jsonInt(response.getBody(), "$.sessionExercises[2].sets[0].holdDuration.totalSeconds"));
    }

    @Test
    void getAllSessions_returnsCreatedSessions() {
        post(BASE, fixture("session/full-session.json"));
        post(BASE, fixture("session/resistance-only-session.json"));

        ResponseEntity<String> response = get(BASE);

        assertStatus(response, 200);
        assertEquals(2, jsonArraySize(response.getBody(), "$"));
    }

    @Test
    void getSessionById_returnsSavedSession() {
        String created = post(BASE, fixture("session/resistance-only-session.json")).getBody();
        int id = jsonInt(created, "$.id");

        ResponseEntity<String> response = get(BASE + "/" + id);

        assertStatus(response, 200);
        assertEquals("Strength Session", jsonString(response.getBody(), "$.name"));
        assertEquals("resistance", jsonString(response.getBody(), "$.sessionExercises[0].exerciseType"));
    }

    @Test
    void getSessionById_returns404_whenMissing() {
        assertStatus(get(BASE + "/555"), 404);
    }

    @Test
    void updateSession_replacesById() {
        int id = jsonInt(post(BASE, fixture("session/resistance-only-session.json")).getBody(), "$.id");
        String update = fixture("session/full-session.json");

        ResponseEntity<String> response = put(BASE + "/" + id, update);

        assertStatus(response, 200);
        assertEquals("Push Day", jsonString(response.getBody(), "$.name"));
        assertEquals(3, jsonArraySize(response.getBody(), "$.sessionExercises"));

        ResponseEntity<String> fetched = get(BASE + "/" + id);
        assertEquals("Push Day", jsonString(fetched.getBody(), "$.name"));
    }

    @Test
    void updateSession_withUnknownId_returns404() {
        ResponseEntity<String> response = put(BASE + "/999",
                fixture("session/full-session.json"));

        assertStatus(response, 404);
    }

    @Test
    void deleteSession_removesAndReturns404OnSecondFetch() {
        int id = jsonInt(post(BASE, fixture("session/full-session.json")).getBody(), "$.id");

        ResponseEntity<String> deleteResponse = delete(BASE + "/" + id);

        assertStatus(deleteResponse, 204);
        assertStatus(get(BASE + "/" + id), 404);
    }

    @Test
    void deleteSession_returns404_whenMissing() {
        assertStatus(delete(BASE + "/777"), 404);
    }

    @Test
    void sessionExists_reportsTrueThenFalse() {
        int id = jsonInt(post(BASE, fixture("session/resistance-only-session.json")).getBody(), "$.id");

        assertTrue(jsonBool(get(BASE + "/" + id + "/exists").getBody(), "$"));

        delete(BASE + "/" + id);

        assertFalse(jsonBool(get(BASE + "/" + id + "/exists").getBody(), "$"));
    }

    @Test
    void createSession_withInvalidStatus_returns400() {
        ResponseEntity<String> response = post(BASE, fixture("invalid/bad-status-session.json"));

        assertStatus(response, 400);
    }

    @Test
    void createSession_withInvalidWeightObject_returns400() {
        ResponseEntity<String> response = post(BASE, fixture("invalid/bad-weight-format-session.json"));

        assertStatus(response, 400);
    }

    @Test
    void createSession_withEmptyBody_returns400() {
        ResponseEntity<String> response = post(BASE, "");

        assertStatus(response, 400);
    }

    @Test
    void createSession_preservesPolymorphicSetsOnReadBack() {
        String created = post(BASE, fixture("session/full-session.json")).getBody();
        int id = jsonInt(created, "$.id");

        ResponseEntity<String> fetched = get(BASE + "/" + id);

        assertStatus(fetched, 200);
        assertEquals("resistance", jsonString(fetched.getBody(), "$.sessionExercises[0].exerciseType"));
        assertEquals("ENDURANCE",
                jsonString(fetched.getBody(), "$.sessionExercises[1].exercise.primaryCategory"));
        assertEquals(60.0, jsonDouble(fetched.getBody(), "$.sessionExercises[0].sets[0].weight.value"));
        assertEquals(6.0, jsonDouble(fetched.getBody(), "$.sessionExercises[0].sets[0].rpe.value"));
    }
}