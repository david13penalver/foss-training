package unitary.com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.analytics;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.david13penalver.foss_training_api.FossTrainingApiApplication;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.exercise.InMemoryExerciseDao;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.session.InMemorySessionDao;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.training.InMemoryTrainingDao;

@SpringBootTest(classes = FossTrainingApiApplication.class)
@AutoConfigureMockMvc
class AnalyticsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InMemoryExerciseDao exerciseDao;

    @Autowired
    private InMemorySessionDao sessionDao;

    @Autowired
    private InMemoryTrainingDao trainingDao;

    @BeforeEach
    void setUp() {
        exerciseDao.clear();
        sessionDao.clear();
        trainingDao.clear();
    }

    @Test
    void calculate1Rm_withDefaults_returns200() throws Exception {
        mockMvc.perform(get("/api/analytics/1rm")
                        .param("weight", "100.0")
                        .param("reps", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weight").value(100.0))
                .andExpect(jsonPath("$.unit").value("KG"))
                .andExpect(jsonPath("$.repetitions").value(5))
                .andExpect(jsonPath("$.formula").value("EPLEY"))
                .andExpect(jsonPath("$.estimated1Rm").value(116.67))
                .andExpect(jsonPath("$.percentages['90']").value(105.0));
    }

    @Test
    void calculate1Rm_withCustomFormulaAndUnit_returns200() throws Exception {
        mockMvc.perform(get("/api/analytics/1rm")
                        .param("weight", "225.0")
                        .param("unit", "LBS")
                        .param("reps", "3")
                        .param("formula", "BRZYCKI"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weight").value(225.0))
                .andExpect(jsonPath("$.unit").value("LBS"))
                .andExpect(jsonPath("$.formula").value("BRZYCKI"))
                .andExpect(jsonPath("$.estimated1Rm").value(238.24));
    }

    @Test
    void calculate1Rm_withNegativeWeight_returns400() throws Exception {
        mockMvc.perform(get("/api/analytics/1rm")
                        .param("weight", "-100.0")
                        .param("reps", "5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Request"));
    }

    @Test
    void calculate1Rm_withZeroReps_returns400() throws Exception {
        mockMvc.perform(get("/api/analytics/1rm")
                        .param("weight", "100.0")
                        .param("reps", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Request"));
    }

    @Test
    void getPersonalRecords_returns200() throws Exception {
        createExercise("Bench Press");

        mockMvc.perform(get("/api/analytics/personal-records"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].exerciseName").value("Bench Press"));
    }

    @Test
    void getPersonalRecordsByExercise_whenExists_returns200() throws Exception {
        createExercise("Bench Press");

        mockMvc.perform(get("/api/analytics/personal-records/exercise/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exerciseId").value(1))
                .andExpect(jsonPath("$.exerciseName").value("Bench Press"));
    }

    @Test
    void getPersonalRecordsByExercise_whenNotFound_returns404() throws Exception {
        mockMvc.perform(get("/api/analytics/personal-records/exercise/999"))
                .andExpect(status().isNotFound());
    }

    private void createExercise(String name) throws Exception {
        String exerciseJson = """
                {
                  "name": "%s",
                  "primaryCategory": "RESISTANCE"
                }
                """.formatted(name);

        mockMvc.perform(post("/api/exercises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(exerciseJson))
                .andExpect(status().isCreated());
    }
}
