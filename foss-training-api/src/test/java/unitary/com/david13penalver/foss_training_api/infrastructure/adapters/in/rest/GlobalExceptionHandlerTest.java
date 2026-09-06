package unitary.com.david13penalver.foss_training_api.infrastructure.adapters.in.rest;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import com.david13penalver.foss_training_api.FossTrainingApiApplication;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.GlobalExceptionHandler;

@SpringBootTest(classes = FossTrainingApiApplication.class)
@AutoConfigureMockMvc
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GlobalExceptionHandler exceptionHandler;

    @Test
    void validationError_returnsProblemDetailWithInvalidParameters() throws Exception {
        String invalidBody = """
                {"name":"","primaryCategory":"RESISTANCE"}
                """;

        mockMvc.perform(post("/api/exercises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.invalidParameters.name").value("Exercise name cannot be blank"));
    }

    @Test
    void malformedJson_returnsProblemDetailBadRequest() throws Exception {
        mockMvc.perform(post("/api/exercises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{malformed"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void handleIllegalArgumentException_returnsBadRequestProblemDetail() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid exercise ID");
        ResponseEntity<ProblemDetail> response = exceptionHandler.handleIllegalArgumentException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid Request", response.getBody().getTitle());
        assertEquals("Invalid exercise ID", response.getBody().getDetail());
    }

    @Test
    void handleIllegalStateException_returnsConflictProblemDetail() {
        IllegalStateException ex = new IllegalStateException("Session cannot be started");
        ResponseEntity<ProblemDetail> response = exceptionHandler.handleIllegalStateException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Operation Conflict", response.getBody().getTitle());
        assertEquals("Session cannot be started", response.getBody().getDetail());
    }

    @Test
    void handleGenericException_returnsInternalServerErrorProblemDetail() {
        Exception ex = new RuntimeException("Database offline");
        ResponseEntity<ProblemDetail> response = exceptionHandler.handleGenericException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Internal Server Error", response.getBody().getTitle());
        assertEquals("An unexpected error occurred", response.getBody().getDetail());
    }
}
