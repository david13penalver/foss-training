package unitary.com.david13penalver.foss_training_api.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.domain.model.EnduranceMetrics;
import com.david13penalver.foss_training_api.domain.model.EnduranceType;
import com.david13penalver.foss_training_api.domain.model.MobilityMetrics;
import com.david13penalver.foss_training_api.domain.model.MobilityType;
import com.david13penalver.foss_training_api.domain.model.MovementPattern;
import com.david13penalver.foss_training_api.domain.model.MuscleGroup;
import com.david13penalver.foss_training_api.domain.model.ResistanceMetrics;

import java.util.Collections;
import java.util.List;

class MetricsTests {

    @Test
    void testResistanceMetrics() {
        ResistanceMetrics metrics = new ResistanceMetrics();
        metrics.setId(1);
        metrics.setMovementPattern(MovementPattern.PUSH);
        metrics.setPrimaryMuscles(List.of(MuscleGroup.CHEST));

        assertEquals(1, metrics.getId());
        assertEquals(MovementPattern.PUSH, metrics.getMovementPattern());
        assertEquals(MuscleGroup.CHEST, metrics.getPrimaryMuscles().get(0));

        ResistanceMetrics allArgs = new ResistanceMetrics(
                2,
                List.of(MuscleGroup.QUADRICEPS),
                Collections.emptyList(),
                MovementPattern.SQUAT,
                true, false, false, "kg",
                3, 8, 12, 60, "2-0-2",
                10.0, 100.0, 2.5);
        assertEquals(MovementPattern.SQUAT, allArgs.getMovementPattern());
    }

    @Test
    void testEnduranceMetrics() {
        EnduranceMetrics metrics = new EnduranceMetrics();
        metrics.setEnduranceType(EnduranceType.AEROBIC);

        assertNotNull(metrics);
        // Assuming Enum exists or matches what was created.
        // Note: In previous step EnduranceType had AEROBIC, ANAEROBIC etc.
        // Let's use a valid one from created file.
        metrics.setEnduranceType(EnduranceType.AEROBIC);
        assertEquals(EnduranceType.AEROBIC, metrics.getEnduranceType());
    }

    @Test
    void testMobilityMetrics() {
        MobilityMetrics metrics = new MobilityMetrics();
        metrics.setMobilityType(MobilityType.YOGA);

        assertNotNull(metrics);
        assertEquals(MobilityType.YOGA, metrics.getMobilityType());
    }
}
