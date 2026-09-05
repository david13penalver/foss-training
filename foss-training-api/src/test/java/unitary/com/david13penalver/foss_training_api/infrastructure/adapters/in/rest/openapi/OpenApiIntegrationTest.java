package unitary.com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.openapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.david13penalver.foss_training_api.FossTrainingApiApplication;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(classes = FossTrainingApiApplication.class)
@AutoConfigureMockMvc
class OpenApiIntegrationTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Set<String> EXPECTED_PATHS = Set.of(
            "/api/exercises", "/api/exercises/{id}", "/api/exercises/{id}/exists",
            "/api/sessions", "/api/sessions/{id}", "/api/sessions/{id}/exists",
            "/api/endurance-types", "/api/endurance-types/{name}",
            "/api/equipment", "/api/equipment/{name}",
            "/api/joints", "/api/joints/{name}",
            "/api/mobility-types", "/api/mobility-types/{name}",
            "/api/movement-patterns", "/api/movement-patterns/{name}",
            "/api/muscle-groups", "/api/muscle-groups/{name}",
            "/api/stretch-types", "/api/stretch-types/{name}");

    private static final int EXPECTED_OPERATION_COUNT = 26;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void openApiJson_isGeneratedFromRunningApplication() throws Exception {
        String body = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String, Object> doc = MAPPER.readValue(body, new TypeReference<>() {});

        assertEquals("3.1.0", doc.get("openapi"));

        Map<String, Object> info = cast(doc.get("info"));
        assertEquals("FOSS Training API", info.get("title"));
        assertEquals("0.0.1-SNAPSHOT", info.get("version"));

        Map<String, Object> paths = cast(doc.get("paths"));
        assertEquals(EXPECTED_PATHS, paths.keySet(),
                "OpenAPI paths drifted from the real controllers; "
                        + "add/remove endpoints or update EXPECTED_PATHS deliberately.");

        int operations = paths.values().stream().mapToInt(v -> cast(v).size()).sum();
        assertEquals(EXPECTED_OPERATION_COUNT, operations,
                "Operations drifted from the real controllers.");
    }

    @Test
    void openApiJson_rendersSessionExercisePolymorphism() throws Exception {
        String body = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String, Object> doc = MAPPER.readValue(body, new TypeReference<>() {});
        Map<String, Object> schemas = cast(cast(doc.get("components")).get("schemas"));

        Map<String, Object> sessionPart = cast(schemas.get("SessionPart"));
        assertTrue(sessionPart.containsKey("properties"), "SessionPart should expose its properties");

        Map<String, Object> sessionExercise = cast(schemas.get("SessionExercise"));
        Map<String, Object> discriminator = cast(sessionExercise.get("discriminator"));
        assertEquals("exerciseType", discriminator.get("propertyName"));
        List<?> oneOf = (List<?>) sessionExercise.get("oneOf");
        assertEquals(3, oneOf.size());

        assertTrue(schemas.containsKey("ResistanceSessionExercise"));
        assertTrue(schemas.containsKey("EnduranceSessionExercise"));
        assertTrue(schemas.containsKey("MobilitySessionExercise"));
        assertTrue(schemas.containsKey("Exercise"));
        assertTrue(schemas.containsKey("Session"));
    }

    @Test
    void openApiYaml_isServed() throws Exception {
        MvcResult result = mockMvc.perform(get("/v3/api-docs.yaml"))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        assertTrue(body.contains("openapi: 3.1.0"));
        assertTrue(body.contains("title: FOSS Training API"));
    }

    @Test
    void swaggerUi_isServed() throws Exception {
        MvcResult html = mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk())
                .andReturn();
        assertTrue(html.getResponse().getContentAsString().contains("Swagger UI"));

        MvcResult legacy = mockMvc.perform(get("/swagger-ui.html")).andReturn();
        int legacyStatus = legacy.getResponse().getStatus();
        assertTrue(legacyStatus == 200 || legacyStatus == 302,
                "Expected swagger-ui.html to redirect or serve the UI but got " + legacyStatus);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> cast(Object value) {
        return (Map<String, Object>) value;
    }
}