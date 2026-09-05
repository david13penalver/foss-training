package unitary.com.david13penalver.foss_training_api.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.david13penalver.foss_training_api.domain.model.common.Distance;
import com.david13penalver.foss_training_api.domain.model.common.DistanceUnit;
import com.david13penalver.foss_training_api.domain.model.common.Duration;
import com.david13penalver.foss_training_api.domain.model.common.HeartRateZone;
import com.david13penalver.foss_training_api.domain.model.common.Pace;
import com.david13penalver.foss_training_api.domain.model.common.Rpe;
import com.david13penalver.foss_training_api.domain.model.common.Tempo;
import com.david13penalver.foss_training_api.domain.model.common.Weight;
import com.david13penalver.foss_training_api.domain.model.common.WeightUnit;
import org.junit.jupiter.api.Test;

class ValueObjectsTest {

    @Test
    void testEquals_selfReference_returnsTrueForAllVOs() {
        Weight weight = Weight.kg(50.0);
        Distance distance = Distance.kilometers(5.0);
        Duration duration = Duration.minutes(30);
        Pace pace = Pace.from(distance, duration);
        Rpe rpe = Rpe.of(8.0);
        Tempo tempo = Tempo.parse("3-1-1-0");

        assertTrue(weight.equals(weight));
        assertTrue(distance.equals(distance));
        assertTrue(duration.equals(duration));
        assertTrue(pace.equals(pace));
        assertTrue(rpe.equals(rpe));
        assertTrue(tempo.equals(tempo));
    }

    @Test
    void testEquals_differentClass_returnsFalseForAllVOs() {
        Weight weight = Weight.kg(50.0);
        Distance distance = Distance.kilometers(5.0);
        Duration duration = Duration.minutes(30);
        Pace pace = Pace.from(distance, duration);
        Rpe rpe = Rpe.of(8.0);
        Tempo tempo = Tempo.parse("3-1-1-0");

assertFalse(weight.equals("50kg"));
        assertFalse(distance.equals("5km"));
        assertFalse(duration.equals("30min"));
        assertFalse(pace.equals("6:00"));
        assertFalse(rpe.equals("8.0"));
        assertFalse(tempo.equals("3-1-1-0"));
        assertFalse(weight.equals(new Object()));
        assertFalse(distance.equals(new Object()));
        assertFalse(duration.equals(new Object()));
        assertFalse(pace.equals(new Object()));
        assertFalse(rpe.equals(new Object()));
        assertFalse(tempo.equals(new Object()));
    }

    @Test
    void testEquals_symmetricForAllVOs() {
        Weight a = Weight.kg(50.0), b = Weight.kg(50.0);
        Distance d1 = new Distance(5000.0, DistanceUnit.METERS), d2 = new Distance(5.0, DistanceUnit.KILOMETERS);
        Duration u1 = Duration.minutes(30), u2 = new Duration(1800);
        Pace p1 = Pace.from(Distance.kilometers(5.0), u1), p2 = Pace.from(Distance.kilometers(5.0), u2);
        Rpe r1 = Rpe.of(8.0), r2 = Rpe.of(8.0);
        Tempo t1 = new Tempo(3, 1, 1, 0), t2 = new Tempo(3, 1, 1, 0);

        assertEquals(a.equals(b), b.equals(a));
        assertTrue(a.equals(b));
        assertTrue(b.equals(a));
        assertEquals(d1.equals(d2), d2.equals(d1));
        assertTrue(d1.equals(d2));
        assertTrue(d2.equals(d1));
        assertEquals(u1.equals(u2), u2.equals(u1));
        assertTrue(u1.equals(u2));
        assertTrue(u2.equals(u1));
        assertEquals(p1.equals(p2), p2.equals(p1));
        assertTrue(p1.equals(p2));
        assertTrue(p2.equals(p1));
        assertEquals(r1.equals(r2), r2.equals(r1));
        assertTrue(r1.equals(r2));
        assertTrue(r2.equals(r1));
        assertEquals(t1.equals(t2), t2.equals(t1));
        assertTrue(t1.equals(t2));
        assertTrue(t2.equals(t1));
    }

    @Test
    void testWeightUnitEnumValues() {
        assertEquals(2, WeightUnit.values().length);
    }

    @Test
    void testWeightUnitKg() {
        assertEquals("kg", WeightUnit.KG.getSymbol());
        assertEquals(1.0, WeightUnit.KG.toKgFactor(), 1e-9);
    }

    @Test
    void testWeightUnitLbs() {
        assertEquals("lbs", WeightUnit.LBS.getSymbol());
        assertEquals(0.45359237, WeightUnit.LBS.toKgFactor(), 1e-9);
    }

    @Test
    void testWeightConstructorAndGetters() {
        Weight w = new Weight(100.0, WeightUnit.KG);
        assertEquals(100.0, w.getValue(), 1e-9);
        assertEquals(WeightUnit.KG, w.getUnit());
    }

