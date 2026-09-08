package unitary.com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.domain.model.common.Distance;
import com.david13penalver.foss_training_api.domain.model.common.DistanceUnit;
import com.david13penalver.foss_training_api.domain.model.common.Duration;
import com.david13penalver.foss_training_api.domain.model.common.Pace;
import com.david13penalver.foss_training_api.domain.model.common.Rpe;
import com.david13penalver.foss_training_api.domain.model.common.Weight;
import com.david13penalver.foss_training_api.domain.model.common.WeightUnit;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.common.DomainValueObjectMapperModule;
import com.fasterxml.jackson.databind.ObjectMapper;

class DomainValueObjectMapperModuleTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new DomainValueObjectMapperModule());
    }

    @Test
    void testWeightDeserialization() throws Exception {
        Weight w = objectMapper.readValue("{\"value\": 80.5, \"unit\": \"KG\"}", Weight.class);
        assertNotNull(w);
        assertEquals(80.5, w.getValue());
        assertEquals(WeightUnit.KG, w.getUnit());

        Weight defaultUnit = objectMapper.readValue("{\"value\": 100.0}", Weight.class);
        assertNotNull(defaultUnit);
        assertEquals(100.0, defaultUnit.getValue());
        assertEquals(WeightUnit.KG, defaultUnit.getUnit());

        Weight defaultValue = objectMapper.readValue("{\"unit\": \"LBS\"}", Weight.class);
        assertNotNull(defaultValue);
        assertEquals(0.0, defaultValue.getValue());
        assertEquals(WeightUnit.LBS, defaultValue.getUnit());
    }

    @Test
    void testDistanceDeserialization() throws Exception {
        Distance d = objectMapper.readValue("{\"value\": 5000.0, \"unit\": \"METERS\"}", Distance.class);
        assertNotNull(d);
        assertEquals(5000.0, d.getValue());
        assertEquals(DistanceUnit.METERS, d.getUnit());

        Distance defaultUnit = objectMapper.readValue("{\"value\": 10.0}", Distance.class);
        assertNotNull(defaultUnit);
        assertEquals(10.0, defaultUnit.getValue());
        assertEquals(DistanceUnit.METERS, defaultUnit.getUnit());

        Distance defaultValue = objectMapper.readValue("{\"unit\": \"MILES\"}", Distance.class);
        assertNotNull(defaultValue);
        assertEquals(0.0, defaultValue.getValue());
        assertEquals(DistanceUnit.MILES, defaultValue.getUnit());
    }

    @Test
    void testDurationDeserialization() throws Exception {
        Duration d1 = objectMapper.readValue("{\"totalSeconds\": 120}", Duration.class);
        assertNotNull(d1);
        assertEquals(120, d1.getTotalSeconds());

        Duration d2 = objectMapper.readValue("180", Duration.class);
        assertNotNull(d2);
        assertEquals(180, d2.getTotalSeconds());

        Duration dDefault = objectMapper.readValue("{}", Duration.class);
        assertNotNull(dDefault);
        assertEquals(0, dDefault.getTotalSeconds());
    }

    @Test
    void testRpeDeserialization() throws Exception {
        Rpe r1 = objectMapper.readValue("{\"value\": 8.5}", Rpe.class);
        assertNotNull(r1);
        assertEquals(8.5, r1.getValue());

        Rpe r2 = objectMapper.readValue("9.0", Rpe.class);
        assertNotNull(r2);
        assertEquals(9.0, r2.getValue());
    }

    @Test
    void testPaceDeserialization() throws Exception {
        Pace p = objectMapper.readValue("{\"secondsPerUnit\": 300, \"unit\": \"KILOMETERS\"}", Pace.class);
        assertNotNull(p);
        assertEquals(300, p.getSecondsPerUnit());
        assertEquals(DistanceUnit.KILOMETERS, p.getUnit());

        Pace defaultUnit = objectMapper.readValue("{\"secondsPerUnit\": 240}", Pace.class);
        assertNotNull(defaultUnit);
        assertEquals(240, defaultUnit.getSecondsPerUnit());
        assertEquals(DistanceUnit.METERS, defaultUnit.getUnit());

        Pace defaultSecs = objectMapper.readValue("{\"unit\": \"MILES\"}", Pace.class);
        assertNotNull(defaultSecs);
        assertEquals(0, defaultSecs.getSecondsPerUnit());
        assertEquals(DistanceUnit.MILES, defaultSecs.getUnit());
    }
}
