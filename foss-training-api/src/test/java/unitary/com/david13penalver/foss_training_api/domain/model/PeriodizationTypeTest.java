package unitary.com.david13penalver.foss_training_api.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.david13penalver.foss_training_api.domain.model.program.PeriodizationType;

class PeriodizationTypeTest {

    @ParameterizedTest
    @EnumSource(PeriodizationType.class)
    void fromString_exactMatch(PeriodizationType type) {
        assertEquals(type, PeriodizationType.fromString(type.name()));
        assertEquals(type, PeriodizationType.fromString(type.name().toLowerCase()));
    }

    @Test
    void fromString_withWhitespace() {
        assertEquals(PeriodizationType.LINEAR, PeriodizationType.fromString("  linear  "));
    }

    @Test
    void fromString_invalidValues() {
        assertThrows(IllegalArgumentException.class, () -> PeriodizationType.fromString(null));
        assertThrows(IllegalArgumentException.class, () -> PeriodizationType.fromString("   "));
        assertThrows(IllegalArgumentException.class, () -> PeriodizationType.fromString("UNKNOWN"));
    }
}
