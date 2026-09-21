package unitary.com.david13penalver.foss_training_api.application.usecase.analytics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.application.usecases.analytics.impl.CalculateHeartRateZonesService;
import com.david13penalver.foss_training_api.domain.model.analytics.HeartRateZoneMethod;
import com.david13penalver.foss_training_api.domain.model.analytics.HeartRateZones;

class CalculateHeartRateZonesServiceTest {

    private final CalculateHeartRateZonesService service = new CalculateHeartRateZonesService();

    @Test
    void execute_calculatesAndReturnsHeartRateZones() {
        HeartRateZones result = service.execute(190, 60, null);

        assertNotNull(result);
        assertEquals(190, result.getMaxHr());
        assertEquals(60, result.getRestingHr());
        assertEquals(HeartRateZoneMethod.KARVONEN, result.getMethod());
        assertEquals(130, result.getHeartRateReserve());
        assertEquals(5, result.getZones().size());
    }
}
