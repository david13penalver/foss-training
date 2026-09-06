package unitary.com.david13penalver.foss_training_api.infrastructure.e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

class ExerciseE2ETest extends E2EIntegrationTestBase {

    private static final String BASE = "/api/exercises";

    @Test
    void createExercise_persistsAndEchoesBody() {
        ResponseEntity<String> response = post(BASE, fixture("exercise/resistance-exercise.json"));

        assertStatus(response, 201);
        int id = jsonInt(response.getBody(), "$.id");
        assertTrue(id > 0);
        assertEquals("/api/exercises/" + id, response.getHeaders().getLocation().getPath());
        assertEquals("Barbell Squat", jsonString(response.getBody(), "$.name"));
        assertEquals("RESISTANCE", jsonString(response.getBody(), "$.primaryCategory"));
    }

    @Test
    void getAllExercises_returnsCreatedExercises() {
        post(BASE, fixture("exercise/resistance-exercise.json"));
        post(BASE, fixture("exercise/endurance-exercise.json"));

        ResponseEntity<String> response = get(BASE);

        assertStatus(response, 200);
        assertEquals(2, jsonArraySize(response.getBody(), "$"));
    }

    @Test
    void getExerciseById_returnsSavedExercise() {
        String created = post(BASE, fixture("exercise/resistance-exercise.json")).getBody();
        int id = jsonInt(created, "$.id");

        ResponseEntity<String> response = get(BASE + "/" + id);

        assertStatus(response, 200);
        assertEquals("Barbell Squat", jsonString(response.getBody(), "$.name"));
    }

    @Test
    void getExerciseById_returns404_whenMissing() {
        ResponseEntity<String> response = get(BASE + "/999");

        assertStatus(response, 404);
    }

    @Test
    void updateExercise_replacesById() {
        int id = jsonInt(post(BASE, fixture("exercise/resistance-exercise.json")).getBody(), "$.id");
        String update = """
                {"name":"Barbell Squat - Heavy","description":"Updated","primaryCategory":"RESISTANCE"}
                """;

        ResponseEntity<String> response = put(BASE + "/" + id, update);

        assertStatus(response, 200);
        assertEquals("Barbell Squat - Heavy", jsonString(response.getBody(), "$.name"));

        ResponseEntity<String> fetched = get(BASE + "/" + id);
        assertEquals("Barbell Squat - Heavy", jsonString(fetched.getBody(), "$.name"));
    }

    @Test
    void updateExercise_withUnknownId_returns404() {
        ResponseEntity<String> response = put(BASE + "/999",
                fixture("exercise/resistance-exercise.json"));

        assertStatus(response, 404);
    }

    @Test
    void deleteExercise_removesAndReturns404OnSecondFetch() {
        int id = jsonInt(post(BASE, fixture("exercise/resistance-exercise.json")).getBody(), "$.id");

        ResponseEntity<String> deleteResponse = delete(BASE + "/" + id);

        assertStatus(deleteResponse, 204);
        assertStatus(get(BASE + "/" + id), 404);
    }

    @Test
    void deleteExercise_returns404_whenMissing() {
        assertStatus(delete(BASE + "/777"), 404);
    }

    @Test
    void existsEndpoint_reportsTrueThenFalse() {
        int id = jsonInt(post(BASE, fixture("exercise/endurance-exercise.json")).getBody(), "$.id");

        assertTrue(jsonBool(get(BASE + "/" + id + "/exists").getBody(), "$"));

        delete(BASE + "/" + id);

        assertFalse(jsonBool(get(BASE + "/" + id + "/exists").getBody(), "$"));
        assertFalse(jsonBool(get(BASE + "/888/exists").getBody(), "$"));
    }

    @Test
    void createExercise_withBlankName_returns400() {
        ResponseEntity<String> response = post(BASE, fixture("invalid/blank-name-exercise.json"));

        assertStatus(response, 400);
    }

    @Test
    void createExercise_withoutPrimaryCategory_returns400() {
        ResponseEntity<String> response = post(BASE, fixture("invalid/null-category-exercise.json"));

        assertStatus(response, 400);
    }

    @Test
    void createExercise_withMalformedJson_returns400() {
        ResponseEntity<String> response = post(BASE, fixture("invalid/malformed.json"));

        assertStatus(response, 400);
    }

    @Test
    void createExercise_withEmptyBody_returns400() {
        ResponseEntity<String> response = post(BASE, "");

        assertStatus(response, 400);
    }

    @Test
    void createExercise_withInvalidEnum_returns400() {
        String body = "{\"name\":\"X\",\"primaryCategory\":\"NOT_A_CATEGORY\"}";

        ResponseEntity<String> response = post(BASE, body);

        assertStatus(response, 400);
    }

    @Test
    void stressCreate_readsBackAll() {
        for (int i = 0; i < 20; i++) {
            String body = String.format("{\"name\":\"Exercise %d\",\"primaryCategory\":\"RESISTANCE\"}", i);
            assertStatus(post(BASE, body), 201);
        }

        ResponseEntity<String> response = get(BASE);

        assertStatus(response, 200);
        assertEquals(20, jsonArraySize(response.getBody(), "$"));
    }
}