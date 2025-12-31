package unitary.com.david13penalver.foss_training_api.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.domain.model.DifficultyLevel;
import com.david13penalver.foss_training_api.domain.model.EnduranceType;
import com.david13penalver.foss_training_api.domain.model.Equipment;
import com.david13penalver.foss_training_api.domain.model.MovementPattern;

class EnumsTest {

    @Test
    void testDifficultyLevel() {
        DifficultyLevel level = DifficultyLevel.BEGINNER;
        assertEquals(1, level.getLevel());
        assertEquals("Beginner", level.getName());

        assertTrue(DifficultyLevel.INTERMEDIATE.isHarderThan(DifficultyLevel.BEGINNER));
        assertTrue(DifficultyLevel.BEGINNER.isEasierThan(DifficultyLevel.INTERMEDIATE));

        assertEquals(DifficultyLevel.ADVANCED, DifficultyLevel.fromLevel(3));
        assertEquals(DifficultyLevel.EXPERT, DifficultyLevel.fromString("Expert"));

        assertThrows(IllegalArgumentException.class, () -> DifficultyLevel.fromLevel(99));
    }

    @Test
    void testMovementPattern() {
        MovementPattern push = MovementPattern.PUSH;
        assertTrue(push.isCompound());

        MovementPattern isolation = MovementPattern.ISOLATION;
        assertFalse(isolation.isCompound());

        assertEquals(MovementPattern.SQUAT, MovementPattern.fromString("Squat"));
    }

    @Test
    void testEquipment() {
        Equipment eq = Equipment.BARBELL;
        assertNotNull(eq.getDescription());
        assertEquals("Barbell", eq.getName());

        assertEquals(Equipment.DUMBBELL, Equipment.fromString("Dumbbell"));
    }

    @Test
    void testEnduranceType() {
        EnduranceType type = EnduranceType.HIIT;
        assertTrue(type.isHighIntensity());
        assertFalse(type.isLowIntensity());

        EnduranceType liss = EnduranceType.LISS;
        assertTrue(liss.isLowIntensity());
    }
}
