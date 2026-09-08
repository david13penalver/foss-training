package unitary.com.david13penalver.foss_training_api.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.domain.model.analytics.OneRepMaxEstimate;
import com.david13penalver.foss_training_api.domain.model.analytics.OneRepMaxFormula;
import com.david13penalver.foss_training_api.domain.model.common.Weight;

class OneRepMaxEstimateTest {

    @Test
    void calculate_success() {
        Weight weight = Weight.kg(100.0);
        OneRepMaxEstimate estimate = OneRepMaxEstimate.calculate(weight, 5, OneRepMaxFormula.EPLEY);

        assertNotNull(estimate);
        assertEquals(weight, estimate.getWeight());
        assertEquals(5, estimate.getReps());
        assertEquals(OneRepMaxFormula.EPLEY, estimate.getFormula());
        assertEquals(116.67, estimate.getEstimated1Rm());

        assertNotNull(estimate.getPercentages());
        assertEquals(10, estimate.getPercentages().size());
        assertEquals(105.0, estimate.getPercentages().get(90)); // 116.67 * 0.90 = 105.00
        assertEquals(58.34, estimate.getPercentages().get(50)); // 116.67 * 0.50 = 58.335 -> 58.34
    }

    @Test
    void calculate_nullWeight_throwsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> OneRepMaxEstimate.calculate(null, 5, OneRepMaxFormula.EPLEY));
        assertEquals("Weight must not be null", ex.getMessage());
    }

    @Test
    void calculate_nullFormula_throwsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> OneRepMaxEstimate.calculate(Weight.kg(100), 5, null));
        assertEquals("Formula must not be null", ex.getMessage());
    }

    @Test
    void noArgsConstructor_andPercentagesUnmodifiable() {
        OneRepMaxEstimate estimate = new OneRepMaxEstimate();
        assertNotNull(estimate.getPercentages());
        assertTrue(estimate.getPercentages().isEmpty());

        estimate.setPercentages(null);
        assertTrue(estimate.getPercentages().isEmpty());
    }
}
