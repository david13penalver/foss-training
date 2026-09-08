package unitary.com.david13penalver.foss_training_api.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.david13penalver.foss_training_api.domain.model.program.ProgramLevel;

class ProgramLevelTest {

    @ParameterizedTest
    @EnumSource(ProgramLevel.class)
    void fromString_exactMatch(ProgramLevel level) {
        assertEquals(level, ProgramLevel.fromString(level.name()));
        assertEquals(level, ProgramLevel.fromString(level.name().toLowerCase()));
    }

    @Test
    void fromString_withWhitespace() {
        assertEquals(ProgramLevel.INTERMEDIATE, ProgramLevel.fromString("  intermediate  "));
    }

    @Test
    void fromString_invalidValues() {
        assertThrows(IllegalArgumentException.class, () -> ProgramLevel.fromString(null));
        assertThrows(IllegalArgumentException.class, () -> ProgramLevel.fromString("   "));
        assertThrows(IllegalArgumentException.class, () -> ProgramLevel.fromString("UNKNOWN"));
    }
}
