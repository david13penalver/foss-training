package com.david13penalver.foss_training_api.infrastructure.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.domain.model.common.Distance;
import com.david13penalver.foss_training_api.domain.model.common.DistanceUnit;
import com.david13penalver.foss_training_api.domain.model.common.Duration;
import com.david13penalver.foss_training_api.domain.model.common.Pace;
import com.david13penalver.foss_training_api.domain.model.common.Rpe;
import com.david13penalver.foss_training_api.domain.model.common.Weight;
import com.david13penalver.foss_training_api.domain.model.common.WeightUnit;
import com.david13penalver.foss_training_api.domain.model.exercise.DifficultyLevel;
import com.david13penalver.foss_training_api.domain.model.exercise.Equipment;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.exercise.ExerciseCategory;
import com.david13penalver.foss_training_api.domain.model.exercise.MovementPattern;
import com.david13penalver.foss_training_api.domain.model.exercise.endurance.EnduranceMetrics;
import com.david13penalver.foss_training_api.domain.model.exercise.endurance.EnduranceType;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.Joint;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MobilityMetrics;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.MobilityType;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.RecommendedTiming;
import com.david13penalver.foss_training_api.domain.model.exercise.mobility.StretchType;
import com.david13penalver.foss_training_api.domain.model.exercise.resistance.MuscleGroup;
import com.david13penalver.foss_training_api.domain.model.exercise.resistance.ResistanceMetrics;
import com.david13penalver.foss_training_api.domain.model.session.EnduranceInterval;
import com.david13penalver.foss_training_api.domain.model.session.EnduranceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.MobilitySessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.MobilitySet;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSessionExercise;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSet;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.domain.model.session.SessionStatusEnum;
import com.david13penalver.foss_training_api.domain.model.session.SetType;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.ExerciseRepository;
import com.david13penalver.foss_training_api.domain.ports.out.session.SessionRepository;

import lombok.RequiredArgsConstructor;

/**
 * Seeds initial exercise catalog and workout session templates on application startup.
 * Guarded by {@code @Profile("!test")} and {@code @ConditionalOnProperty} to ensure zero test interference.
 */
