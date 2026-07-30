package unitary.com.david13penalver.foss_training_api.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.david13penalver.foss_training_api.domain.model.session.SessionPartEnum;
import com.david13penalver.foss_training_api.domain.model.session.SessionStatusEnum;
import org.junit.jupiter.api.Test;

class SessionEnumsTest {

    @Test
    void testSessionPartEnumValues() {
        assertEquals(8, SessionPartEnum.values().length);
    }

    @Test
    void testSessionPartEnumActivation() {
        SessionPartEnum part = SessionPartEnum.ACTIVATION;
        assertEquals("Activation Phase", part.getName());
        assertNotNull(part.getDescription());
        assertNotNull(part.getExamples());
        assertEquals(5, part.getRecommendedMinDuration());
        assertEquals(10, part.getRecommendedMaxDuration());
        assertTrue(part.isPreWorkout());
        assertFalse(part.isMainTraining());
        assertFalse(part.isPostWorkout());
        assertFalse(part.requiresHighCNSFreshness());
    }

    @Test
    void testSessionPartEnumWarmup() {
        SessionPartEnum part = SessionPartEnum.WARMUP;
        assertEquals("Warm-up Phase", part.getName());
        assertTrue(part.isPreWorkout());
        assertFalse(part.isMainTraining());
        assertFalse(part.isPostWorkout());
        assertFalse(part.requiresHighCNSFreshness());
    }

    @Test
    void testSessionPartEnumSkillWork() {
        SessionPartEnum part = SessionPartEnum.SKILL_WORK;
        assertTrue(part.isPreWorkout());
        assertFalse(part.isMainTraining());
        assertFalse(part.isPostWorkout());
        assertTrue(part.requiresHighCNSFreshness());
    }

    @Test
    void testSessionPartEnumMainWork() {
        SessionPartEnum part = SessionPartEnum.MAIN_WORK;
        assertFalse(part.isPreWorkout());
        assertTrue(part.isMainTraining());
        assertFalse(part.isPostWorkout());
        assertTrue(part.requiresHighCNSFreshness());
        assertEquals(20, part.getRecommendedMinDuration());
        assertEquals(60, part.getRecommendedMaxDuration());
    }

    @Test
    void testSessionPartEnumAccessoryWork() {
        SessionPartEnum part = SessionPartEnum.ACCESSORY_WORK;
        assertFalse(part.isPreWorkout());
        assertTrue(part.isMainTraining());
        assertFalse(part.isPostWorkout());
        assertFalse(part.requiresHighCNSFreshness());
    }

    @Test
    void testSessionPartEnumMetabolicConditioning() {
        SessionPartEnum part = SessionPartEnum.METABOLIC_CONDITIONING;
        assertFalse(part.isPreWorkout());
        assertTrue(part.isMainTraining());
        assertFalse(part.isPostWorkout());
        assertFalse(part.requiresHighCNSFreshness());
    }

    @Test
    void testSessionPartEnumCooldown() {
        SessionPartEnum part = SessionPartEnum.COOLDOWN;
        assertFalse(part.isPreWorkout());
        assertFalse(part.isMainTraining());
        assertTrue(part.isPostWorkout());
        assertFalse(part.requiresHighCNSFreshness());
    }

    @Test
    void testSessionPartEnumRecovery() {
        SessionPartEnum part = SessionPartEnum.RECOVERY;
        assertFalse(part.isPreWorkout());
        assertFalse(part.isMainTraining());
        assertTrue(part.isPostWorkout());
        assertFalse(part.requiresHighCNSFreshness());
    }

    @Test
    void testSessionPartEnumFromString() {
        assertEquals(SessionPartEnum.ACTIVATION, SessionPartEnum.fromString("ACTIVATION"));
        assertEquals(SessionPartEnum.WARMUP, SessionPartEnum.fromString("warmup"));
        assertEquals(SessionPartEnum.MAIN_WORK, SessionPartEnum.fromString("main_work"));
        assertEquals(SessionPartEnum.COOLDOWN, SessionPartEnum.fromString("COOLDOWN"));
        assertEquals(SessionPartEnum.RECOVERY, SessionPartEnum.fromString("Recovery"));
        assertThrows(IllegalArgumentException.class, () -> SessionPartEnum.fromString("invalid"));
    }

