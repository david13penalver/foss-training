package unitary.com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.athlete;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.david13penalver.foss_training_api.FossTrainingApiApplication;
import unitary.com.david13penalver.foss_training_api.testutil.TestDatabaseCleaner;

@SpringBootTest(classes = FossTrainingApiApplication.class)
@AutoConfigureMockMvc
@Import(TestDatabaseCleaner.class)
class AthleteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestDatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        databaseCleaner.clearAll();
    }

    @Test
    void logBodyweight_andRetrieveHistoryAndLatest() throws Exception {
        mockMvc.perform(get("/api/athlete/bodyweight/latest"))
                .andExpect(status().isNotFound());

        String request1 = """
                {
                  "entryDate": "2026-09-15",
                  "weightKg": 82.0,
                  "bodyFatPercentage": 15.0,
                  "notes": "Baseline"
                }
                """;
        mockMvc.perform(post("/api/athlete/bodyweight")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request1))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.weightKg").value(82.0))
                .andExpect(jsonPath("$.entryDate").value("2026-09-15"));

        String request2 = """
                {
                  "entryDate": "2026-09-20",
                  "weightKg": 81.2,
                  "bodyFatPercentage": 14.8,
                  "notes": "Post cut"
                }
                """;
        mockMvc.perform(post("/api/athlete/bodyweight")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request2))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.weightKg").value(81.2));

        mockMvc.perform(get("/api/athlete/bodyweight/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].entryDate").value("2026-09-15"))
                .andExpect(jsonPath("$[1].entryDate").value("2026-09-20"));

        mockMvc.perform(get("/api/athlete/bodyweight/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weightKg").value(81.2))
                .andExpect(jsonPath("$.entryDate").value("2026-09-20"));

        // Delete entry 1
        mockMvc.perform(delete("/api/athlete/bodyweight/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/athlete/bodyweight/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(2));
    }

    @Test
    void calculateRelativeStrength_returnsCalculatedScores() throws Exception {
        String request = """
                {
                  "totalWeightKg": 500.0,
                  "bodyweightKg": 80.0,
                  "gender": "MALE"
                }
                """;

        mockMvc.perform(post("/api/athlete/relative-strength")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalWeightKg").value(500.0))
                .andExpect(jsonPath("$.bodyweightKg").value(80.0))
                .andExpect(jsonPath("$.ratio").value(6.25))
                .andExpect(jsonPath("$.dots").isNumber())
                .andExpect(jsonPath("$.wilks").isNumber())
                .andExpect(jsonPath("$.classification").isString());
    }

    @Test
    void calculateRelativeStrength_withInvalidInput_returns400() throws Exception {
        String request = """
                {
                  "totalWeightKg": -100.0,
                  "bodyweightKg": 80.0
                }
                """;

        mockMvc.perform(post("/api/athlete/relative-strength")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }
}