    @Test
    void testWeightConstructorNegativeValueThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Weight(-1.0, WeightUnit.KG));
    }

    @Test
    void testWeightConstructorNullUnitThrows() {
        assertThrows(NullPointerException.class, () -> new Weight(10.0, null));
    }

    @Test
    void testWeightZeroIsAllowed() {
        Weight w = new Weight(0.0, WeightUnit.KG);
        assertEquals(0.0, w.getValue(), 1e-9);
    }

    @Test
    void testWeightFactoryKg() {
        Weight w = Weight.kg(50.0);
        assertEquals(50.0, w.getValue(), 1e-9);
        assertEquals(WeightUnit.KG, w.getUnit());
    }

    @Test
    void testWeightFactoryLbs() {
        Weight w = Weight.lbs(135.0);
        assertEquals(135.0, w.getValue(), 1e-9);
        assertEquals(WeightUnit.LBS, w.getUnit());
    }

    @Test
    void testWeightFactoryZero() {
        Weight w = Weight.zero(WeightUnit.LBS);
        assertEquals(0.0, w.getValue(), 1e-9);
        assertEquals(WeightUnit.LBS, w.getUnit());
    }

    @Test
    void testWeightToKgFromKgReturnsSame() {
        Weight w = Weight.kg(10.0);
        assertSame(w, w.toKg());
    }

    @Test
    void testWeightToKgFromLbs() {
        Weight w = Weight.lbs(1.0);
        Weight kg = w.toKg();
        assertEquals(WeightUnit.KG, kg.getUnit());
        assertEquals(0.45359237, kg.getValue(), 1e-9);
    }

    @Test
    void testWeightToLbsFromLbsReturnsSame() {
        Weight w = Weight.lbs(10.0);
        assertSame(w, w.toLbs());
    }

    @Test
    void testWeightToLbsFromKg() {
        Weight w = Weight.kg(1.0);
        Weight lbs = w.toLbs();
        assertEquals(WeightUnit.LBS, lbs.getUnit());
        assertEquals(1.0 / 0.45359237, lbs.getValue(), 1e-9);
    }

    @Test
    void testWeightPlusSameUnit() {
        Weight a = Weight.kg(10.0);
        Weight b = Weight.kg(5.0);
        Weight result = a.plus(b);
        assertEquals(15.0, result.getValue(), 1e-9);
        assertEquals(WeightUnit.KG, result.getUnit());
    }

    @Test
    void testWeightPlusDifferentUnitKgAsBase() {
        Weight kg = Weight.kg(10.0);
        Weight lbs = Weight.lbs(1.0);
        Weight result = kg.plus(lbs);
        assertEquals(WeightUnit.KG, result.getUnit());
        assertEquals(10.0 + 0.45359237, result.getValue(), 1e-9);
    }

    @Test
    void testWeightPlusDifferentUnitLbsAsBase() {
        Weight lbs = Weight.lbs(10.0);
        Weight kg = Weight.kg(1.0);
        Weight result = lbs.plus(kg);
        assertEquals(WeightUnit.LBS, result.getUnit());
        assertEquals(10.0 + 1.0 / 0.45359237, result.getValue(), 1e-9);
    }

    @Test
    void testWeightPlusNullThrows() {
        assertThrows(NullPointerException.class, () -> Weight.kg(10.0).plus(null));
    }

    @Test
    void testWeightTimes() {
        Weight w = Weight.kg(10.0);
        Weight result = w.times(2.0);
        assertEquals(20.0, result.getValue(), 1e-9);
        assertEquals(WeightUnit.KG, result.getUnit());
    }

    @Test
    void testWeightTimesZero() {
        Weight w = Weight.kg(10.0);
        Weight result = w.times(0.0);
        assertEquals(0.0, result.getValue(), 1e-9);
    }

    @Test
    void testWeightTimesNegativeThrows() {
        assertThrows(IllegalArgumentException.class, () -> Weight.kg(10.0).times(-1.0));
    }

    @Test
    void testWeightCompareTo() {
        Weight a = Weight.kg(10.0);
        Weight b = Weight.kg(20.0);
        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
        assertEquals(0, a.compareTo(Weight.kg(10.0)));
    }

    @Test
    void testWeightCompareToCrossUnit() {
        Weight kg = Weight.kg(1.0);
        Weight lbs = Weight.lbs(2.0);
        assertTrue(kg.compareTo(lbs) > 0);
    }

    @Test
    void testWeightEqualsSameValueSameUnit() {
        assertEquals(Weight.kg(10.0), Weight.kg(10.0));
    }

    @Test
    void testWeightEqualsDifferentValue() {
        assertNotEquals(Weight.kg(10.0), Weight.kg(20.0));
    }

    @Test
    void testWeightEqualsDifferentUnitSameKgValue() {
        double lbsValue = 10.0 / 0.45359237;
        assertEquals(Weight.kg(10.0), Weight.lbs(lbsValue));
    }

    @Test
    void testWeightEqualsNull() {
        assertNotEquals(null, Weight.kg(10.0));
    }

    @Test
    void testWeightHashCodeConsistency() {
        Weight a = Weight.kg(10.0);
        Weight b = Weight.kg(10.0);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testWeightHashCodeCrossUnit() {
        double lbsValue = 10.0 / 0.45359237;
        assertEquals(Weight.kg(10.0).hashCode(), Weight.lbs(lbsValue).hashCode());
    }

    @Test
    void testWeightToString() {
        assertEquals("10.00 kg", Weight.kg(10.0).toString());
        assertEquals("5.50 lbs", Weight.lbs(5.5).toString());
    }

    @Test
    void testDistanceUnitEnumValues() {
        assertEquals(3, DistanceUnit.values().length);
    }

    @Test
    void testDistanceUnitMeters() {
        assertEquals("m", DistanceUnit.METERS.getSymbol());
        assertEquals(1.0, DistanceUnit.METERS.toMetersFactor(), 1e-9);
    }

    @Test
    void testDistanceUnitKilometers() {
        assertEquals("km", DistanceUnit.KILOMETERS.getSymbol());
        assertEquals(1000.0, DistanceUnit.KILOMETERS.toMetersFactor(), 1e-9);
    }

    @Test
    void testDistanceUnitMiles() {
        assertEquals("mi", DistanceUnit.MILES.getSymbol());
        assertEquals(1609.344, DistanceUnit.MILES.toMetersFactor(), 1e-9);
    }

    @Test
    void testDistanceConstructorAndGetters() {
        Distance d = new Distance(5.0, DistanceUnit.KILOMETERS);
        assertEquals(5.0, d.getValue(), 1e-9);
        assertEquals(DistanceUnit.KILOMETERS, d.getUnit());
    }

    @Test
    void testDistanceConstructorNegativeValueThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Distance(-1.0, DistanceUnit.METERS));
    }

    @Test
    void testDistanceConstructorNullUnitThrows() {
        assertThrows(NullPointerException.class, () -> new Distance(10.0, null));
    }

    @Test
    void testDistanceFactoryMeters() {
        Distance d = Distance.meters(100.0);
        assertEquals(100.0, d.getValue(), 1e-9);
        assertEquals(DistanceUnit.METERS, d.getUnit());
    }

    @Test
    void testDistanceFactoryKilometers() {
        Distance d = Distance.kilometers(5.0);
        assertEquals(5.0, d.getValue(), 1e-9);
        assertEquals(DistanceUnit.KILOMETERS, d.getUnit());
    }

    @Test
    void testDistanceFactoryMiles() {
        Distance d = Distance.miles(3.14);
        assertEquals(3.14, d.getValue(), 1e-9);
        assertEquals(DistanceUnit.MILES, d.getUnit());
    }

    @Test
    void testDistanceFactoryZero() {
        Distance d = Distance.zero(DistanceUnit.KILOMETERS);
        assertEquals(0.0, d.getValue(), 1e-9);
        assertEquals(DistanceUnit.KILOMETERS, d.getUnit());
    }

    @Test
    void testDistanceToMetersFromMetersReturnsSame() {
        Distance d = Distance.meters(100.0);
        assertSame(d, d.toMeters());
    }

    @Test
    void testDistanceToMetersFromKilometers() {
        Distance d = Distance.kilometers(1.0);
        Distance m = d.toMeters();
        assertEquals(1000.0, m.getValue(), 1e-9);
        assertEquals(DistanceUnit.METERS, m.getUnit());
    }

    @Test
    void testDistanceToMetersFromMiles() {
        Distance d = Distance.miles(1.0);
        Distance m = d.toMeters();
        assertEquals(1609.344, m.getValue(), 1e-9);
        assertEquals(DistanceUnit.METERS, m.getUnit());
    }

    @Test
    void testDistanceToKilometersFromKilometersReturnsSame() {
        Distance d = Distance.kilometers(1.0);
        assertSame(d, d.toKilometers());
    }

    @Test
    void testDistanceToKilometersFromMeters() {
        Distance d = Distance.meters(5000.0);
        Distance km = d.toKilometers();
        assertEquals(5.0, km.getValue(), 1e-9);
        assertEquals(DistanceUnit.KILOMETERS, km.getUnit());
    }

    @Test
    void testDistanceToKilometersFromMiles() {
        Distance d = Distance.miles(1.0);
        Distance km = d.toKilometers();
        assertEquals(1609.344 / 1000.0, km.getValue(), 1e-9);
        assertEquals(DistanceUnit.KILOMETERS, km.getUnit());
    }

    @Test
    void testDistanceToMilesFromMilesReturnsSame() {
        Distance d = Distance.miles(1.0);
        assertSame(d, d.toMiles());
    }

    @Test
    void testDistanceToMilesFromKilometers() {
        Distance d = Distance.kilometers(1.0);
        Distance mi = d.toMiles();
        assertEquals(1000.0 / 1609.344, mi.getValue(), 1e-9);
        assertEquals(DistanceUnit.MILES, mi.getUnit());
    }

    @Test
    void testDistanceToMilesFromMeters() {
        Distance d = Distance.meters(1609.344);
        Distance mi = d.toMiles();
        assertEquals(1.0, mi.getValue(), 1e-9);
        assertEquals(DistanceUnit.MILES, mi.getUnit());
    }

    @Test
    void testDistancePlusSameUnit() {
        Distance a = Distance.kilometers(5.0);
        Distance b = Distance.kilometers(3.0);
        Distance result = a.plus(b);
        assertEquals(8.0, result.getValue(), 1e-9);
        assertEquals(DistanceUnit.KILOMETERS, result.getUnit());
    }

    @Test
    void testDistancePlusMetersAsBase() {
        Distance m = Distance.meters(1000.0);
        Distance km = Distance.kilometers(1.0);
        Distance result = m.plus(km);
        assertEquals(2000.0, result.getValue(), 1e-9);
        assertEquals(DistanceUnit.METERS, result.getUnit());
    }

    @Test
    void testDistancePlusKilometersAsBase() {
        Distance km = Distance.kilometers(1.0);
        Distance m = Distance.meters(1000.0);
        Distance result = km.plus(m);
        assertEquals(2.0, result.getValue(), 1e-9);
        assertEquals(DistanceUnit.KILOMETERS, result.getUnit());
    }

    @Test
    void testDistancePlusMilesAsBase() {
        Distance mi = Distance.miles(1.0);
        Distance m = Distance.meters(1609.344);
        Distance result = mi.plus(m);
        assertEquals(2.0, result.getValue(), 1e-9);
        assertEquals(DistanceUnit.MILES, result.getUnit());
    }

    @Test
    void testDistancePlusNullThrows() {
        assertThrows(NullPointerException.class, () -> Distance.meters(10.0).plus(null));
    }

    @Test
    void testDistanceCompareTo() {
        Distance a = Distance.meters(100.0);
        Distance b = Distance.meters(200.0);
        assertTrue(a.compareTo(b) < 0);
        assertEquals(0, a.compareTo(Distance.meters(100.0)));
    }

    @Test
    void testDistanceCompareToCrossUnit() {
        Distance km = Distance.kilometers(1.0);
        Distance m = Distance.meters(500.0);
        assertTrue(km.compareTo(m) > 0);
    }

    @Test
    void testDistanceEqualsSameValueSameUnit() {
        assertEquals(Distance.kilometers(5.0), Distance.kilometers(5.0));
    }

    @Test
    void testDistanceEqualsCrossUnit() {
        assertEquals(Distance.kilometers(1.0), Distance.meters(1000.0));
    }

    @Test
    void testDistanceEqualsDifferentValue() {
        assertNotEquals(Distance.kilometers(1.0), Distance.kilometers(2.0));
    }

    @Test
    void testDistanceEqualsNull() {
        assertNotEquals(null, Distance.kilometers(1.0));
    }

    @Test
    void testDistanceHashCodeConsistency() {
        assertEquals(Distance.kilometers(1.0).hashCode(), Distance.meters(1000.0).hashCode());
    }

    @Test
    void testDistanceToString() {
        assertEquals("5.00 km", Distance.kilometers(5.0).toString());
        assertEquals("100.00 m", Distance.meters(100.0).toString());
        assertEquals("3.14 mi", Distance.miles(3.14).toString());
    }

    @Test
    void testDurationConstructorAndGetters() {
        Duration d = new Duration(3661);
        assertEquals(3661, d.getTotalSeconds());
        assertEquals(61, d.getMinutes());
        assertEquals(1, d.getHours());
    }

    @Test
    void testDurationConstructorNegativeThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Duration(-1));
    }

    @Test
    void testDurationFactorySeconds() {
        Duration d = Duration.seconds(90);
        assertEquals(90, d.getTotalSeconds());
    }

    @Test
    void testDurationFactoryMinutes() {
        Duration d = Duration.minutes(5);
        assertEquals(300, d.getTotalSeconds());
    }

    @Test
    void testDurationFactoryHours() {
        Duration d = Duration.hours(2);
        assertEquals(7200, d.getTotalSeconds());
    }

    @Test
    void testDurationFactoryZero() {
        Duration d = Duration.zero();
        assertEquals(0, d.getTotalSeconds());
        assertEquals(0, d.getMinutes());
        assertEquals(0, d.getHours());
    }

    @Test
    void testDurationGetMinutes() {
        assertEquals(1, Duration.seconds(90).getMinutes());
        assertEquals(0, Duration.seconds(59).getMinutes());
    }

    @Test
    void testDurationGetHours() {
        assertEquals(1, Duration.seconds(3600).getHours());
        assertEquals(0, Duration.seconds(3599).getHours());
    }

    @Test
    void testDurationPlus() {
        Duration a = Duration.seconds(100);
        Duration b = Duration.seconds(200);
        assertEquals(300, a.plus(b).getTotalSeconds());
    }

    @Test
    void testDurationPlusNullThrows() {
        assertThrows(NullPointerException.class, () -> Duration.seconds(10).plus(null));
    }

    @Test
    void testDurationMinus() {
        Duration a = Duration.seconds(300);
        Duration b = Duration.seconds(100);
        assertEquals(200, a.minus(b).getTotalSeconds());
    }

    @Test
    void testDurationMinusNegativeThrows() {
        assertThrows(IllegalArgumentException.class, () -> Duration.seconds(100).minus(Duration.seconds(200)));
    }

    @Test
    void testDurationMinusNullThrows() {
        assertThrows(NullPointerException.class, () -> Duration.seconds(10).minus(null));
    }

    @Test
    void testDurationMinusExactZero() {
        assertEquals(0, Duration.seconds(100).minus(Duration.seconds(100)).getTotalSeconds());
    }

    @Test
    void testDurationToFormattedStringMinutesOnly() {
        assertEquals("01:30", Duration.seconds(90).toFormattedString());
    }

    @Test
    void testDurationToFormattedStringWithHours() {
        assertEquals("01:01:01", Duration.seconds(3661).toFormattedString());
    }

    @Test
    void testDurationToFormattedStringZero() {
        assertEquals("00:00", Duration.zero().toFormattedString());
    }

    @Test
    void testDurationToFormattedStringExactMinute() {
        assertEquals("01:00", Duration.seconds(60).toFormattedString());
    }

    @Test
    void testDurationToFormattedStringExactHour() {
        assertEquals("01:00:00", Duration.seconds(3600).toFormattedString());
    }

    @Test
    void testDurationCompareTo() {
        assertTrue(Duration.seconds(100).compareTo(Duration.seconds(200)) < 0);
        assertEquals(0, Duration.seconds(100).compareTo(Duration.seconds(100)));
        assertTrue(Duration.seconds(200).compareTo(Duration.seconds(100)) > 0);
    }

    @Test
    void testDurationEqualsSameSeconds() {
        assertEquals(Duration.seconds(60), Duration.minutes(1));
    }

    @Test
    void testDurationEqualsDifferentSeconds() {
        assertNotEquals(Duration.seconds(60), Duration.seconds(61));
    }

    @Test
    void testDurationEqualsNull() {
        assertNotEquals(null, Duration.seconds(10));
    }

    @Test
    void testDurationHashCodeConsistency() {
        assertEquals(Duration.seconds(100).hashCode(), Duration.minutes(1).plus(Duration.seconds(40)).hashCode());
    }

    @Test
    void testDurationToString() {
        assertEquals("01:30", Duration.seconds(90).toString());
    }

    @Test
    void testPaceConstructorAndGetters() {
        Pace p = new Pace(300, DistanceUnit.KILOMETERS);
        assertEquals(300, p.getSecondsPerUnit());
        assertEquals(DistanceUnit.KILOMETERS, p.getUnit());
    }

    @Test
    void testPaceConstructorNegativeThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Pace(-1, DistanceUnit.KILOMETERS));
    }

    @Test
    void testPaceConstructorNullUnitThrows() {
        assertThrows(NullPointerException.class, () -> new Pace(300, null));
    }

    @Test
    void testPaceConstructorZeroAllowed() {
        Pace p = new Pace(0, DistanceUnit.KILOMETERS);
        assertEquals(0, p.getSecondsPerUnit());
    }

    @Test
    void testPaceFrom() {
        Pace p = Pace.from(Distance.kilometers(1.0), Duration.seconds(300));
        assertEquals(300, p.getSecondsPerUnit());
        assertEquals(DistanceUnit.KILOMETERS, p.getUnit());
    }

    @Test
    void testPaceFromZeroDistance() {
        Pace p = Pace.from(Distance.kilometers(0.0), Duration.seconds(100));
        assertEquals(0, p.getSecondsPerUnit());
        assertEquals(DistanceUnit.KILOMETERS, p.getUnit());
    }

    @Test
    void testPaceFromRoundsToNearest() {
        Pace p = Pace.from(Distance.kilometers(1.0), Duration.seconds(330));
        assertEquals(330, p.getSecondsPerUnit());
        Pace p2 = Pace.from(Distance.kilometers(1.0), Duration.seconds(250));
        assertEquals(250, p2.getSecondsPerUnit());
    }

    @Test
    void testPaceFromNullDistanceThrows() {
        assertThrows(NullPointerException.class, () -> Pace.from(null, Duration.seconds(100)));
    }

    @Test
    void testPaceFromNullDurationThrows() {
        assertThrows(NullPointerException.class, () -> Pace.from(Distance.kilometers(1.0), null));
    }

    @Test
    void testPaceFromMiles() {
        Pace p = Pace.from(Distance.miles(1.0), Duration.seconds(480));
        assertEquals(480, p.getSecondsPerUnit());
        assertEquals(DistanceUnit.MILES, p.getUnit());
    }

    @Test
    void testPaceToFormattedString() {
        assertEquals("05:00 min/km", Pace.from(Distance.kilometers(1.0), Duration.seconds(300)).toFormattedString());
    }

    @Test
    void testPaceToFormattedStringMiles() {
        assertEquals("08:00 min/mi", Pace.from(Distance.miles(1.0), Duration.seconds(480)).toFormattedString());
    }

    @Test
    void testPaceToFormattedStringZero() {
        assertEquals("00:00 min/km", new Pace(0, DistanceUnit.KILOMETERS).toFormattedString());
    }

    @Test
    void testPaceEquals() {
        assertEquals(Pace.from(Distance.kilometers(1.0), Duration.seconds(300)),
                Pace.from(Distance.kilometers(1.0), Duration.seconds(300)));
    }

    @Test
    void testPaceEqualsDifferentSeconds() {
        assertNotEquals(Pace.from(Distance.kilometers(1.0), Duration.seconds(300)),
                Pace.from(Distance.kilometers(1.0), Duration.seconds(310)));
    }

    @Test
    void testPaceEqualsDifferentUnit() {
        assertNotEquals(new Pace(300, DistanceUnit.KILOMETERS), new Pace(300, DistanceUnit.MILES));
    }

    @Test
    void testPaceEqualsNull() {
        assertNotEquals(null, Pace.from(Distance.kilometers(1.0), Duration.seconds(300)));
    }

    @Test
    void testPaceHashCodeConsistency() {
        Pace a = Pace.from(Distance.kilometers(1.0), Duration.seconds(300));
        Pace b = Pace.from(Distance.kilometers(1.0), Duration.seconds(300));
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testPaceToString() {
        assertEquals("05:00 min/km", Pace.from(Distance.kilometers(1.0), Duration.seconds(300)).toString());
    }

    @Test
    void testRpeConstructorAndGetters() {
        Rpe r = Rpe.of(7.5);
        assertEquals(7.5, r.getValue(), 1e-9);
    }

    @Test
    void testRpeConstructorTooLowThrows() {
        assertThrows(IllegalArgumentException.class, () -> Rpe.of(0.9));
    }

    @Test
    void testRpeConstructorTooHighThrows() {
        assertThrows(IllegalArgumentException.class, () -> Rpe.of(10.1));
    }

    @Test
    void testRpeConstructorBoundaryMin() {
        assertEquals(1.0, Rpe.of(1.0).getValue(), 1e-9);
    }

    @Test
    void testRpeConstructorBoundaryMax() {
        assertEquals(10.0, Rpe.of(10.0).getValue(), 1e-9);
    }

    @Test
    void testRpeGetRir() {
        assertEquals(3.0, Rpe.of(7.0).getRir(), 1e-9);
        assertEquals(0.0, Rpe.of(10.0).getRir(), 1e-9);
    }

    @Test
    void testRpeIsWarmup() {
        assertTrue(Rpe.of(5.9).isWarmup());
        assertFalse(Rpe.of(6.0).isWarmup());
        assertFalse(Rpe.of(7.0).isWarmup());
    }

    @Test
    void testRpeIsEffectiveSet() {
        assertFalse(Rpe.of(6.9).isEffectiveSet());
        assertTrue(Rpe.of(7.0).isEffectiveSet());
        assertTrue(Rpe.of(10.0).isEffectiveSet());
    }

    @Test
    void testRpeIsMaxEffort() {
        assertFalse(Rpe.of(9.4).isMaxEffort());
        assertTrue(Rpe.of(9.5).isMaxEffort());
        assertTrue(Rpe.of(10.0).isMaxEffort());
    }

    @Test
    void testRpeCompareTo() {
        assertTrue(Rpe.of(5.0).compareTo(Rpe.of(7.0)) < 0);
        assertEquals(0, Rpe.of(7.0).compareTo(Rpe.of(7.0)));
        assertTrue(Rpe.of(8.0).compareTo(Rpe.of(7.0)) > 0);
    }

    @Test
    void testRpeEquals() {
        assertEquals(Rpe.of(7.0), Rpe.of(7.0));
    }

    @Test
    void testRpeNotEquals() {
        assertNotEquals(Rpe.of(7.0), Rpe.of(8.0));
    }

    @Test
    void testRpeEqualsNull() {
        assertNotEquals(null, Rpe.of(7.0));
    }

    @Test
    void testRpeHashCodeConsistency() {
        assertEquals(Rpe.of(7.0).hashCode(), Rpe.of(7.0).hashCode());
    }

    @Test
    void testRpeToString() {
        assertEquals("@RPE 7.5 (RIR 2.5)", Rpe.of(7.5).toString());
    }

    @Test
    void testRpeToStringMax() {
        assertEquals("@RPE 10.0 (RIR 0.0)", Rpe.of(10.0).toString());
    }

    @Test
    void testRpeToStringMin() {
        assertEquals("@RPE 1.0 (RIR 9.0)", Rpe.of(1.0).toString());
    }

    @Test
    void testTempoConstructorAndGetters() {
        Tempo t = new Tempo(3, 1, 1, 0);
        assertEquals(3, t.getEccentricSeconds());
        assertEquals(1, t.getPauseAfterEccentricSeconds());
        assertEquals(1, t.getConcentricSeconds());
        assertEquals(0, t.getPauseAfterConcentricSeconds());
    }

    @Test
    void testTempoConstructorNegativeThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Tempo(-1, 1, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Tempo(1, -1, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Tempo(1, 1, -1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Tempo(1, 1, 1, -1));
    }

    @Test
    void testTempoParse() {
        Tempo t = Tempo.parse("3-1-1-0");
        assertEquals(3, t.getEccentricSeconds());
        assertEquals(1, t.getPauseAfterEccentricSeconds());
        assertEquals(1, t.getConcentricSeconds());
        assertEquals(0, t.getPauseAfterConcentricSeconds());
    }

    @Test
    void testTempoParseWithX() {
        Tempo t = Tempo.parse("2-X-1-X");
        assertEquals(2, t.getEccentricSeconds());
        assertEquals(0, t.getPauseAfterEccentricSeconds());
        assertEquals(1, t.getConcentricSeconds());
        assertEquals(0, t.getPauseAfterConcentricSeconds());
    }

    @Test
    void testTempoParseLowerCaseX() {
        Tempo t = Tempo.parse("2-x-1-x");
        assertEquals(2, t.getEccentricSeconds());
        assertEquals(0, t.getPauseAfterEccentricSeconds());
    }

    @Test
    void testTempoParseNullThrows() {
        assertThrows(NullPointerException.class, () -> Tempo.parse(null));
    }

    @Test
    void testTempoParseWrongPartCountThrows() {
        assertThrows(IllegalArgumentException.class, () -> Tempo.parse("3-1-1"));
        assertThrows(IllegalArgumentException.class, () -> Tempo.parse("3-1-1-0-1"));
        assertThrows(IllegalArgumentException.class, () -> Tempo.parse("3-1"));
    }

    @Test
    void testTempoParseNonNumericThrows() {
        assertThrows(IllegalArgumentException.class, () -> Tempo.parse("a-1-1-0"));
    }

    @Test
    void testTempoParseWithTrimmedX() {
        Tempo t = Tempo.parse("2-x-1-x");
        assertEquals(0, t.getPauseAfterEccentricSeconds());
    }

    @Test
    void testTempoGetTotalRepDuration() {
        assertEquals(5, new Tempo(3, 1, 1, 0).getTotalRepDuration());
        assertEquals(0, new Tempo(0, 0, 0, 0).getTotalRepDuration());
    }

    @Test
    void testTempoToFormattedString() {
        assertEquals("3-1-1-0", new Tempo(3, 1, 1, 0).toFormattedString());
        assertEquals("2-0-1-0", new Tempo(2, 0, 1, 0).toFormattedString());
    }

    @Test
    void testTempoEquals() {
        assertEquals(new Tempo(3, 1, 1, 0), new Tempo(3, 1, 1, 0));
    }

    @Test
    void testTempoNotEquals() {
        assertNotEquals(new Tempo(3, 1, 1, 0), new Tempo(3, 1, 2, 0));
    }

    @Test
    void testTempoNotEqualsDifferentPauseEccentric() {
        assertNotEquals(new Tempo(3, 1, 1, 0), new Tempo(3, 2, 1, 0));
    }

    @Test
    void testTempoNotEqualsDifferentConcentric() {
        assertNotEquals(new Tempo(3, 1, 1, 0), new Tempo(3, 1, 2, 1));
    }

    @Test
    void testTempoNotEqualsDifferentPauseConcentric() {
        assertNotEquals(new Tempo(3, 1, 1, 0), new Tempo(3, 1, 1, 5));
    }

    @Test
    void testTempoEqualsNull() {
        assertNotEquals(null, new Tempo(3, 1, 1, 0));
    }

    @Test
    void testTempoHashCodeConsistency() {
        assertEquals(new Tempo(3, 1, 1, 0).hashCode(), new Tempo(3, 1, 1, 0).hashCode());
    }

    @Test
    void testTempoToString() {
        assertEquals("3-1-1-0", Tempo.parse("3-1-1-0").toString());
    }

    @Test
    void testHeartRateZoneEnumValues() {
        assertEquals(5, HeartRateZone.values().length);
    }

    @Test
    void testHeartRateZone1() {
        assertEquals("Active Recovery", HeartRateZone.ZONE_1.getDisplayName());
        assertEquals(0.50, HeartRateZone.ZONE_1.getMinPercentage(), 1e-9);
        assertEquals(0.60, HeartRateZone.ZONE_1.getMaxPercentage(), 1e-9);
        assertNotNull(HeartRateZone.ZONE_1.getDescription());
    }

    @Test
    void testHeartRateZone2() {
        assertEquals("Aerobic Base", HeartRateZone.ZONE_2.getDisplayName());
        assertEquals(0.60, HeartRateZone.ZONE_2.getMinPercentage(), 1e-9);
        assertEquals(0.70, HeartRateZone.ZONE_2.getMaxPercentage(), 1e-9);
        assertNotNull(HeartRateZone.ZONE_2.getDescription());
    }

    @Test
    void testHeartRateZone3() {
        assertEquals("Tempo / Aerobic Endurance", HeartRateZone.ZONE_3.getDisplayName());
        assertEquals(0.70, HeartRateZone.ZONE_3.getMinPercentage(), 1e-9);
        assertEquals(0.80, HeartRateZone.ZONE_3.getMaxPercentage(), 1e-9);
    }

    @Test
    void testHeartRateZone4() {
        assertEquals("Lactate Threshold", HeartRateZone.ZONE_4.getDisplayName());
        assertEquals(0.80, HeartRateZone.ZONE_4.getMinPercentage(), 1e-9);
        assertEquals(0.90, HeartRateZone.ZONE_4.getMaxPercentage(), 1e-9);
    }

    @Test
    void testHeartRateZone5() {
        assertEquals("Neuromuscular / Anaerobic", HeartRateZone.ZONE_5.getDisplayName());
        assertEquals(0.90, HeartRateZone.ZONE_5.getMinPercentage(), 1e-9);
        assertEquals(1.00, HeartRateZone.ZONE_5.getMaxPercentage(), 1e-9);
    }

    @Test
    void testHeartRateZoneFromHeartRateZone1() {
        assertEquals(HeartRateZone.ZONE_1, HeartRateZone.fromHeartRate(119, 200));
    }

    @Test
    void testHeartRateZoneFromHeartRateZone1Boundary() {
        assertEquals(HeartRateZone.ZONE_1, HeartRateZone.fromHeartRate(119, 200));
    }

    @Test
    void testHeartRateZoneFromHeartRateZone2AtBoundary() {
        assertEquals(HeartRateZone.ZONE_2, HeartRateZone.fromHeartRate(120, 200));
    }

    @Test
    void testHeartRateZoneFromHeartRateZone2BelowBoundary() {
        assertEquals(HeartRateZone.ZONE_2, HeartRateZone.fromHeartRate(139, 200));
    }

    @Test
    void testHeartRateZoneFromHeartRateZone3AtBoundary() {
        assertEquals(HeartRateZone.ZONE_3, HeartRateZone.fromHeartRate(140, 200));
    }

    @Test
    void testHeartRateZoneFromHeartRateZone3BelowBoundary() {
        assertEquals(HeartRateZone.ZONE_3, HeartRateZone.fromHeartRate(159, 200));
    }

    @Test
    void testHeartRateZoneFromHeartRateZone4AtBoundary() {
        assertEquals(HeartRateZone.ZONE_4, HeartRateZone.fromHeartRate(160, 200));
    }

    @Test
    void testHeartRateZoneFromHeartRateZone4BelowBoundary() {
        assertEquals(HeartRateZone.ZONE_4, HeartRateZone.fromHeartRate(179, 200));
    }

    @Test
    void testHeartRateZoneFromHeartRateZone5AtBoundary() {
        assertEquals(HeartRateZone.ZONE_5, HeartRateZone.fromHeartRate(180, 200));
    }

    @Test
    void testHeartRateZoneFromHeartRateZone5Max() {
        assertEquals(HeartRateZone.ZONE_5, HeartRateZone.fromHeartRate(200, 200));
    }

    @Test
    void testHeartRateZoneFromHeartRateMaxHrZeroThrows() {
        assertThrows(IllegalArgumentException.class, () -> HeartRateZone.fromHeartRate(100, 0));
    }

    @Test
    void testHeartRateZoneFromHeartRateMaxHrNegativeThrows() {
        assertThrows(IllegalArgumentException.class, () -> HeartRateZone.fromHeartRate(100, -1));
    }
}