    @Test
    void testSessionStatusEnumValues() {
        assertEquals(11, SessionStatusEnum.values().length);
    }

    @Test
    void testSessionStatusEnumPlanned() {
        SessionStatusEnum status = SessionStatusEnum.PLANNED;
        assertEquals("Planned", status.getName());
        assertNotNull(status.getDescription());
        assertTrue(status.isEditable());
        assertFalse(status.isFinal());
        assertTrue(status.canStart());
        assertFalse(status.canResume());
        assertFalse(status.canPause());
        assertFalse(status.canComplete());
        assertTrue(status.canCancel());
        assertFalse(status.countsTowardAdherence());
        assertFalse(status.countsAsNonAdherence());
        assertFalse(status.requiresRecoveryTracking());
        assertTrue(status.allowsDataModification());
        assertEquals("#4CAF50", status.getDisplayColor());
    }

    @Test
    void testSessionStatusEnumDraft() {
        SessionStatusEnum status = SessionStatusEnum.DRAFT;
        assertEquals("Draft", status.getName());
        assertTrue(status.isEditable());
        assertFalse(status.isFinal());
        assertTrue(status.canStart());
        assertFalse(status.canResume());
        assertFalse(status.canPause());
        assertFalse(status.canComplete());
        assertTrue(status.canCancel());
        assertFalse(status.countsTowardAdherence());
        assertFalse(status.countsAsNonAdherence());
        assertFalse(status.requiresRecoveryTracking());
        assertTrue(status.allowsDataModification());
        assertEquals("#9E9E9E", status.getDisplayColor());
    }

    @Test
    void testSessionStatusEnumReady() {
        SessionStatusEnum status = SessionStatusEnum.READY;
        assertEquals("Ready to Start", status.getName());
        assertTrue(status.isEditable());
        assertFalse(status.isFinal());
        assertTrue(status.canStart());
        assertFalse(status.canResume());
        assertFalse(status.canPause());
        assertFalse(status.canComplete());
        assertTrue(status.canCancel());
        assertFalse(status.countsTowardAdherence());
        assertFalse(status.countsAsNonAdherence());
        assertFalse(status.requiresRecoveryTracking());
        assertTrue(status.allowsDataModification());
        assertEquals("#4CAF50", status.getDisplayColor());
    }

    @Test
    void testSessionStatusEnumInProgress() {
        SessionStatusEnum status = SessionStatusEnum.IN_PROGRESS;
        assertEquals("In Progress", status.getName());
        assertFalse(status.isEditable());
        assertFalse(status.isFinal());
        assertFalse(status.canStart());
        assertFalse(status.canResume());
        assertTrue(status.canPause());
        assertTrue(status.canComplete());
        assertFalse(status.canCancel());
        assertFalse(status.countsTowardAdherence());
        assertFalse(status.countsAsNonAdherence());
        assertFalse(status.requiresRecoveryTracking());
        assertFalse(status.allowsDataModification());
        assertEquals("#2196F3", status.getDisplayColor());
    }

    @Test
    void testSessionStatusEnumPaused() {
        SessionStatusEnum status = SessionStatusEnum.PAUSED;
        assertEquals("Paused", status.getName());
        assertFalse(status.isEditable());
        assertFalse(status.isFinal());
        assertFalse(status.canStart());
        assertTrue(status.canResume());
        assertFalse(status.canPause());
        assertTrue(status.canComplete());
        assertTrue(status.canCancel());
        assertFalse(status.countsTowardAdherence());
        assertFalse(status.countsAsNonAdherence());
        assertFalse(status.requiresRecoveryTracking());
        assertFalse(status.allowsDataModification());
        assertEquals("#FF9800", status.getDisplayColor());
    }

