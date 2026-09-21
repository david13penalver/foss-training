package unitary.com.david13penalver.foss_training_api.domain.model.analytics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.domain.model.analytics.CalculatedHeartRateZone;
import com.david13penalver.foss_training_api.domain.model.analytics.HeartRateZoneCalculator;
import com.david13penalver.foss_training_api.domain.model.analytics.HeartRateZoneMethod;
import com.david13penalver.foss_training_api.domain.model.analytics.HeartRateZones;
import com.david13penalver.foss_training_api.domain.model.common.HeartRateZone;

class HeartRateZoneCalculatorTest {

    @Test
    void compute_withKarvonenFormula_calculatesExpectedZones() {
        // maxHr = 190, restingHr = 60 -> HRR = 130
        HeartRateZones result = HeartRateZoneCalculator.compute(190, 60, null);

        assertNotNull(result);
        assertEquals(190, result.getMaxHr());
        assertEquals(60, result.getRestingHr());
        assertNull(result.getAge());
        assertEquals(HeartRateZoneMethod.KARVONEN, result.getMethod());
        assertEquals(130, result.getHeartRateReserve());
        assertEquals(5, result.getZones().size());

        // Zone 1: 50% - 60% -> 60 + 0.50*130 = 125, 60 + 0.60*130 = 138
        CalculatedHeartRateZone z1 = result.getZones().get(0);
        assertEquals(HeartRateZone.ZONE_1, z1.getZone());
        assertEquals(1, z1.getZoneNumber());
        assertEquals("Active Recovery", z1.getDisplayName());
        assertEquals(50.0, z1.getMinPercentage());
        assertEquals(60.0, z1.getMaxPercentage());
        assertEquals(125, z1.getMinBpm());
        assertEquals(138, z1.getMaxBpm());
        assertNotNull(z1.getDescription());
        assertNotNull(z1.getTrainingBenefit());

        // Zone 2: 60% - 70% -> 60 + 0.60*130 = 138, 60 + 0.70*130 = 151
        CalculatedHeartRateZone z2 = result.getZones().get(1);
        assertEquals(HeartRateZone.ZONE_2, z2.getZone());
        assertEquals(138, z2.getMinBpm());
        assertEquals(151, z2.getMaxBpm());

        // Zone 3: 70% - 80% -> 60 + 0.70*130 = 151, 60 + 0.80*130 = 164
        CalculatedHeartRateZone z3 = result.getZones().get(2);
        assertEquals(HeartRateZone.ZONE_3, z3.getZone());
        assertEquals(151, z3.getMinBpm());
        assertEquals(164, z3.getMaxBpm());

        // Zone 4: 80% - 90% -> 60 + 0.80*130 = 164, 60 + 0.90*130 = 177
        CalculatedHeartRateZone z4 = result.getZones().get(3);
        assertEquals(HeartRateZone.ZONE_4, z4.getZone());
        assertEquals(164, z4.getMinBpm());
        assertEquals(177, z4.getMaxBpm());

        // Zone 5: 90% - 100% -> 60 + 0.90*130 = 177, 60 + 1.00*130 = 190
        CalculatedHeartRateZone z5 = result.getZones().get(4);
        assertEquals(HeartRateZone.ZONE_5, z5.getZone());
        assertEquals(177, z5.getMinBpm());
        assertEquals(190, z5.getMaxBpm());
    }

    @Test
    void compute_withPercentMaxHr_whenRestingHrOmitted() {
        // maxHr = 200, restingHr = null
        HeartRateZones result = HeartRateZoneCalculator.compute(200, null, null);

        assertNotNull(result);
        assertEquals(200, result.getMaxHr());
        assertNull(result.getRestingHr());
        assertEquals(HeartRateZoneMethod.PERCENT_MAX_HR, result.getMethod());
        assertNull(result.getHeartRateReserve());

        // Zone 1: 50% * 200 = 100, 60% * 200 = 120
        assertEquals(100, result.getZones().get(0).getMinBpm());
        assertEquals(120, result.getZones().get(0).getMaxBpm());

        // Zone 5: 90% * 200 = 180, 100% * 200 = 200
        assertEquals(180, result.getZones().get(4).getMinBpm());
        assertEquals(200, result.getZones().get(4).getMaxBpm());
    }

    @Test
    void compute_withAgeFallbackTanakaFormula() {
        // age = 30 -> Tanaka: 208 - (0.7 * 30) = 187
        HeartRateZones result = HeartRateZoneCalculator.compute(null, 60, 30);

        assertNotNull(result);
        assertEquals(187, result.getMaxHr());
        assertEquals(60, result.getRestingHr());
        assertEquals(30, result.getAge());
        assertEquals(HeartRateZoneMethod.KARVONEN, result.getMethod());
        assertEquals(127, result.getHeartRateReserve()); // 187 - 60 = 127
    }

    @Test
    void compute_findZoneForBpm() {
        HeartRateZones zones = HeartRateZoneCalculator.compute(190, 60, null);

        assertEquals(HeartRateZone.ZONE_1, zones.findZoneForBpm(120).getZone()); // below Z1 min clamps to Z1
        assertEquals(HeartRateZone.ZONE_1, zones.findZoneForBpm(130).getZone());
        assertEquals(HeartRateZone.ZONE_2, zones.findZoneForBpm(145).getZone());
        assertEquals(HeartRateZone.ZONE_3, zones.findZoneForBpm(160).getZone());
        assertEquals(HeartRateZone.ZONE_4, zones.findZoneForBpm(170).getZone());
        assertEquals(HeartRateZone.ZONE_5, zones.findZoneForBpm(185).getZone());
        assertEquals(HeartRateZone.ZONE_5, zones.findZoneForBpm(200).getZone()); // above Z5 clamps to Z5
    }

    @Test
    void compute_validationErrors() {
        // Neither maxHr nor age
        assertThrows(IllegalArgumentException.class, () -> HeartRateZoneCalculator.compute(null, 60, null));

        // maxHr out of range
        assertThrows(IllegalArgumentException.class, () -> HeartRateZoneCalculator.compute(50, 40, null));
        assertThrows(IllegalArgumentException.class, () -> HeartRateZoneCalculator.compute(250, 60, null));

        // age out of range
        assertThrows(IllegalArgumentException.class, () -> HeartRateZoneCalculator.compute(null, 60, 5));
        assertThrows(IllegalArgumentException.class, () -> HeartRateZoneCalculator.compute(null, 60, 120));

        // restingHr out of range or >= maxHr
        assertThrows(IllegalArgumentException.class, () -> HeartRateZoneCalculator.compute(180, 20, null));
        assertThrows(IllegalArgumentException.class, () -> HeartRateZoneCalculator.compute(180, 180, null));
        assertThrows(IllegalArgumentException.class, () -> HeartRateZoneCalculator.compute(180, 190, null));
    }
}
