package unitary.com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.session;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.david13penalver.foss_training_api.FossTrainingApiApplication;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.session.InMemorySessionDao;

@SpringBootTest(classes = FossTrainingApiApplication.class)
@AutoConfigureMockMvc
class SessionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InMemorySessionDao sessionDao;

    @BeforeEach
    void setUp() {
        sessionDao.clear();
    }

    @Test
    void getAllSessions_returnsEmptyList_whenNothingSaved() throws Exception {
        mockMvc.perform(get("/api/sessions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void createSession_withResistanceExercise_assignsIdAndPreservesSets() throws Exception {
        String body = """
                {
                  "name": "Push Day",
                  "description": "Chest and triceps",
                  "sessionStatus": "PLANNED",
                  "sessionExercises": [
                    {
                      "exerciseType": "resistance",
                      "orderIndex": 1,
                      "exercise": {"name": "Bench Press", "primaryCategory": "RESISTANCE"},
                      "sets": [
                        {"setNumber": 1, "setType": "WORKING", "weight": {"value": 100.0, "unit": "KG"}, "repetitions": 5, "rpe": {"value": 8.0}, "restSeconds": 120}
                      ]
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/sessions/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Push Day"))
                .andExpect(jsonPath("$.sessionExercises[0].exerciseType").value("resistance"))
                .andExpect(jsonPath("$.sessionExercises[0].exercise.name").value("Bench Press"))
                .andExpect(jsonPath("$.sessionExercises[0].sets[0].weight.value").value(100.0))
                .andExpect(jsonPath("$.sessionExercises[0].sets[0].weight.unit").value("KG"))
                .andExpect(jsonPath("$.sessionExercises[0].sets[0].repetitions").value(5))
                .andExpect(jsonPath("$.sessionExercises[0].sets[0].rpe.value").value(8.0));
    }

    @Test
    void createSession_withAllExerciseTypes_roundTrips() throws Exception {
        String body = """
                {
                  "name": "Full Session",
                  "sessionStatus": "DRAFT",
                  "sessionExercises": [
                    {
                      "exerciseType": "resistance",
                      "orderIndex": 1,
                      "exercise": {"name": "Squat", "primaryCategory": "RESISTANCE"},
                      "sets": [
                        {"setNumber": 1, "setType": "WARMUP", "weight": {"value": 60.0, "unit": "KG"}, "repetitions": 5}
                      ]
                    },
                    {
                      "exerciseType": "endurance",
                      "orderIndex": 2,
                      "exercise": {"name": "Interval Run", "primaryCategory": "ENDURANCE"},
                      "intervals": [
                        {"intervalNumber": 1, "distance": {"value": 400.0, "unit": "METERS"}, "duration": {"totalSeconds": 90}}
                      ]
                    },
                    {
                      "exerciseType": "mobility",
                      "orderIndex": 3,
                      "exercise": {"name": "Hamstring Stretch", "primaryCategory": "MOBILITY"},
                      "sets": [
                        {"setNumber": 1, "holdDuration": {"totalSeconds": 30}, "repetitions": 2, "bilateral": true}
                      ]
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/sessions/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.sessionExercises.length()").value(3))
                .andExpect(jsonPath("$.sessionExercises[1].exerciseType").value("endurance"))
                .andExpect(jsonPath("$.sessionExercises[1].intervals[0].distance.value").value(400.0))
                .andExpect(jsonPath("$.sessionExercises[1].intervals[0].duration.totalSeconds").value(90))
                .andExpect(jsonPath("$.sessionExercises[2].exerciseType").value("mobility"))
                .andExpect(jsonPath("$.sessionExercises[2].sets[0].holdDuration.totalSeconds").value(30))
                .andExpect(jsonPath("$.sessionExercises[2].sets[0].bilateral").value(true));
    }

    @Test
    void getSessionById_returnsSession_whenExists() throws Exception {
        createSession();

        mockMvc.perform(get("/api/sessions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Push Day"))
                .andExpect(jsonPath("$.sessionExercises[0].exerciseType").value("resistance"));
    }

    @Test
    void getSessionById_returnsNotFound_whenMissing() throws Exception {
        mockMvc.perform(get("/api/sessions/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateSession_overwritesById() throws Exception {
        createSession();
        String body = """
                {
                  "name": "Push Day - Updated",
                  "sessionStatus": "PLANNED",
                  "sessionExercises": [
                    {
                      "exerciseType": "resistance",
                      "orderIndex": 1,
                      "exercise": {"name": "Bench Press", "primaryCategory": "RESISTANCE"},
                      "sets": [
                        {"setNumber": 1, "setType": "WORKING", "weight": {"value": 110.0, "unit": "KG"}, "repetitions": 3}
                      ]
                    }
                  ]
                }
                """;

        mockMvc.perform(put("/api/sessions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Push Day - Updated"));

        mockMvc.perform(get("/api/sessions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Push Day - Updated"));
    }

    @Test
    void deleteSession_removesSession_andAllowsExistsCheck() throws Exception {
        createSession();

        mockMvc.perform(delete("/api/sessions/1"))
                .andExpect(status().isNoContent());

        assertExists("1", false);

        mockMvc.perform(get("/api/sessions/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteSession_returnsNotFound_whenMissing() throws Exception {
        mockMvc.perform(delete("/api/sessions/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void sessionExists_returnsTrue_whenExists() throws Exception {
        createSession();

        assertExists("1", true);
    }

    @Test
    void updateSession_whenNotFound_returns404() throws Exception {
        String body = """
                {"name":"Unknown Session"}
                """;

        mockMvc.perform(put("/api/sessions/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    private void createSession() throws Exception {
        String body = """
                {
                  "name": "Push Day",
                  "sessionStatus": "PLANNED",
                  "sessionExercises": [
                    {
                      "exerciseType": "resistance",
                      "orderIndex": 1,
                      "exercise": {"name": "Bench Press", "primaryCategory": "RESISTANCE"},
                      "sets": [
                        {"setNumber": 1, "setType": "WORKING", "weight": {"value": 100.0, "unit": "KG"}, "repetitions": 5}
                      ]
                    }
                  ]
                }
                """;
        mockMvc.perform(post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    private void assertExists(String id, boolean expected) throws Exception {
        mockMvc.perform(get("/api/sessions/{id}/exists", id))
                .andExpect(status().isOk())
                .andExpect(content().string(Boolean.toString(expected)));
    }
}