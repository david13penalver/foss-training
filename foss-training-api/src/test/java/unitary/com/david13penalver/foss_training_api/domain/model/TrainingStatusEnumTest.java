package unitary.com.david13penalver.foss_training_api.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.domain.model.training.TrainingStatusEnum;

class TrainingStatusEnumTest {

    @Test
    void testAllEnumValuesAndAttributes() {
        for (TrainingStatusEnum status : TrainingStatusEnum.values()) {
            assertNotNull(status.getName());
            assertNotNull(status.getDescription());
            assertNotNull(status.toString());
            assertEquals(status.getName(), status.toString());
        }
    }

    @Test
    void testStatusHelpers() {
        assertTrue(TrainingStatusEnum.PLANNED.canStart());
        assertFalse(TrainingStatusEnum.IN_PROGRESS.canStart());

        assertTrue(TrainingStatusEnum.PAUSED.canResume());
        assertFalse(TrainingStatusEnum.PLANNED.canResume());

        assertTrue(TrainingStatusEnum.IN_PROGRESS.canPause());
        assertFalse(TrainingStatusEnum.PAUSED.canPause());

        assertTrue(TrainingStatusEnum.IN_PROGRESS.canComplete());
        assertTrue(TrainingStatusEnum.PAUSED.canComplete());
        assertFalse(TrainingStatusEnum.PLANNED.canComplete());

        assertTrue(TrainingStatusEnum.PLANNED.canCancel());
        assertTrue(TrainingStatusEnum.PAUSED.canCancel());
        assertFalse(TrainingStatusEnum.COMPLETED.canCancel());
    }

    @Test
    void fromString_parsesCaseInsensitive() {
        assertEquals(TrainingStatusEnum.PLANNED, TrainingStatusEnum.fromString("PLANNED"));
        assertEquals(TrainingStatusEnum.PLANNED, TrainingStatusEnum.fromString("planned"));
        assertEquals(TrainingStatusEnum.IN_PROGRESS, TrainingStatusEnum.fromString("in_progress"));
    }

    @Test
    void fromString_withInvalidOrNull_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> TrainingStatusEnum.fromString("NON_EXISTENT"));
        assertThrows(IllegalArgumentException.class, () -> TrainingStatusEnum.fromString(null));
    }
}
