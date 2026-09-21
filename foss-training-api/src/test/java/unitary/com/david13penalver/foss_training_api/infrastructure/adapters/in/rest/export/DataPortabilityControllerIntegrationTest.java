package unitary.com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.export;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
class DataPortabilityControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestDatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        databaseCleaner.clearAll();
    }

    @Test
    void exportBackup_andRestoreViaImportBackup() throws Exception {
        // Create an exercise
        String exerciseJson = """
                {
                  "name": "Back Squat",
                  "primaryCategory": "RESISTANCE"
                }
                """;
        mockMvc.perform(post("/api/exercises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(exerciseJson))
                .andExpect(status().isCreated());

        // Create a bodyweight entry
        String bwJson = """
                {
                  "entryDate": "2026-09-21",
                  "weightKg": 82.5
                }
                """;
        mockMvc.perform(post("/api/athlete/bodyweight")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bwJson))
                .andExpect(status().isCreated());

        // Export backup
        String backupJson = mockMvc.perform(get("/api/data/export/backup"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(jsonPath("$.exportVersion").value("1.0"))
                .andExpect(jsonPath("$.exercises.length()").value(1))
                .andExpect(jsonPath("$.exercises[0].name").value("Back Squat"))
                .andExpect(jsonPath("$.bodyweightEntries.length()").value(1))
                .andReturn().getResponse().getContentAsString();

        // Wipe DB
        databaseCleaner.clearAll();

        // Verify DB is clean
        mockMvc.perform(get("/api/exercises"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        // Restore backup
        mockMvc.perform(post("/api/data/import/backup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(backupJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exercisesImported").value(1))
                .andExpect(jsonPath("$.bodyweightImported").value(1));

        // Verify restored
        mockMvc.perform(get("/api/exercises"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Back Squat"));
    }

    @Test
    void exportWorkoutsCsv_returnsCsvHeaderAndContent() throws Exception {
        String csv = mockMvc.perform(get("/api/data/export/workouts.csv"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/csv; charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        assertTrue(csv.startsWith("training_id,training_date"));
    }

    @Test
    void importWorkoutsCsv_createsTrainingsAndExercises() throws Exception {
        String csv = """
                training_id,training_date,training_name,training_status,session_rpe,exercise_name,exercise_category,item_number,set_type,weight_kg,reps,set_rpe,distance_m,duration_s,notes
                ,2026-09-20,Leg Day,COMPLETED,8,Barbell Squat,RESISTANCE,1,WORKING,100.0,5,8,,,Solid reps
                ,2026-09-20,Leg Day,COMPLETED,8,Barbell Squat,RESISTANCE,2,WORKING,100.0,5,8,,,Good set
                """;

        mockMvc.perform(post("/api/data/import/workouts.csv")
                        .contentType("text/csv")
                        .content(csv))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainingsImported").value(1));

        mockMvc.perform(get("/api/trainings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Leg Day"));
    }
}
