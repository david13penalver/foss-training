package unitary.com.david13penalver.foss_training_api.infrastructure.e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.david13penalver.foss_training_api.FossTrainingApiApplication;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.exercise.InMemoryExerciseDao;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.session.InMemorySessionDao;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = FossTrainingApiApplication.class)
@AutoConfigureTestRestTemplate
public abstract class E2EIntegrationTestBase {

    private static final HttpHeaders JSON_HEADERS = new HttpHeaders();

    static {
        JSON_HEADERS.setContentType(MediaType.APPLICATION_JSON);
    }

    @Autowired
    protected TestRestTemplate restTemplate;

    @LocalServerPort
    protected int port;

    @Autowired
    private InMemoryExerciseDao exerciseDao;

    @Autowired
    private InMemorySessionDao sessionDao;

    @BeforeEach
    void resetDatabases() {
        exerciseDao.clear();
        sessionDao.clear();
    }

    protected static String fixture(String resourcePath) {
        try {
            return new String(Files.readAllBytes(
                    new ClassPathResource("e2e/" + resourcePath).getFile().toPath()),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not load fixture e2e/" + resourcePath, e);
        }
    }

    protected ResponseEntity<String> get(String url) {
        return restTemplate.getForEntity(baseUrl(url), String.class);
    }

    protected ResponseEntity<String> post(String url, String jsonBody) {
        return restTemplate.exchange(baseUrl(url), HttpMethod.POST,
                new HttpEntity<>(jsonBody, JSON_HEADERS), String.class);
    }

    protected ResponseEntity<String> put(String url, String jsonBody) {
        return restTemplate.exchange(baseUrl(url), HttpMethod.PUT,
                new HttpEntity<>(jsonBody, JSON_HEADERS), String.class);
    }

    protected ResponseEntity<String> delete(String url) {
        return restTemplate.exchange(baseUrl(url), HttpMethod.DELETE,
                new HttpEntity<>(JSON_HEADERS), String.class);
    }

    protected static String jsonString(String body, String jsonPath) {
        return JsonPath.read(body, jsonPath);
    }

    protected static int jsonInt(String body, String jsonPath) {
        return JsonPath.read(body, jsonPath);
    }

    protected static double jsonDouble(String body, String jsonPath) {
        Number n = JsonPath.read(body, jsonPath);
        return n.doubleValue();
    }

    protected static boolean jsonBool(String body, String jsonPath) {
        return JsonPath.read(body, jsonPath);
    }

    protected static int jsonArraySize(String body, String jsonPath) {
        return ((List<?>) JsonPath.read(body, jsonPath)).size();
    }

    protected static void assertStatus(ResponseEntity<String> response, int expected) {
        assertEquals(expected, response.getStatusCode().value(),
                () -> "Expected status " + expected + " but got "
                        + response.getStatusCode().value() + ". Body: " + response.getBody());
    }

    protected static void assertBodyContains(ResponseEntity<String> response, String fragment) {
        assertTrue(response.getBody() != null && response.getBody().contains(fragment),
                () -> "Expected body to contain '" + fragment + "' but was: " + response.getBody());
    }

    private String baseUrl(String path) {
        return "http://localhost:" + port + path;
    }
}