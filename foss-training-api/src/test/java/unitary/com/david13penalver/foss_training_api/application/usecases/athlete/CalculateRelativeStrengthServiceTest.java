package unitary.com.david13penalver.foss_training_api.application.usecases.athlete;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.david13penalver.foss_training_api.application.usecases.athlete.impl.CalculateRelativeStrengthService;
import com.david13penalver.foss_training_api.domain.model.athlete.Gender;
import com.david13penalver.foss_training_api.domain.model.athlete.RelativeStrengthScore;

class CalculateRelativeStrengthServiceTest {

    private CalculateRelativeStrengthService service;

    @BeforeEach
    void setUp() {
        service = new CalculateRelativeStrengthService();
    }

    @Test
    void execute_maleStandardLifter_calculatesAccurateScores() {
        // e.g. 500kg total @ 80kg bodyweight for Male
        RelativeStrengthScore score = service.execute(500.0, 80.0, Gender.MALE);

        assertNotNull(score);
        assertEquals(500.0, score.totalWeightKg());
        assertEquals(80.0, score.bodyweightKg());
        assertEquals(Gender.MALE, score.gender());
        assertEquals(6.25, score.relativeStrengthRatio());
        assertTrue(score.dotsScore() > 300.0 && score.dotsScore() < 400.0, "DOTS score should be in reasonable range");
        assertTrue(score.wilksScore() > 300.0 && score.wilksScore() < 400.0, "Wilks score should be in reasonable range");
        assertEquals("Proficient", score.classification());
    }

    @Test
    void execute_femaleLifter_calculatesUsingFemaleCoefficients() {
        // e.g. 350kg total @ 60kg bodyweight for Female
        RelativeStrengthScore score = service.execute(350.0, 60.0, Gender.FEMALE);

        assertNotNull(score);
        assertEquals(5.83, score.relativeStrengthRatio());
        assertTrue(score.dotsScore() > 350.0, "Female DOTS score should reflect female formula");
        assertTrue(score.wilksScore() > 350.0, "Female Wilks score should reflect female formula");
    }

    @Test
    void execute_defaultGender_defaultsToMale() {
        RelativeStrengthScore score = service.execute(400.0, 80.0, null);
        assertNotNull(score);
        assertEquals(Gender.MALE, score.gender());
        assertEquals(5.0, score.relativeStrengthRatio());
    }

    @Test
    void execute_nullOrNegativeTotal_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> service.execute(null, 80.0, Gender.MALE));
        assertThrows(IllegalArgumentException.class, () -> service.execute(0.0, 80.0, Gender.MALE));
        assertThrows(IllegalArgumentException.class, () -> service.execute(-50.0, 80.0, Gender.MALE));
    }

    @Test
    void execute_nullOrNegativeBodyweight_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> service.execute(500.0, null, Gender.MALE));
        assertThrows(IllegalArgumentException.class, () -> service.execute(500.0, 0.0, Gender.MALE));
        assertThrows(IllegalArgumentException.class, () -> service.execute(500.0, -80.0, Gender.MALE));
    }

    @Test
    void classification_tiers_coverAllRanges() {
        // Very low DOTS -> Novice
        RelativeStrengthScore novice = service.execute(200.0, 90.0, Gender.MALE);
        assertEquals("Novice", novice.classification());

        // High DOTS -> Elite or World-Class
        RelativeStrengthScore elite = service.execute(850.0, 80.0, Gender.MALE);
        assertTrue(elite.classification().equals("Elite") || elite.classification().equals("World-Class"));
    }
}
