package unitary.com.david13penalver.foss_training_api.application.usecase.analytics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.application.usecases.analytics.impl.CalculateOneRepMaxService;
import com.david13penalver.foss_training_api.domain.model.analytics.OneRepMaxEstimate;
import com.david13penalver.foss_training_api.domain.model.analytics.OneRepMaxFormula;
import com.david13penalver.foss_training_api.domain.model.common.Weight;

class CalculateOneRepMaxServiceTest {

    private final CalculateOneRepMaxService service = new CalculateOneRepMaxService();

    @Test
    void execute_calculatesEstimate() {
        OneRepMaxEstimate result = service.execute(Weight.kg(100.0), 5, OneRepMaxFormula.EPLEY);

        assertNotNull(result);
        assertEquals(116.67, result.getEstimated1Rm());
        assertEquals(5, result.getReps());
        assertEquals(OneRepMaxFormula.EPLEY, result.getFormula());
    }
}