@Component
@Profile("!test")
@ConditionalOnProperty(name = "app.seeder.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class DatabaseDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseDataSeeder.class);

    private final ExerciseRepository exerciseRepository;
    private final SessionRepository sessionRepository;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Starting database seed data initialization...");

        Map<String, Exercise> exercisesByName = seedExercises();
        seedSessionTemplates(exercisesByName);

        log.info("Database seed data initialization completed successfully.");
    }

    private Map<String, Exercise> seedExercises() {
        Map<String, Exercise> existing = exerciseRepository.findAll().stream()
                .filter(e -> e.getName() != null)
                .collect(Collectors.toMap(e -> e.getName().trim().toLowerCase(), e -> e, (a, b) -> a));

        List<Exercise> toSeed = createInitialExercisesCatalog();
        int addedCount = 0;

        for (Exercise ex : toSeed) {
            String key = ex.getName().trim().toLowerCase();
            if (!existing.containsKey(key)) {
                Exercise saved = exerciseRepository.save(ex);
                existing.put(key, saved);
                addedCount++;
                log.debug("Seeded exercise: {}", saved.getName());
            }
        }

        log.info("Exercise seeding finished. Added {} new exercises, total catalog size: {}", addedCount, existing.size());
        return existing;
    }

    private void seedSessionTemplates(Map<String, Exercise> exercises) {
        Map<String, Session> existing = sessionRepository.findAll().stream()
                .filter(s -> s.getName() != null)
                .collect(Collectors.toMap(s -> s.getName().trim().toLowerCase(), s -> s, (a, b) -> a));

        List<Session> templates = createInitialSessionTemplates(exercises);
        int addedCount = 0;

        for (Session session : templates) {
            String key = session.getName().trim().toLowerCase();
            if (!existing.containsKey(key)) {
                Session saved = sessionRepository.save(session);
                existing.put(key, saved);
                addedCount++;
                log.debug("Seeded session template: {}", saved.getName());
            }
        }

        log.info("Session templates seeding finished. Added {} new templates, total sessions: {}", addedCount, existing.size());
    }

    private List<Exercise> createInitialExercisesCatalog() {
        List<Exercise> list = new ArrayList<>();

        // ==================== RESISTANCE: CHEST & PUSH ====================
        list.add(createResistance(
                "Barbell Flat Bench Press",
                "Classic horizontal compound push targeting the pectoral muscles, anterior deltoids, and triceps.",
                DifficultyLevel.INTERMEDIATE,
                List.of(Equipment.BARBELL, Equipment.BENCH),
                List.of(MuscleGroup.CHEST),
                List.of(MuscleGroup.TRICEPS, MuscleGroup.SHOULDERS),
                MovementPattern.PUSH,
                4, 6, 8, 120, "2-0-1-0", 20.0, 250.0, 2.5,
                List.of(
                        "Lie flat on the bench with eyes positioned directly under the barbell.",
                        "Grip the bar slightly wider than shoulder width with feet planted firmly on the floor.",
                        "Unrack with locked elbows, draw shoulders back and down into the bench.",
                        "Lower the bar under control until it lightly touches mid-sternum.",
                        "Press powerfully upward in a slight backward arc to return to starting position."
                ),
                List.of("Flaring elbows at 90 degrees putting stress on rotator cuff", "Bouncing the bar off the ribcage", "Lifting hips off the bench surface"),
                List.of("Always bench inside a power rack or use safety spotter arms", "Keep wrists neutral and directly under the bar"),
                List.of("chest", "bench", "push", "compound", "hypertrophy", "strength")
        ));

        list.add(createResistance(
                "Incline Dumbbell Press",
                "Upper chest and clavicular head builder performed on a 30 to 45 degree inclined bench.",
                DifficultyLevel.INTERMEDIATE,
                List.of(Equipment.DUMBBELL, Equipment.BENCH),
                List.of(MuscleGroup.CHEST, MuscleGroup.SHOULDERS),
                List.of(MuscleGroup.TRICEPS),
                MovementPattern.PUSH,
                3, 8, 12, 90, "3-1-1-0", 10.0, 60.0, 2.0,
                List.of(
                        "Set the incline bench to a 30 to 45-degree angle.",
                        "Sit down with dumbbells resting upright on knees.",
                        "Kick weights back toward shoulders and lean into position.",
                        "Press dumbbells upward in a slight converging arc without banging them at top.",
                        "Lower steadily until upper arms reach parallel with torso."
                ),
                List.of("Excessive bench incline turning the exercise into an overhead press", "Arching lower back excessively off the back pad"),
                List.of("Keep scapulae retracted and core braced throughout the set"),
                List.of("chest", "incline", "dumbbells", "upper chest", "push")
        ));

        list.add(createResistance(
                "Cable Chest Fly",
                "Continuous tension chest isolation exercise maximizing peak contraction and stretch across sternal pectoralis fibers.",
                DifficultyLevel.BEGINNER,
                List.of(Equipment.CABLE_MACHINE),
                List.of(MuscleGroup.CHEST),
                List.of(MuscleGroup.SHOULDERS),
                MovementPattern.ISOLATION,
                3, 12, 15, 60, "2-1-1-1", 5.0, 50.0, 2.5,
                List.of(
                        "Set cable pulleys at chest height with single D-handles.",
                        "Step forward into a staggered stance with a slight forward torso lean.",
                        "Bring handles together in a wide hugging arc until hands meet at midline.",
                        "Squeeze pectorals hard at peak contraction for one second.",
                        "Return slowly along the same path feeling a deep chest stretch."
                ),
                List.of("Bending elbows too much turning it into a press", "Using body sway to bring cables together"),
                List.of("Maintain slight bend in elbows to protect the biceps tendon"),
                List.of("chest", "fly", "cables", "isolation", "hypertrophy")
        ));

        // ==================== RESISTANCE: BACK & PULL ====================
        list.add(createResistance(
                "Conventional Barbell Deadlift",
                "Premier whole-body hinge movement forging massive posterior chain strength and back thickness.",
                DifficultyLevel.ADVANCED,
                List.of(Equipment.BARBELL),
                List.of(MuscleGroup.LOWER_BACK, MuscleGroup.HAMSTRINGS, MuscleGroup.GLUTES),
                List.of(MuscleGroup.LATS, MuscleGroup.UPPER_BACK, MuscleGroup.FOREARMS, MuscleGroup.CORE),
                MovementPattern.HINGE,
                4, 4, 6, 180, "2-1-1-0", 40.0, 320.0, 5.0,
                List.of(
                        "Stand with feet hip-width apart, barbell placed over mid-foot.",
                        "Hinge at the hips and grip the barbell immediately outside your shins.",
                        "Pull your chest proud, wedge hips into position, and take out the bar slack.",
                        "Drive the floor away extending knees and hips simultaneously until standing upright.",
                        "Hinge hips back and descend the barbell in reverse order."
                ),
                List.of("Rounding the lumbar spine under heavy loads", "Letting the barbell drift away from shins", "Hyperextending spine backward at lockout"),
                List.of("Maintain maximum intra-abdominal pressure using the Valsalva maneuver", "Keep lats engaged like squeezing oranges in armpits"),
                List.of("deadlift", "back", "posterior chain", "hinge", "strength", "power")
        ));

        list.add(createResistance(
                "Pull-Up",
                "Gold-standard upper body vertical pulling movement developing latissimus width and upper back strength.",
                DifficultyLevel.INTERMEDIATE,
                List.of(Equipment.PULL_UP_BAR, Equipment.BODYWEIGHT),
                List.of(MuscleGroup.LATS),
                List.of(MuscleGroup.BICEPS, MuscleGroup.UPPER_BACK, MuscleGroup.FOREARMS),
                MovementPattern.PULL,
                4, 6, 10, 90, "2-0-1-1", 0.0, 50.0, 2.5,
                List.of(
                        "Hang from pull-up bar with an overhand grip slightly wider than shoulder width.",
                        "Initiate movement by depressing and retracting shoulder blades downward.",
                        "Pull elbows toward hips until chin cleanly clears the bar.",
                        "Lower yourself under complete control back to a dead hang position."
                ),
                List.of("Kipping or swinging legs to generate artificial momentum", "Half-repping without full elbow extension at the bottom"),
                List.of("Avoid hyperextending cervical spine to reach chin over bar"),
                List.of("back", "lats", "pullup", "bodyweight", "pull", "calisthenics")
        ));

        list.add(createResistance(
                "Barbell Bent-Over Row",
                "Heavy horizontal pulling compound building dense middle back, rhomboid, and latissimus thickness.",
                DifficultyLevel.INTERMEDIATE,
                List.of(Equipment.BARBELL),
                List.of(MuscleGroup.UPPER_BACK, MuscleGroup.LATS),
                List.of(MuscleGroup.BICEPS, MuscleGroup.LOWER_BACK, MuscleGroup.FOREARMS),
                MovementPattern.PULL,
                4, 8, 10, 90, "2-1-1-0", 20.0, 160.0, 2.5,
                List.of(
                        "Hinge forward at the hips to approximately 45 degrees with a flat, rigid spine.",
                        "Grip barbell slightly wider than shoulder-width with pronated hands.",
                        "Pull the bar smoothly into your lower ribcage, driving elbows past your back.",
                        "Squeeze shoulder blades together forcefully, then lower the bar under control."
                ),
                List.of("Jerking torso upwards using hip extension momentum", "Allowing upper back to round excessively"),
                List.of("Keep core braced and neck in neutral alignment throughout the set"),
                List.of("back", "row", "pull", "rhomboids", "hypertrophy", "compound")
        ));

        // ==================== RESISTANCE: SHOULDERS ====================
        list.add(createResistance(
                "Standing Overhead Barbell Press",
                "Foundational vertical press forging powerful deltoids, triceps, and overhead functional stability.",
                DifficultyLevel.INTERMEDIATE,
                List.of(Equipment.BARBELL),
                List.of(MuscleGroup.SHOULDERS),
                List.of(MuscleGroup.TRICEPS, MuscleGroup.UPPER_BACK, MuscleGroup.CORE),
                MovementPattern.PUSH,
                4, 6, 8, 120, "2-0-1-0", 20.0, 120.0, 2.5,
                List.of(
                        "Rack bar at collarbone height. Grip just outside shoulder width with vertical forearms.",
                        "Squeeze glutes and brace abdominals tightly.",
                        "Press the bar vertically upward, tilting head back slightly to clear the chin.",
                        "Lock out overhead with barbell centered directly over mid-foot and spine.",
                        "Lower under control back to clavicular rest position."
                ),
                List.of("Arching lower back excessively to turn movement into an incline press", "Pressing bar forward rather than straight overhead"),
                List.of("Keep glutes tightly squeezed throughout each repetition to protect the lumbar spine"),
                List.of("shoulders", "ohp", "overhead", "press", "delts", "compound")
        ));

        list.add(createResistance(
                "Dumbbell Lateral Raise",
                "Strict lateral deltoid isolation creating broad shoulder width and distinct deltoid cap aesthetics.",
                DifficultyLevel.BEGINNER,
                List.of(Equipment.DUMBBELL),
                List.of(MuscleGroup.SHOULDERS),
                List.of(MuscleGroup.UPPER_BACK),
                MovementPattern.ISOLATION,
                4, 12, 15, 60, "2-1-1-1", 4.0, 30.0, 1.0,
                List.of(
                        "Stand with dumbbells resting at your sides with a slight forward torso hinge.",
                        "Maintain a soft bend in the elbows.",
                        "Raise arms outward along the scapular plane (30 degrees forward) to shoulder height.",
                        "Pause briefly at top, then lower steadily under full eccentric tension."
                ),
                List.of("Using hip sway or body bouncing to launch weights upward", "Shrugging traps up toward ears"),
                List.of("Prioritize strict technique and tempo over heavy poundage"),
                List.of("shoulders", "lateral raise", "delts", "isolation", "hypertrophy")
        ));

        list.add(createResistance(
                "Face Pull",
                "Essential rear deltoid and rotator cuff exercise improving postural alignment and shoulder joint longevity.",
                DifficultyLevel.BEGINNER,
                List.of(Equipment.CABLE_MACHINE),
                List.of(MuscleGroup.SHOULDERS, MuscleGroup.UPPER_BACK),
                List.of(MuscleGroup.BICEPS),
                MovementPattern.PULL,
                3, 15, 20, 60, "2-2-1-1", 10.0, 45.0, 2.5,
                List.of(
                        "Attach rope to high pulley setting on cable station.",
                        "Grip rope ends with thumbs pointed backward.",
                        "Step back, pull rope toward eye level while simultaneously externally rotating wrists.",
                        "Hold contraction for two seconds feeling rear delts and mid-traps engage.",
                        "Extend arms back to starting position smoothly."
                ),
                List.of("Pulling rope downward toward chest rather than up to eye level", "Leaning back excessively to use bodyweight"),
                List.of("Emphasize external rotation of the hands at the peak of the pull"),
                List.of("shoulders", "rear delts", "cable", "posture", "rehab", "prehab")
        ));

        // ==================== RESISTANCE: LEGS ====================
        list.add(createResistance(
                "Barbell Back Squat",
                "Primary lower-body compound movement developing quadriceps, glutes, core stability, and total-body power.",
                DifficultyLevel.INTERMEDIATE,
                List.of(Equipment.BARBELL, Equipment.POWER_RACK),
                List.of(MuscleGroup.QUADRICEPS, MuscleGroup.GLUTES),
                List.of(MuscleGroup.HAMSTRINGS, MuscleGroup.CALVES, MuscleGroup.CORE),
                MovementPattern.SQUAT,
                4, 6, 8, 180, "3-1-1-0", 20.0, 300.0, 2.5,
                List.of(
                        "Set barbell at upper chest height in the squat rack. Step under and position bar across upper traps.",
                        "Grip bar firmly, step back, and set feet shoulder-width apart with toes flared slightly outward.",
                        "Take a deep breath into abdomen, brace core, and descend by breaking at hips and knees simultaneously.",
                        "Descend until hip crease is below top of knees while maintaining neutral upright spine.",
                        "Drive feet firmly through the floor to ascend back to full hip and knee lockout."
                ),
                List.of("Knees caving inward (valgus collapse)", "Heels lifting off the floor during the descent", "Rounding lower back at bottom of squat"),
                List.of("Always use safety spotter arms adjusted to just below your lowest depth", "Maintain constant intra-abdominal pressure throughout"),
                List.of("legs", "squat", "quads", "glutes", "power", "compound", "strength")
        ));

        list.add(createResistance(
                "Romanian Deadlift",
                "Hip-hinge staple emphasizing eccentric stretch and profound hypertrophy in hamstrings and glutes.",
                DifficultyLevel.INTERMEDIATE,
                List.of(Equipment.BARBELL),
                List.of(MuscleGroup.HAMSTRINGS, MuscleGroup.GLUTES),
                List.of(MuscleGroup.LOWER_BACK, MuscleGroup.FOREARMS),
                MovementPattern.HINGE,
                3, 8, 10, 90, "3-1-1-0", 30.0, 200.0, 2.5,
                List.of(
                        "Hold barbell at hip height with an overhand grip, feet hip-width apart.",
                        "Initiate movement by pushing hips backward while keeping knees soft with slight bend.",
                        "Glide barbell down close along thighs and shins until a deep hamstring stretch is felt.",
                        "Drive hips forward powerfully, squeezing glutes to return to the top."
                ),
                List.of("Squatting down by bending knees excessively", "Allowing barbell to drift forward away from legs"),
                List.of("Stop descending once hips stop moving backward to prevent lower spine flexion"),
                List.of("legs", "hamstrings", "glutes", "rdl", "hinge", "hypertrophy")
        ));

        list.add(createResistance(
                "Bulgarian Split Squat",
                "High-intensity unilateral leg movement building quad size, glute development, and balance.",
                DifficultyLevel.INTERMEDIATE,
                List.of(Equipment.DUMBBELL, Equipment.BENCH),
                List.of(MuscleGroup.QUADRICEPS, MuscleGroup.GLUTES),
                List.of(MuscleGroup.HAMSTRINGS, MuscleGroup.CALVES),
                MovementPattern.LUNGE,
                3, 8, 12, 90, "3-0-1-0", 8.0, 40.0, 2.0,
                List.of(
                        "Place top of rear foot laces-down on a bench behind you.",
                        "Step forward with lead working leg into a comfortable lunge stance.",
                        "Lower your body vertically until back knee hovers an inch above the floor.",
                        "Drive firmly through the entire lead foot to return to the upright position."
                ),
                List.of("Front heel lifting off the floor during the descent", "Excessive forward torso collapse"),
                List.of("Practice with bodyweight first to determine ideal foot positioning before loading"),
                List.of("legs", "quads", "glutes", "unilateral", "split squat")
        ));

        list.add(createResistance(
                "Lying Leg Curl",
                "Direct knee flexion isolation targeting all heads of the hamstring complex through full range of motion.",
                DifficultyLevel.BEGINNER,
                List.of(Equipment.LYING_LEG_CURL),
                List.of(MuscleGroup.HAMSTRINGS),
                List.of(MuscleGroup.CALVES),
                MovementPattern.ISOLATION,
                3, 10, 12, 60, "3-0-1-1", 15.0, 95.0, 2.5,
                List.of(
                        "Lie prone on machine with roller pad positioned just below calf muscles.",
                        "Grasp handles firmly and press hips flat into the bench pad.",
                        "Curl weight upward smoothly toward glutes through full knee flexion.",
                        "Control weight eccentrically back down to full knee extension."
                ),
                List.of("Lifting hips off the pad to generate momentum during the curl"),
                List.of("Align machine pivot axis with knee joint line for smooth kinematics"),
                List.of("legs", "hamstrings", "isolation", "machine")
        ));

        list.add(createResistance(
                "Standing Calf Raise",
                "Full range of motion gastrocnemius developer emphasizing deep ankle dorsiflexion stretch.",
                DifficultyLevel.BEGINNER,
                List.of(Equipment.STANDING_CALF_RAISE),
                List.of(MuscleGroup.CALVES),
                List.of(MuscleGroup.HIP_FLEXORS),
                MovementPattern.ISOLATION,
                4, 12, 15, 60, "2-2-1-1", 20.0, 150.0, 5.0,
                List.of(
                        "Place balls of feet securely on edge of block with shoulder pads resting snugly.",
                        "Lower heels down into a deep calf stretch below block level.",
                        "Hold bottom stretch position for two full seconds to eliminate Achilles bounce.",
                        "Press through big toes into full plantarflexion at top and squeeze calves."
                ),
                List.of("Bouncing quickly at the bottom using Achilles tendon elasticity"),
                List.of("Pause at both top and bottom for maximum hypertrophy stimulation"),
                List.of("calves", "legs", "isolation", "machine")
        ));

        // ==================== RESISTANCE: ARMS & CORE ====================
        list.add(createResistance(
                "Barbell Biceps Curl",
                "Foundational mass builder for the biceps brachii, brachialis, and forearm flexors.",
                DifficultyLevel.BEGINNER,
                List.of(Equipment.BARBELL),
                List.of(MuscleGroup.BICEPS),
                List.of(MuscleGroup.FOREARMS),
                MovementPattern.ISOLATION,
                3, 8, 12, 60, "2-0-1-0", 15.0, 70.0, 2.5,
                List.of(
                        "Stand tall holding barbell with an underhand shoulder-width grip.",
                        "Keep elbows pinned at sides of ribcage.",
                        "Curl barbell upward in an arc toward upper chest while flexing biceps.",
                        "Squeeze at top contraction, then lower under steady control."
                ),
                List.of("Swinging upper body backward to cheat the weight up", "Elbows drifting forward excessively"),
                List.of("Maintain completely stationary torso and tight glutes"),
                List.of("arms", "biceps", "curl", "isolation", "hypertrophy")
        ));

        list.add(createResistance(
                "Triceps Rope Pushdown",
                "Cable pushdown maximizing triceps lateral and medial head isolation with split lockout.",
                DifficultyLevel.BEGINNER,
                List.of(Equipment.CABLE_MACHINE),
                List.of(MuscleGroup.TRICEPS),
                List.of(MuscleGroup.FOREARMS),
                MovementPattern.ISOLATION,
                3, 12, 15, 60, "2-0-1-1", 10.0, 60.0, 2.5,
                List.of(
                        "Attach rope to high cable pulley. Stand with slight forward torso lean.",
                        "Pin elbows against sides of ribcage.",
                        "Extend arms downward and spread rope ends outward at bottom lockout.",
                        "Hold contraction for one second, then let cable rise until elbows reach 90 degrees."
                ),
                List.of("Allowing elbows to flare outward or drift forward during the set"),
                List.of("Lock wrists firmly to prevent tendon irritation"),
                List.of("arms", "triceps", "cable", "isolation", "hypertrophy")
        ));

        list.add(createResistance(
                "Hanging Leg Raise",
                "High-level core movement targeting lower rectus abdominis through posterior pelvic tilt.",
                DifficultyLevel.ADVANCED,
                List.of(Equipment.PULL_UP_BAR, Equipment.BODYWEIGHT),
                List.of(MuscleGroup.ABS),
                List.of(MuscleGroup.HIP_FLEXORS, MuscleGroup.FOREARMS, MuscleGroup.CORE),
                MovementPattern.ISOLATION,
                3, 10, 15, 60, "2-1-1-1", 0.0, 20.0, 2.5,
                List.of(
                        "Hang from pull-up bar with straight arms and depressed shoulders.",
                        "Roll pelvis upward and raise straight legs until parallel to the floor or higher.",
                        "Hold the top crunch contraction momentarily.",
                        "Lower legs under strict control without allowing torso to swing."
                ),
                List.of("Using swinging momentum rather than abdominal curling strength"),
                List.of("Engage lats to stabilize torso and keep body from swinging back and forth"),
                List.of("core", "abs", "hanging", "bodyweight", "calisthenics")
        ));

        // ==================== ENDURANCE ====================
        list.add(createEndurance(
                "5K Outdoor Road Run",
                "Aerobic baseline distance run developing cardiovascular stroke volume and running economy.",
                DifficultyLevel.INTERMEDIATE,
                List.of(Equipment.BODYWEIGHT),
                EnduranceType.AEROBIC,
                1, 1, 5000.0, 1500, 150,
                List.of(
                        "Warm up with 5 minutes of dynamic lower limb drills and easy jogging.",
                        "Establish a steady Zone 2/3 aerobic pace with conversational breathing.",
                        "Keep cadence near 170 to 180 strides per minute with light ground contact.",
                        "Finish with 5 minutes of gentle walking cool-down and stretching."
                ),
                List.of("Hydrate adequately and wear quality running footwear with appropriate arch support"),
                List.of("cardio", "running", "aerobic", "endurance", "5k", "outdoor")
        ));

        list.add(createEndurance(
                "HIIT Assault Bike Sprints",
                "All-out anaerobic interval protocol inducing rapid VO2max improvement and metabolic conditioning.",
                DifficultyLevel.ADVANCED,
                List.of(Equipment.ASSAULT_BIKE),
                EnduranceType.HIIT,
                1, 8, 0.0, 20, 175,
                List.of(
                        "Pedal easily for 3 minutes to warm up knees, hips, and shoulders.",
                        "Sprint at 100% maximal effort for 20 seconds driving arms and legs furiously.",
                        "Coast slowly or rest for 40 seconds.",
                        "Repeat for 8 grueling intervals."
                ),
                List.of("Adjust saddle height so a 10 to 15 degree knee bend remains at bottom of stroke"),
                List.of("cardio", "hiit", "sprints", "bike", "anaerobic", "conditioning")
        ));

        list.add(createEndurance(
                "2000m Rowing Ergometer Time Trial",
                "Full-body aerobic benchmark challenging cardiorespiratory output and mental resilience.",
                DifficultyLevel.ADVANCED,
                List.of(Equipment.ROWING_MACHINE),
                EnduranceType.AEROBIC,
                1, 1, 2000.0, 450, 168,
                List.of(
                        "Strap feet securely into footplates with strap across widest part of shoe.",
                        "Drive sequence: push forcefully with legs, open hips, then draw handle to lower sternum.",
                        "Recovery sequence: extend arms, swing hips forward, then slide seat up smoothly.",
                        "Maintain steady 500m split times across the full 2000-meter trial."
                ),
                List.of("Keep lumbar spine flat during the catch position to avoid back strain"),
                List.of("cardio", "rowing", "ergometer", "aerobic", "full body")
        ));

        // ==================== MOBILITY ====================
        list.add(createMobility(
                "World's Greatest Stretch",
                "Multi-planar dynamic mobility drill mobilizing hips, thoracic spine, hamstrings, and ankles.",
                DifficultyLevel.BEGINNER,
                List.of(Equipment.YOGA_MAT, Equipment.BODYWEIGHT),
                MobilityType.DYNAMIC_MOBILITY,
                StretchType.DYNAMIC,
                List.of(Joint.HIP, Joint.THORACIC_SPINE, Joint.ANKLE),
                RecommendedTiming.PRE_WORKOUT,
                true, 45, 5, 3,
                List.of(
                        "Step into a long lunge with back knee off the floor and leg straight.",
                        "Place both hands inside the lead foot on the mat.",
                        "Drop lead elbow toward instep of lead foot to open hip.",
                        "Rotate chest toward front knee, reaching arm straight up to the ceiling.",
                        "Place hand down, push hips up and back into a hamstring pike.",
                        "Step forward and switch to opposite leg."
                ),
                List.of("Breathe deeply into each position without rushing the rotational phase"),
                List.of("mobility", "hips", "thoracic", "warmup", "dynamic", "full body")
        ));

        list.add(createMobility(
                "Hip 90/90 Flow",
                "Internal and external hip rotation drill restoring acetabulofemoral capsule range and pelvic mobility.",
                DifficultyLevel.INTERMEDIATE,
                List.of(Equipment.YOGA_MAT, Equipment.BODYWEIGHT),
                MobilityType.DYNAMIC_MOBILITY,
                StretchType.DYNAMIC,
                List.of(Joint.HIP),
                RecommendedTiming.PRE_WORKOUT,
                true, 60, 5, 3,
                List.of(
                        "Sit on floor with lead leg bent 90 degrees in front and trail leg bent 90 degrees at your side.",
                        "Sit tall through spine and hinge forward from hips over the front shin.",
                        "Hold stretch for several deep breaths.",
                        "Pivot smoothly on heels opening knees wide to switch sides without hands if possible."
                ),
                List.of("Ensure rotation comes from hip joints, not by torquing the knee joint"),
                List.of("mobility", "hips", "90-90", "prehab", "rotation")
        ));

        list.add(createMobility(
                "Cat-Cow Spine Flow",
                "Gentle spinal articulation movement improving segmental mobility across cervical, thoracic, and lumbar spine.",
                DifficultyLevel.BEGINNER,
                List.of(Equipment.YOGA_MAT, Equipment.BODYWEIGHT),
                MobilityType.DYNAMIC_MOBILITY,
                StretchType.DYNAMIC,
                List.of(Joint.THORACIC_SPINE, Joint.LUMBAR_SPINE, Joint.NECK),
                RecommendedTiming.DAILY,
                false, 5, 10, 2,
                List.of(
                        "Begin on hands and knees with wrists under shoulders and knees under hips.",
                        "Inhale: drop belly toward mat, lift chest, gaze upward (Cow pose).",
                        "Exhale: arch back upward toward ceiling, tuck tailbone and chin (Cat pose).",
                        "Flow fluidly between poses matching movement to continuous rhythmic breathing."
                ),
                List.of("Move mindfully vertebra by vertebra rather than dumping force into lower back"),
                List.of("mobility", "spine", "back", "daily", "warmup", "yoga")
        ));

        list.add(createMobility(
                "Shoulder Dislocates with Band",
                "Dynamic shoulder girdle drill opening pectorals, anterior deltoids, and optimizing scapular rhythm.",
                DifficultyLevel.BEGINNER,
                List.of(Equipment.RESISTANCE_BAND),
                MobilityType.DYNAMIC_MOBILITY,
                StretchType.DYNAMIC,
                List.of(Joint.SHOULDER),
                RecommendedTiming.PRE_WORKOUT,
                true, 2, 12, 3,
                List.of(
                        "Hold resistance band in front of hips with a wide overhand grip.",
                        "Keep elbows straight and core braced.",
                        "Raise band overhead and continue arc smoothly until it reaches behind hips.",
                        "Reverse motion back to starting position without bending elbows."
                ),
                List.of("Keep hands wide enough to allow symmetrical movement without shrugging"),
                List.of("mobility", "shoulders", "band", "warmup", "rotator cuff")
        ));

        list.add(createMobility(
                "Pigeon Pose",
                "Deep static stretch relieving tightness in deep gluteal rotators, piriformis, and hip capsule.",
                DifficultyLevel.INTERMEDIATE,
                List.of(Equipment.YOGA_MAT, Equipment.BODYWEIGHT),
                MobilityType.STATIC_STRETCHING,
                StretchType.STATIC,
                List.of(Joint.HIP),
                RecommendedTiming.POST_WORKOUT,
                true, 60, 1, 2,
                List.of(
                        "From high plank, bring right knee forward behind right wrist, angling shin diagonally.",
                        "Extend left leg straight back with top of foot flat on floor.",
                        "Square hips toward the floor and lower torso forward over front shin onto forearms.",
                        "Breathe deeply for 60 seconds, then slowly press up and switch sides."
                ),
                List.of("Flex the front foot slightly to protect the knee joint from lateral torque"),
                List.of("mobility", "glutes", "hips", "piriformis", "static", "post-workout")
        ));

        return list;
    }

    private List<Session> createInitialSessionTemplates(Map<String, Exercise> exMap) {
        List<Session> templates = new ArrayList<>();

        // 1. PUSH DAY TEMPLATE
        Exercise benchPress = exMap.get("barbell flat bench press");
        Exercise inclineDbPress = exMap.get("incline dumbbell press");
        Exercise ohp = exMap.get("standing overhead barbell press");
        Exercise lateralRaise = exMap.get("dumbbell lateral raise");
        Exercise tricepsPushdown = exMap.get("triceps rope pushdown");

        if (benchPress != null && inclineDbPress != null && ohp != null && lateralRaise != null && tricepsPushdown != null) {
            Session pushDay = new Session();
            pushDay.setName("Push Day: Hypertrophy & Pressing");
            pushDay.setDescription("Comprehensive upper body pressing routine focusing on chest mass, shoulder strength, and triceps hypertrophy.");
            pushDay.setSessionStatus(SessionStatusEnum.PLANNED);

            ResistanceSessionExercise seBench = new ResistanceSessionExercise();
            seBench.setExercise(benchPress);
            seBench.setNotes("Focus on controlled descent and explosive press");
            seBench.addSet(new ResistanceSet(1, SetType.WARMUP, new Weight(50.0, WeightUnit.KG), 10, new Rpe(6.0), 90));
            seBench.addSet(new ResistanceSet(2, SetType.WORKING, new Weight(80.0, WeightUnit.KG), 8, new Rpe(8.0), 120));
            seBench.addSet(new ResistanceSet(3, SetType.WORKING, new Weight(80.0, WeightUnit.KG), 8, new Rpe(8.5), 120));
            seBench.addSet(new ResistanceSet(4, SetType.WORKING, new Weight(82.5, WeightUnit.KG), 6, new Rpe(9.0), 150));
            pushDay.addExercise(seBench);

            ResistanceSessionExercise seIncline = new ResistanceSessionExercise();
            seIncline.setExercise(inclineDbPress);
            seIncline.setNotes("30-degree incline, deep pectoral stretch");
            seIncline.addSet(new ResistanceSet(1, SetType.WORKING, new Weight(28.0, WeightUnit.KG), 10, new Rpe(8.0), 90));
            seIncline.addSet(new ResistanceSet(2, SetType.WORKING, new Weight(28.0, WeightUnit.KG), 10, new Rpe(8.5), 90));
            seIncline.addSet(new ResistanceSet(3, SetType.WORKING, new Weight(30.0, WeightUnit.KG), 8, new Rpe(9.0), 90));
            pushDay.addExercise(seIncline);

            ResistanceSessionExercise seOhp = new ResistanceSessionExercise();
            seOhp.setExercise(ohp);
            seOhp.setNotes("Strict standing form, locked glutes");
            seOhp.addSet(new ResistanceSet(1, SetType.WORKING, new Weight(50.0, WeightUnit.KG), 8, new Rpe(8.0), 120));
            seOhp.addSet(new ResistanceSet(2, SetType.WORKING, new Weight(50.0, WeightUnit.KG), 8, new Rpe(8.5), 120));
            seOhp.addSet(new ResistanceSet(3, SetType.WORKING, new Weight(52.5, WeightUnit.KG), 6, new Rpe(9.0), 120));
            pushDay.addExercise(seOhp);

            ResistanceSessionExercise seLat = new ResistanceSessionExercise();
            seLat.setExercise(lateralRaise);
            seLat.setNotes("Strict lateral deltoid isolation, no body swing");
            seLat.addSet(new ResistanceSet(1, SetType.WORKING, new Weight(12.0, WeightUnit.KG), 15, new Rpe(8.0), 60));
            seLat.addSet(new ResistanceSet(2, SetType.WORKING, new Weight(12.0, WeightUnit.KG), 14, new Rpe(8.5), 60));
            seLat.addSet(new ResistanceSet(3, SetType.WORKING, new Weight(12.0, WeightUnit.KG), 12, new Rpe(9.0), 60));
            seLat.addSet(new ResistanceSet(4, SetType.DROP_SET, new Weight(8.0, WeightUnit.KG), 15, new Rpe(9.5), 60));
            pushDay.addExercise(seLat);

            ResistanceSessionExercise seTri = new ResistanceSessionExercise();
            seTri.setExercise(tricepsPushdown);
            seTri.setNotes("Flare rope outward at bottom lockout");
            seTri.addSet(new ResistanceSet(1, SetType.WORKING, new Weight(25.0, WeightUnit.KG), 12, new Rpe(8.0), 60));
            seTri.addSet(new ResistanceSet(2, SetType.WORKING, new Weight(27.5, WeightUnit.KG), 10, new Rpe(8.5), 60));
            seTri.addSet(new ResistanceSet(3, SetType.WORKING, new Weight(27.5, WeightUnit.KG), 10, new Rpe(9.0), 60));
            pushDay.addExercise(seTri);

            templates.add(pushDay);
        }

        // 2. PULL DAY TEMPLATE
        Exercise deadlift = exMap.get("conventional barbell deadlift");
        Exercise pullup = exMap.get("pull-up");
        Exercise row = exMap.get("barbell bent-over row");
        Exercise facepull = exMap.get("face pull");
        Exercise curl = exMap.get("barbell biceps curl");

        if (deadlift != null && pullup != null && row != null && facepull != null && curl != null) {
            Session pullDay = new Session();
            pullDay.setName("Pull Day: Posterior Strength & Back");
            pullDay.setDescription("High-yield posterior chain and vertical/horizontal pulling workout targeting lats, deadlift power, and biceps.");
            pullDay.setSessionStatus(SessionStatusEnum.PLANNED);

            ResistanceSessionExercise seDeadlift = new ResistanceSessionExercise();
            seDeadlift.setExercise(deadlift);
            seDeadlift.setNotes("Brace tightly before each pull");
            seDeadlift.addSet(new ResistanceSet(1, SetType.WARMUP, new Weight(70.0, WeightUnit.KG), 8, new Rpe(6.0), 120));
            seDeadlift.addSet(new ResistanceSet(2, SetType.WARMUP, new Weight(110.0, WeightUnit.KG), 5, new Rpe(7.0), 120));
            seDeadlift.addSet(new ResistanceSet(3, SetType.WORKING, new Weight(140.0, WeightUnit.KG), 5, new Rpe(8.0), 180));
            seDeadlift.addSet(new ResistanceSet(4, SetType.WORKING, new Weight(145.0, WeightUnit.KG), 5, new Rpe(8.5), 180));
            seDeadlift.addSet(new ResistanceSet(5, SetType.WORKING, new Weight(150.0, WeightUnit.KG), 4, new Rpe(9.0), 180));
            pullDay.addExercise(seDeadlift);

            ResistanceSessionExercise sePullup = new ResistanceSessionExercise();
            sePullup.setExercise(pullup);
            sePullup.setNotes("Full dead hang to chin over bar");
            sePullup.addSet(new ResistanceSet(1, SetType.WORKING, new Weight(0.0, WeightUnit.KG), 10, new Rpe(8.0), 90));
            sePullup.addSet(new ResistanceSet(2, SetType.WORKING, new Weight(0.0, WeightUnit.KG), 8, new Rpe(8.5), 90));
            sePullup.addSet(new ResistanceSet(3, SetType.WORKING, new Weight(0.0, WeightUnit.KG), 8, new Rpe(9.0), 90));
            pullDay.addExercise(sePullup);

            ResistanceSessionExercise seRow = new ResistanceSessionExercise();
            seRow.setExercise(row);
            seRow.setNotes("Torso at 45 degrees, pull to lower sternum");
            seRow.addSet(new ResistanceSet(1, SetType.WORKING, new Weight(70.0, WeightUnit.KG), 10, new Rpe(8.0), 90));
            seRow.addSet(new ResistanceSet(2, SetType.WORKING, new Weight(75.0, WeightUnit.KG), 8, new Rpe(8.5), 90));
            seRow.addSet(new ResistanceSet(3, SetType.WORKING, new Weight(75.0, WeightUnit.KG), 8, new Rpe(9.0), 90));
            pullDay.addExercise(seRow);

            ResistanceSessionExercise seFace = new ResistanceSessionExercise();
            seFace.setExercise(facepull);
            seFace.setNotes("2-second hold at eye level with external rotation");
            seFace.addSet(new ResistanceSet(1, SetType.WORKING, new Weight(20.0, WeightUnit.KG), 15, new Rpe(8.0), 60));
            seFace.addSet(new ResistanceSet(2, SetType.WORKING, new Weight(22.5, WeightUnit.KG), 15, new Rpe(8.5), 60));
            seFace.addSet(new ResistanceSet(3, SetType.WORKING, new Weight(22.5, WeightUnit.KG), 15, new Rpe(9.0), 60));
            pullDay.addExercise(seFace);

            ResistanceSessionExercise seCurl = new ResistanceSessionExercise();
            seCurl.setExercise(curl);
            seCurl.setNotes("Strict bicep curls, elbows pinned");
            seCurl.addSet(new ResistanceSet(1, SetType.WORKING, new Weight(32.5, WeightUnit.KG), 10, new Rpe(8.0), 60));
            seCurl.addSet(new ResistanceSet(2, SetType.WORKING, new Weight(35.0, WeightUnit.KG), 8, new Rpe(8.5), 60));
            seCurl.addSet(new ResistanceSet(3, SetType.WORKING, new Weight(35.0, WeightUnit.KG), 8, new Rpe(9.0), 60));
            pullDay.addExercise(seCurl);

            templates.add(pullDay);
        }

        // 3. LEGS & CORE POWER
        Exercise squat = exMap.get("barbell back squat");
        Exercise rdl = exMap.get("romanian deadlift");
        Exercise splitSquat = exMap.get("bulgarian split squat");
        Exercise legCurl = exMap.get("lying leg curl");
        Exercise legRaise = exMap.get("hanging leg raise");

        if (squat != null && rdl != null && splitSquat != null && legCurl != null && legRaise != null) {
            Session legsDay = new Session();
            legsDay.setName("Legs & Core Power Routine");
            legsDay.setDescription("Lower body strength and hypertrophy workout combining heavy squats, hinge patterns, unilateral volume, and core.");
            legsDay.setSessionStatus(SessionStatusEnum.PLANNED);

            ResistanceSessionExercise seSquat = new ResistanceSessionExercise();
            seSquat.setExercise(squat);
            seSquat.setNotes("Deep squat depth below parallel");
            seSquat.addSet(new ResistanceSet(1, SetType.WARMUP, new Weight(60.0, WeightUnit.KG), 8, new Rpe(6.0), 120));
            seSquat.addSet(new ResistanceSet(2, SetType.WORKING, new Weight(100.0, WeightUnit.KG), 6, new Rpe(8.0), 180));
            seSquat.addSet(new ResistanceSet(3, SetType.WORKING, new Weight(105.0, WeightUnit.KG), 6, new Rpe(8.5), 180));
            seSquat.addSet(new ResistanceSet(4, SetType.WORKING, new Weight(110.0, WeightUnit.KG), 5, new Rpe(9.0), 180));
            legsDay.addExercise(seSquat);

            ResistanceSessionExercise seRdl = new ResistanceSessionExercise();
            seRdl.setExercise(rdl);
            seRdl.setNotes("Feel intense stretch in hamstrings");
            seRdl.addSet(new ResistanceSet(1, SetType.WORKING, new Weight(90.0, WeightUnit.KG), 10, new Rpe(8.0), 120));
            seRdl.addSet(new ResistanceSet(2, SetType.WORKING, new Weight(95.0, WeightUnit.KG), 8, new Rpe(8.5), 120));
            seRdl.addSet(new ResistanceSet(3, SetType.WORKING, new Weight(100.0, WeightUnit.KG), 8, new Rpe(9.0), 120));
            legsDay.addExercise(seRdl);

            ResistanceSessionExercise seSplit = new ResistanceSessionExercise();
            seSplit.setExercise(splitSquat);
            seSplit.setNotes("Drive through front foot, torso tall");
            seSplit.addSet(new ResistanceSet(1, SetType.WORKING, new Weight(18.0, WeightUnit.KG), 10, new Rpe(8.0), 90));
            seSplit.addSet(new ResistanceSet(2, SetType.WORKING, new Weight(20.0, WeightUnit.KG), 10, new Rpe(8.5), 90));
            seSplit.addSet(new ResistanceSet(3, SetType.WORKING, new Weight(22.0, WeightUnit.KG), 8, new Rpe(9.0), 90));
            legsDay.addExercise(seSplit);

            ResistanceSessionExercise seCurl = new ResistanceSessionExercise();
            seCurl.setExercise(legCurl);
            seCurl.setNotes("Strict knee flexion, squeeze glutes to keep hips down");
            seCurl.addSet(new ResistanceSet(1, SetType.WORKING, new Weight(45.0, WeightUnit.KG), 12, new Rpe(8.0), 60));
            seCurl.addSet(new ResistanceSet(2, SetType.WORKING, new Weight(50.0, WeightUnit.KG), 10, new Rpe(8.5), 60));
            seCurl.addSet(new ResistanceSet(3, SetType.WORKING, new Weight(50.0, WeightUnit.KG), 10, new Rpe(9.0), 60));
            legsDay.addExercise(seCurl);

            ResistanceSessionExercise seRaise = new ResistanceSessionExercise();
            seRaise.setExercise(legRaise);
            seRaise.setNotes("Posterior pelvic tilt without swinging");
            seRaise.addSet(new ResistanceSet(1, SetType.WORKING, new Weight(0.0, WeightUnit.KG), 12, new Rpe(8.0), 60));
            seRaise.addSet(new ResistanceSet(2, SetType.WORKING, new Weight(0.0, WeightUnit.KG), 12, new Rpe(8.5), 60));
            seRaise.addSet(new ResistanceSet(3, SetType.WORKING, new Weight(0.0, WeightUnit.KG), 10, new Rpe(9.0), 60));
            legsDay.addExercise(seRaise);

            templates.add(legsDay);
        }

        // 4. CONDITIONING & ENDURANCE
        Exercise rowErg = exMap.get("2000m rowing ergometer time trial");
        Exercise assaultBike = exMap.get("hiit assault bike sprints");
        Exercise roadRun = exMap.get("5k outdoor road run");

        if (rowErg != null && assaultBike != null && roadRun != null) {
            Session condSession = new Session();
            condSession.setName("Conditioning & Endurance Engine");
            condSession.setDescription("Cardiovascular endurance conditioning combining rowing benchmark, assault bike interval bursts, and aerobic volume.");
            condSession.setSessionStatus(SessionStatusEnum.PLANNED);

            EnduranceSessionExercise seRow = new EnduranceSessionExercise();
            seRow.setExercise(rowErg);
            seRow.setNotes("Pace target: 1:52 / 500m split");
            seRow.addInterval(new EnduranceInterval(1, new Distance(2000.0, DistanceUnit.METERS), Duration.seconds(450), new Pace(112, DistanceUnit.KILOMETERS), 165, 178, 280.0, 28.0, 180));
            condSession.addExercise(seRow);

            EnduranceSessionExercise seBike = new EnduranceSessionExercise();
            seBike.setExercise(assaultBike);
            seBike.setNotes("20s max effort, 40s active recovery");
            seBike.addInterval(new EnduranceInterval(1, null, Duration.seconds(20), null, 160, 172, 450.0, 75.0, 40));
            seBike.addInterval(new EnduranceInterval(2, null, Duration.seconds(20), null, 165, 175, 460.0, 76.0, 40));
            seBike.addInterval(new EnduranceInterval(3, null, Duration.seconds(20), null, 168, 178, 440.0, 74.0, 40));
            seBike.addInterval(new EnduranceInterval(4, null, Duration.seconds(20), null, 170, 180, 430.0, 72.0, 40));
            condSession.addExercise(seBike);

            EnduranceSessionExercise seRun = new EnduranceSessionExercise();
            seRun.setExercise(roadRun);
            seRun.setNotes("Zone 2 aerobic recovery pace");
            seRun.addInterval(new EnduranceInterval(1, new Distance(5000.0, DistanceUnit.METERS), Duration.seconds(1500), new Pace(300, DistanceUnit.KILOMETERS), 150, 162, null, 175.0, 0));
            condSession.addExercise(seRun);

            templates.add(condSession);
        }

        // 5. MOBILITY & PREHAB FLOW
        Exercise catCow = exMap.get("cat-cow spine flow");
        Exercise wgs = exMap.get("world's greatest stretch");
        Exercise hip90 = exMap.get("hip 90/90 flow");
        Exercise bandDislocates = exMap.get("shoulder dislocates with band");
        Exercise pigeon = exMap.get("pigeon pose");

        if (catCow != null && wgs != null && hip90 != null && bandDislocates != null && pigeon != null) {
            Session mobilitySession = new Session();
            mobilitySession.setName("Full Body Prehab & Mobility Flow");
            mobilitySession.setDescription("Comprehensive joint mobilization, spinal decompression, and hip opener protocol for recovery days and pre-workout priming.");
            mobilitySession.setSessionStatus(SessionStatusEnum.PLANNED);

            MobilitySessionExercise seCatCow = new MobilitySessionExercise();
            seCatCow.setExercise(catCow);
            seCatCow.setNotes("Smooth segmental spinal articulation with breath");
            seCatCow.addSet(new MobilitySet(1, Duration.seconds(5), 10, false));
            seCatCow.addSet(new MobilitySet(2, Duration.seconds(5), 10, false));
            mobilitySession.addExercise(seCatCow);

            MobilitySessionExercise seWgs = new MobilitySessionExercise();
            seWgs.setExercise(wgs);
            seWgs.setNotes("Dynamic lunging, thoracic twist, and hamstring pike");
            seWgs.addSet(new MobilitySet(1, Duration.seconds(45), 5, true));
            seWgs.addSet(new MobilitySet(2, Duration.seconds(45), 5, true));
            seWgs.addSet(new MobilitySet(3, Duration.seconds(45), 5, true));
            mobilitySession.addExercise(seWgs);

            MobilitySessionExercise seHip = new MobilitySessionExercise();
            seHip.setExercise(hip90);
            seHip.setNotes("Internal and external hip rotation transitions");
            seHip.addSet(new MobilitySet(1, Duration.seconds(60), 5, true));
            seHip.addSet(new MobilitySet(2, Duration.seconds(60), 5, true));
            mobilitySession.addExercise(seHip);

            MobilitySessionExercise seBand = new MobilitySessionExercise();
            seBand.setExercise(bandDislocates);
            seBand.setNotes("Arms straight, smooth circular overhead range");
            seBand.addSet(new MobilitySet(1, Duration.seconds(2), 12, true));
            seBand.addSet(new MobilitySet(2, Duration.seconds(2), 12, true));
            mobilitySession.addExercise(seBand);

            MobilitySessionExercise sePigeon = new MobilitySessionExercise();
            sePigeon.setExercise(pigeon);
            sePigeon.setNotes("Deep glute and piriformis static stretch");
            sePigeon.addSet(new MobilitySet(1, Duration.seconds(60), 1, true));
            sePigeon.addSet(new MobilitySet(2, Duration.seconds(60), 1, true));
            mobilitySession.addExercise(sePigeon);

            templates.add(mobilitySession);
        }

        return templates;
    }

    private Exercise createResistance(
            String name,
            String description,
            DifficultyLevel difficulty,
            List<Equipment> equipment,
            List<MuscleGroup> primaryMuscles,
            List<MuscleGroup> secondaryMuscles,
            MovementPattern movementPattern,
            int sets,
            int repsMin,
            int repsMax,
            int restSeconds,
            String tempo,
            Double minWeight,
            Double maxWeight,
            Double increment,
            List<String> instructions,
            List<String> mistakes,
            List<String> safety,
            List<String> tags
    ) {
        Exercise ex = new Exercise();
        ex.setName(name);
        ex.setDescription(description);
        ex.setPrimaryCategory(ExerciseCategory.RESISTANCE);
        ex.setDifficultyLevel(difficulty);
        ex.setEquipmentRequired(equipment);
        ex.setStepByStepInstructions(instructions);
        ex.setCommonMistakes(mistakes);
        ex.setSafetyTips(safety);
        ex.setTags(tags);
        ex.setActive(true);

        ResistanceMetrics metrics = new ResistanceMetrics();
        metrics.setPrimaryMuscles(primaryMuscles);
        metrics.setSecondaryMuscles(secondaryMuscles);
        metrics.setMovementPattern(movementPattern);
        metrics.setRecommendedSets(sets);
        metrics.setRecommendedRepsMin(repsMin);
        metrics.setRecommendedRepsMax(repsMax);
        metrics.setRecommendedRestSeconds(restSeconds);
        metrics.setTempoRecommendation(tempo);
        metrics.setDefaultWeightUnit("kg");
        metrics.setMinWeight(minWeight);
        metrics.setMaxWeight(maxWeight);
        metrics.setWeightIncrement(increment);
        metrics.setWeighted(minWeight != null && minWeight > 0);
        metrics.setBodyweight(equipment != null && equipment.contains(Equipment.BODYWEIGHT));
        metrics.setTimed(false);

        ex.setResistanceMetrics(metrics);
        return ex;
    }

    private Exercise createEndurance(
            String name,
            String description,
            DifficultyLevel difficulty,
            List<Equipment> equipment,
            EnduranceType type,
            int blocks,
            int reps,
            Double distance,
            int durationSeconds,
            int hr,
            List<String> instructions,
            List<String> safety,
            List<String> tags
    ) {
        Exercise ex = new Exercise();
        ex.setName(name);
        ex.setDescription(description);
        ex.setPrimaryCategory(ExerciseCategory.ENDURANCE);
        ex.setDifficultyLevel(difficulty);
        ex.setEquipmentRequired(equipment);
        ex.setStepByStepInstructions(instructions);
        ex.setSafetyTips(safety);
        ex.setTags(tags);
        ex.setActive(true);

        EnduranceMetrics metrics = new EnduranceMetrics();
        metrics.setEnduranceType(type);
        metrics.setRecommendedBlocks(blocks);
        metrics.setRecommendedRepetitions(reps);
        metrics.setRecommendedTrackDistance(distance);
        metrics.setRecommendedTrackDuration(durationSeconds);
        metrics.setRecommendedTrackAverageHeartRate(hr);
        metrics.setDistanceUnit("meters");
        metrics.setPaceUnit("min/km");
        metrics.setHeartRateUnit("bpm");

        ex.setEnduranceMetrics(metrics);
        return ex;
    }

    private Exercise createMobility(
            String name,
            String description,
            DifficultyLevel difficulty,
            List<Equipment> equipment,
            MobilityType type,
            StretchType stretchType,
            List<Joint> joints,
            RecommendedTiming timing,
            boolean bilateral,
            int holdSeconds,
            int reps,
            int sets,
            List<String> instructions,
            List<String> safety,
            List<String> tags
    ) {
        Exercise ex = new Exercise();
        ex.setName(name);
        ex.setDescription(description);
        ex.setPrimaryCategory(ExerciseCategory.MOBILITY);
        ex.setDifficultyLevel(difficulty);
        ex.setEquipmentRequired(equipment);
        ex.setStepByStepInstructions(instructions);
        ex.setSafetyTips(safety);
        ex.setTags(tags);
        ex.setActive(true);

        MobilityMetrics metrics = new MobilityMetrics();
        metrics.setMobilityType(type);
        metrics.setStretchType(stretchType);
        metrics.setTargetJoints(joints);
        metrics.setTiming(timing);
        metrics.setPerformBilaterally(bilateral);
        metrics.setRecommendedHoldTimeSeconds(holdSeconds);
        metrics.setRecommendedRepetitions(reps);
        metrics.setRecommendedSets(sets);

        ex.setMobilityMetrics(metrics);
        return ex;
    }
}
