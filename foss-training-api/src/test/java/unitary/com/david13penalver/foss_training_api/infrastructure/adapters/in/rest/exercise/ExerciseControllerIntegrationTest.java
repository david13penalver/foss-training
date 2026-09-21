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
import org.springframework.context.annotation.Import;
import unitary.com.david13penalver.foss_training_api.testutil.TestDatabaseCleaner;

@SpringBootTest(classes = FossTrainingApiApplication.class)
@AutoConfigureMockMvc
@Import(TestDatabaseCleaner.class)
class ExerciseControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestDatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        databaseCleaner.clearAll();
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
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/exercises/1"))
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

    @Test
    void updateExercise_whenNotFound_returns404() throws Exception {
        String body = """
                {"name":"Unknown","primaryCategory":"RESISTANCE"}
                """;

        mockMvc.perform(put("/api/exercises/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllExercises_withSearchQuery_returnsMatchingExercises() throws Exception {
        createExercise("Barbell Squat", "RESISTANCE");
        createExercise("Bench Press", "RESISTANCE");
        createExercise("Running", "ENDURANCE");

        mockMvc.perform(get("/api/exercises").param("search", "squat"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Barbell Squat"))
                .andExpect(header().string("X-Total-Count", "1"));
    }

    @Test
    void getAllExercises_withCategoryFilter_returnsMatchingExercises() throws Exception {
        createExercise("Squat", "RESISTANCE");
        createExercise("Deadlift", "RESISTANCE");
        createExercise("Cycling", "ENDURANCE");

        mockMvc.perform(get("/api/exercises").param("primaryCategory", "ENDURANCE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Cycling"))
                .andExpect(header().string("X-Total-Count", "1"));
    }

    @Test
    void getAllExercises_withPagination_returnsPagedContentAndHeaders() throws Exception {
        createExercise("Exercise A", "RESISTANCE");
        createExercise("Exercise B", "RESISTANCE");
        createExercise("Exercise C", "RESISTANCE");

        mockMvc.perform(get("/api/exercises")
                        .param("page", "0")
                        .param("size", "2")
                        .param("sortBy", "name")
                        .param("sortDirection", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Exercise A"))
                .andExpect(jsonPath("$[1].name").value("Exercise B"))
                .andExpect(header().string("X-Total-Count", "3"))
                .andExpect(header().string("X-Total-Pages", "2"))
                .andExpect(header().string("X-Page-Number", "0"))
                .andExpect(header().string("X-Page-Size", "2"));
    }

    @Test
    void searchExercises_returnsPageResponseDto() throws Exception {
        createExercise("Bench Press", "RESISTANCE");
        createExercise("Overhead Press", "RESISTANCE");
        createExercise("Pull Up", "RESISTANCE");

        mockMvc.perform(get("/api/exercises/search")
                        .param("search", "press")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true));
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
                .andExpect(status().isCreated());
    }

    private void assertExists(String id, boolean expected) throws Exception {
        mockMvc.perform(get("/api/exercises/{id}/exists", id))
                .andExpect(status().isOk())
                .andExpect(content().string(Boolean.toString(expected)));
    }
}