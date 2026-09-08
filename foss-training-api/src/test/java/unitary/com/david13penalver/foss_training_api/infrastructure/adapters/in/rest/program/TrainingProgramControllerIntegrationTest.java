package unitary.com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.program;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.david13penalver.foss_training_api.FossTrainingApiApplication;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.program.InMemoryTrainingProgramDao;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.training.InMemoryTrainingDao;

@SpringBootTest(classes = FossTrainingApiApplication.class)
@AutoConfigureMockMvc
class TrainingProgramControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InMemoryTrainingProgramDao programDao;

    @Autowired
    private InMemoryTrainingDao trainingDao;

    @BeforeEach
    void setUp() {
        programDao.clear();
        trainingDao.clear();
    }

    private String validProgramJson(String name, int weeks) {
        return """
                {
                  "name": "%s",
                  "description": "Full training plan",
                  "durationWeeks": %d,
                  "periodizationType": "LINEAR",
                  "level": "INTERMEDIATE",
                  "workouts": [
                    {
                      "dayOfWeek": 1,
                      "focus": "Upper Body",
                      "session": {
                        "name": "Upper Power",
                        "sessionStatus": "PLANNED",
                        "sessionExercises": []
                      }
                    }
                  ]
                }
                """.formatted(name, weeks);
    }

    @Test
    void createProgram_returns201WithLocation() throws Exception {
        mockMvc.perform(post("/api/programs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validProgramJson("12-Week PPL", 12)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/programs/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("12-Week PPL"))
                .andExpect(jsonPath("$.durationWeeks").value(12))
                .andExpect(jsonPath("$.periodizationType").value("LINEAR"))
                .andExpect(jsonPath("$.level").value("INTERMEDIATE"))
                .andExpect(jsonPath("$.workouts.length()").value(1));
    }

    @Test
    void getProgramById_whenExists_returns200() throws Exception {
        createProgram("12-Week PPL", 12);

        mockMvc.perform(get("/api/programs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("12-Week PPL"));
    }

    @Test
    void getProgramById_whenMissing_returns404() throws Exception {
        mockMvc.perform(get("/api/programs/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllPrograms_returns200() throws Exception {
        createProgram("P1", 4);
        createProgram("P2", 8);

        mockMvc.perform(get("/api/programs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void updateProgram_whenExists_returns200() throws Exception {
        createProgram("P1", 4);

        mockMvc.perform(put("/api/programs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validProgramJson("P1 Updated", 6)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("P1 Updated"))
                .andExpect(jsonPath("$.durationWeeks").value(6));
    }

    @Test
    void updateProgram_whenNotFound_returns404() throws Exception {
        mockMvc.perform(put("/api/programs/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validProgramJson("Missing", 4)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteProgram_whenExists_returns204() throws Exception {
        createProgram("P1", 4);

        mockMvc.perform(delete("/api/programs/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/programs/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteProgram_whenNotFound_returns404() throws Exception {
        mockMvc.perform(delete("/api/programs/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void programExists_returnsTrueThenFalse() throws Exception {
        createProgram("P1", 4);

        mockMvc.perform(get("/api/programs/1/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        mockMvc.perform(delete("/api/programs/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/programs/1/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void createProgram_withBlankName_returns400() throws Exception {
        String invalid = """
                {
                  "name": "",
                  "durationWeeks": 4
                }
                """;

        mockMvc.perform(post("/api/programs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalid))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Failed"));
    }

    @Test
    void generateSchedule_whenExists_returns201AndCreatesTrainings() throws Exception {
        createProgram("4-Week Block", 2);

        mockMvc.perform(post("/api/programs/1/generate-schedule")
                        .param("startDate", "2026-09-14"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("4-Week Block - W1D1: Upper Power"))
                .andExpect(jsonPath("$[0].trainingDate").value("2026-09-14"))
                .andExpect(jsonPath("$[1].name").value("4-Week Block - W2D1: Upper Power"))
                .andExpect(jsonPath("$[1].trainingDate").value("2026-09-21"));
    }

    @Test
    void generateSchedule_whenNotFound_returns404() throws Exception {
        mockMvc.perform(post("/api/programs/999/generate-schedule"))
                .andExpect(status().isNotFound());
    }

    @Test
    void generateSchedule_whenNoWorkouts_returns400() throws Exception {
        String emptyWorkouts = """
                {
                  "name": "Empty Program",
                  "durationWeeks": 2,
                  "workouts": []
                }
                """;
        mockMvc.perform(post("/api/programs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emptyWorkouts))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/programs/1/generate-schedule"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Request"));
    }

    private void createProgram(String name, int weeks) throws Exception {
        mockMvc.perform(post("/api/programs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validProgramJson(name, weeks)))
                .andExpect(status().isCreated());
    }
}