    @Test
    void testSessionStatusEnumCompleted() {
        SessionStatusEnum status = SessionStatusEnum.COMPLETED;
        assertEquals("Completed", status.getName());
        assertFalse(status.isEditable());
        assertTrue(status.isFinal());
        assertFalse(status.canStart());
        assertFalse(status.canResume());
        assertFalse(status.canPause());
        assertFalse(status.canComplete());
        assertFalse(status.canCancel());
        assertTrue(status.countsTowardAdherence());
        assertFalse(status.countsAsNonAdherence());
        assertTrue(status.requiresRecoveryTracking());
        assertFalse(status.allowsDataModification());
        assertEquals("#00C853", status.getDisplayColor());
    }

    @Test
    void testSessionStatusEnumPartiallyCompleted() {
        SessionStatusEnum status = SessionStatusEnum.PARTIALLY_COMPLETED;
        assertTrue(status.isFinal());
        assertTrue(status.countsTowardAdherence());
        assertFalse(status.countsAsNonAdherence());
        assertTrue(status.requiresRecoveryTracking());
        assertEquals("#FFC107", status.getDisplayColor());
    }

    @Test
    void testSessionStatusEnumSkipped() {
        SessionStatusEnum status = SessionStatusEnum.SKIPPED;
        assertTrue(status.isFinal());
        assertFalse(status.countsTowardAdherence());
        assertTrue(status.countsAsNonAdherence());
        assertEquals("#F44336", status.getDisplayColor());
    }

    @Test
    void testSessionStatusEnumMissed() {
        SessionStatusEnum status = SessionStatusEnum.MISSED;
        assertTrue(status.isFinal());
        assertFalse(status.countsTowardAdherence());
        assertTrue(status.countsAsNonAdherence());
        assertEquals("#F44336", status.getDisplayColor());
    }

    @Test
    void testSessionStatusEnumCancelled() {
        SessionStatusEnum status = SessionStatusEnum.CANCELLED;
        assertTrue(status.isFinal());
        assertFalse(status.canCancel());
        assertFalse(status.countsTowardAdherence());
        assertFalse(status.countsAsNonAdherence());
        assertFalse(status.requiresRecoveryTracking());
        assertFalse(status.allowsDataModification());
        assertEquals("#757575", status.getDisplayColor());
    }

    @Test
    void testSessionStatusEnumFailed() {
        SessionStatusEnum status = SessionStatusEnum.FAILED;
        assertEquals("Failed/Abandoned", status.getName());
        assertTrue(status.isFinal());
        assertFalse(status.canStart());
        assertFalse(status.canResume());
        assertFalse(status.canPause());
        assertFalse(status.canComplete());
        assertFalse(status.canCancel());
        assertFalse(status.countsTowardAdherence());
        assertFalse(status.countsAsNonAdherence());
        assertFalse(status.requiresRecoveryTracking());
        assertFalse(status.allowsDataModification());
        assertEquals("#D32F2F", status.getDisplayColor());
    }

    @Test
    void testSessionStatusEnumFromString() {
        assertEquals(SessionStatusEnum.PLANNED, SessionStatusEnum.fromString("PLANNED"));
        assertEquals(SessionStatusEnum.IN_PROGRESS, SessionStatusEnum.fromString("in_progress"));
        assertEquals(SessionStatusEnum.COMPLETED, SessionStatusEnum.fromString("Completed"));
        assertEquals(SessionStatusEnum.FAILED, SessionStatusEnum.fromString("failed"));
        assertThrows(IllegalArgumentException.class, () -> SessionStatusEnum.fromString("unknown"));
    }

    @Test
    void testSessionStatusEnumToString() {
        assertEquals("Planned", SessionStatusEnum.PLANNED.toString());
        assertEquals("In Progress", SessionStatusEnum.IN_PROGRESS.toString());
        assertEquals("Completed", SessionStatusEnum.COMPLETED.toString());
    }
}
