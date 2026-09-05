package unitary.com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.exercise;

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
import com.david13penalver.foss_training_api.infrastructure.adapters.out.exercise.InMemoryExerciseDao;

@SpringBootTest(classes = FossTrainingApiApplication.class)
@AutoConfigureMockMvc
class ExerciseControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InMemoryExerciseDao exerciseDao;

    @BeforeEach
    void setUp() {
        exerciseDao.clear();
    }

    @Test
    void getAllExercises_returnsEmptyList_whenNothingSaved() throws Exception {
        mockMvc.perform(get("/api/exercises"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void createExercise_assignsIdAndReturnsSavedBody() throws Exception {
        String body = """
                {"name":"Squat","description":"Lower body","primaryCategory":"RESISTANCE"}
                """;

        mockMvc.perform(post("/api/exercises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Squat"))
                .andExpect(jsonPath("$.primaryCategory").value("RESISTANCE"));
    }

    @Test
    void getExerciseById_returnsExercise_whenExists() throws Exception {
        createExercise();

        mockMvc.perform(get("/api/exercises/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Squat"));
    }

    @Test
    void getExerciseById_returnsNotFound_whenMissing() throws Exception {
        mockMvc.perform(get("/api/exercises/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllExercises_returnsSavedExercises() throws Exception {
        createExercise();
        createExercise("Bench Press", "ENDURANCE");

        mockMvc.perform(get("/api/exercises"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void updateExercise_overwritesById() throws Exception {
        createExercise();
        String body = """
                {"name":"Squat - Heavy","description":"Updated","primaryCategory":"RESISTANCE"}
                """;

        mockMvc.perform(put("/api/exercises/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Squat - Heavy"));

        mockMvc.perform(get("/api/exercises/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Squat - Heavy"));
    }

    @Test
    void deleteExercise_removesExercise_andAllowsExistsCheck() throws Exception {
        createExercise();

        mockMvc.perform(delete("/api/exercises/1"))
                .andExpect(status().isNoContent());

        assertExists("1", false);

        mockMvc.perform(get("/api/exercises/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteExercise_returnsNotFound_whenMissing() throws Exception {
        mockMvc.perform(delete("/api/exercises/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void exerciseExists_returnsTrue_whenExists() throws Exception {
        createExercise();

        assertExists("1", true);
    }

    private void createExercise() throws Exception {
        createExercise("Squat", "RESISTANCE");
    }

    private void createExercise(String name, String category) throws Exception {
        String body = String.format("""
                {"name":"%s","primaryCategory":"%s"}
                """, name, category);
        mockMvc.perform(post("/api/exercises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    private void assertExists(String id, boolean expected) throws Exception {
        mockMvc.perform(get("/api/exercises/{id}/exists", id))
                .andExpect(status().isOk())
                .andExpect(content().string(Boolean.toString(expected)));
    }
}