package unitary.com.david13penalver.foss_training_api.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.david13penalver.foss_training_api.domain.model.analytics.OneRepMaxFormula;

class OneRepMaxFormulaTest {

    @ParameterizedTest
    @EnumSource(OneRepMaxFormula.class)
    void calculate_withOneRep_returnsWeight(OneRepMaxFormula formula) {
        assertEquals(100.0, formula.calculate(100.0, 1));
    }

    @Test
    void calculate_epley() {
        // 100 * (1 + 5/30) = 116.67
        assertEquals(116.67, OneRepMaxFormula.EPLEY.calculate(100.0, 5));
    }

    @Test
    void calculate_brzycki() {
        // 100 * (36 / 32) = 112.5
        assertEquals(112.5, OneRepMaxFormula.BRZYCKI.calculate(100.0, 5));
    }

    @Test
    void calculate_brzycki_throwsWhenRepsGte37() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> OneRepMaxFormula.BRZYCKI.calculate(100.0, 37));
        assertEquals("Repetitions must be less than 37 for Brzycki formula", ex.getMessage());
    }

    @Test
    void calculate_lombardi() {
        // 100 * 5^0.10 = 117.46
        assertEquals(117.46, OneRepMaxFormula.LOMBARDI.calculate(100.0, 5));
    }

    @Test
    void calculate_mayhew() {
        assertEquals(119.01, OneRepMaxFormula.MAYHEW.calculate(100.0, 5));
    }

    @Test
    void calculate_oconner() {
        assertEquals(112.5, OneRepMaxFormula.OCONNER.calculate(100.0, 5));
    }

    @Test
    void calculate_wathen() {
        assertEquals(116.58, OneRepMaxFormula.WATHEN.calculate(100.0, 5));
    }

    @Test
    void calculate_invalidWeight_throwsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> OneRepMaxFormula.EPLEY.calculate(0.0, 5));
        assertEquals("Weight must be greater than zero", ex.getMessage());

        assertThrows(IllegalArgumentException.class,
                () -> OneRepMaxFormula.EPLEY.calculate(-10.0, 5));
    }

    @Test
    void calculate_invalidReps_throwsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> OneRepMaxFormula.EPLEY.calculate(100.0, 0));
        assertEquals("Repetitions must be at least 1", ex.getMessage());

        assertThrows(IllegalArgumentException.class,
                () -> OneRepMaxFormula.EPLEY.calculate(100.0, -2));
    }

    @Test
    void fromString_validValues() {
        assertEquals(OneRepMaxFormula.EPLEY, OneRepMaxFormula.fromString("epley"));
        assertEquals(OneRepMaxFormula.BRZYCKI, OneRepMaxFormula.fromString("BRZYCKI"));
        assertEquals(OneRepMaxFormula.LOMBARDI, OneRepMaxFormula.fromString("  lombardi  "));
    }

    @Test
    void fromString_invalidValues() {
        assertThrows(IllegalArgumentException.class, () -> OneRepMaxFormula.fromString(null));
        assertThrows(IllegalArgumentException.class, () -> OneRepMaxFormula.fromString("   "));
        assertThrows(IllegalArgumentException.class, () -> OneRepMaxFormula.fromString("UNKNOWN"));
    }
}
