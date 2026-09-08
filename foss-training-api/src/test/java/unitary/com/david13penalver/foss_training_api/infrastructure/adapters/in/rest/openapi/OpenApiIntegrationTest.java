package unitary.com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.openapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.david13penalver.foss_training_api.FossTrainingApiApplication;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(classes = FossTrainingApiApplication.class)
@AutoConfigureMockMvc
class OpenApiIntegrationTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    private Set<String> getExpectedControllerPaths() {
        Set<String> paths = new TreeSet<>();
        handlerMapping.getHandlerMethods().forEach((info, method) -> {
            if (method.getBeanType().getPackageName().startsWith("com.david13penalver.foss_training_api.infrastructure.adapters.in.rest")) {
                if (info.getPathPatternsCondition() != null) {
                    paths.addAll(info.getPathPatternsCondition().getPatternValues());
                }
            }
        });
        return paths;
    }

    private int getExpectedOperationCount() {
        int count = 0;
        for (var entry : handlerMapping.getHandlerMethods().entrySet()) {
            if (entry.getValue().getBeanType().getPackageName().startsWith("com.david13penalver.foss_training_api.infrastructure.adapters.in.rest")) {
                int methods = entry.getKey().getMethodsCondition().getMethods().size();
                count += (methods > 0 ? methods : 1);
            }
        }
        return count;
    }

    @Test
    void openApiJson_isGeneratedFromRunningApplication() throws Exception {
        String body = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // Auto-export openapi.json to target/ for documentation synchronization
        Path targetDir = Path.of("target");
        if (Files.exists(targetDir)) {
            Files.writeString(targetDir.resolve("openapi.json"), body);
        }

        Map<String, Object> doc = MAPPER.readValue(body, new TypeReference<>() {});

        assertEquals("3.1.0", doc.get("openapi"));

        Map<String, Object> info = cast(doc.get("info"));
        assertEquals("FOSS Training API", info.get("title"));
        assertEquals("0.0.1-SNAPSHOT", info.get("version"));

        Map<String, Object> paths = cast(doc.get("paths"));
        Set<String> expectedPaths = getExpectedControllerPaths();
        assertEquals(expectedPaths, paths.keySet(),
                "OpenAPI paths must automatically match all registered REST controllers.");

        int operations = paths.values().stream().mapToInt(v -> cast(v).size()).sum();
        int expectedOperations = getExpectedOperationCount();
        assertEquals(expectedOperations, operations,
                "OpenAPI operation count must automatically match registered controller methods.");
    }

    @Test
    void openApiJson_rendersSessionExercisePolymorphism() throws Exception {
        String body = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String, Object> doc = MAPPER.readValue(body, new TypeReference<>() {});
        Map<String, Object> schemas = cast(cast(doc.get("components")).get("schemas"));

        Map<String, Object> sessionExercise = cast(schemas.get("SessionExercise"));
        Map<String, Object> discriminator = cast(sessionExercise.get("discriminator"));
        assertEquals("exerciseType", discriminator.get("propertyName"));

        Map<String, Object> sessionProperties = cast(cast(schemas.get("Session")).get("properties"));
        List<?> oneOf = (List<?>) ((Map<?, ?>) ((Map<?, ?>) sessionProperties.get("sessionExercises"))
                .get("items")).get("oneOf");
        assertEquals(3, oneOf.size(), "Session.sessionExercises should reference the three subtypes via oneOf");

        assertTrue(schemas.containsKey("ResistanceSessionExercise"));
        assertTrue(schemas.containsKey("EnduranceSessionExercise"));
        assertTrue(schemas.containsKey("MobilitySessionExercise"));

        Map<String, Object> resistance = cast(schemas.get("ResistanceSessionExercise"));
        List<?> allOf = (List<?>) resistance.get("allOf");
        assertEquals("#/components/schemas/SessionExercise", ((Map<?, ?>) allOf.getFirst()).get("$ref"),
                "ResistanceSessionExercise should inherit from SessionExercise via allOf");
        assertTrue(refersTo((Map<?, ?>) allOf.get(1), "ResistanceSet"),
                "ResistanceSessionExercise should carry its own sets property");
    }

    private static boolean refersTo(Map<?, ?> node, String schemaName) {
        Object ref = node.get("$ref");
        if (ref != null) {
            return ref.equals("#/components/schemas/" + schemaName);
        }
        Object items = node.get("items");
        if (items instanceof Map<?, ?> itemsMap && refersTo(itemsMap, schemaName)) {
            return true;
        }
        Object properties = node.get("properties");
        if (properties instanceof Map<?, ?> map) {
            for (Object value : map.values()) {
                if (value instanceof Map<?, ?> nested && refersTo(nested, schemaName)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Test
    void openApiYaml_isServed() throws Exception {
        MvcResult result = mockMvc.perform(get("/v3/api-docs.yaml"))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        assertTrue(body.contains("openapi: 3.1.0"));
        assertTrue(body.contains("title: FOSS Training API"));

        Path targetDir = Path.of("target");
        if (Files.exists(targetDir)) {
            Files.writeString(targetDir.resolve("openapi.yaml"), body);
        }
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