package unitary.com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.exercise;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.david13penalver.foss_training_api.FossTrainingApiApplication;

@SpringBootTest(classes = FossTrainingApiApplication.class)
@AutoConfigureMockMvc
class EnumExerciseControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void equipment_returnsAllAndByName() throws Exception {
        mockMvc.perform(get("/api/equipment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").exists());

        mockMvc.perform(get("/api/equipment/BARBELL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("BARBELL"));

        mockMvc.perform(get("/api/equipment/UNKNOWN"))
                .andExpect(status().isNotFound());
    }

    @Test
    void joints_returnsAllAndByName() throws Exception {
        mockMvc.perform(get("/api/joints"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").exists());

        mockMvc.perform(get("/api/joints/NECK"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("NECK"));

        mockMvc.perform(get("/api/joints/UNKNOWN"))
                .andExpect(status().isNotFound());
    }

    @Test
    void enduranceTypes_returnsAllAndByName() throws Exception {
        mockMvc.perform(get("/api/endurance-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").exists());

        mockMvc.perform(get("/api/endurance-types/AEROBIC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("AEROBIC"));

        mockMvc.perform(get("/api/endurance-types/UNKNOWN"))
                .andExpect(status().isNotFound());
    }

    @Test
    void mobilityTypes_returnsAllAndByName() throws Exception {
        mockMvc.perform(get("/api/mobility-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").exists());

        mockMvc.perform(get("/api/mobility-types/STATIC_STRETCHING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("STATIC_STRETCHING"));

        mockMvc.perform(get("/api/mobility-types/UNKNOWN"))
                .andExpect(status().isNotFound());
    }

    @Test
    void movementPatterns_returnsAllAndByName() throws Exception {
        mockMvc.perform(get("/api/movement-patterns"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").exists());

        mockMvc.perform(get("/api/movement-patterns/PUSH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("PUSH"));

        mockMvc.perform(get("/api/movement-patterns/UNKNOWN"))
                .andExpect(status().isNotFound());
    }

    @Test
    void muscleGroups_returnsAllAndByName() throws Exception {
        mockMvc.perform(get("/api/muscle-groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").exists());

        mockMvc.perform(get("/api/muscle-groups/CHEST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("CHEST"));

        mockMvc.perform(get("/api/muscle-groups/UNKNOWN"))
                .andExpect(status().isNotFound());
    }

    @Test
    void stretchTypes_returnsAllAndByName() throws Exception {
        mockMvc.perform(get("/api/stretch-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").exists());

        mockMvc.perform(get("/api/stretch-types/STATIC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("STATIC"));

        mockMvc.perform(get("/api/stretch-types/UNKNOWN"))
                .andExpect(status().isNotFound());
    }
}