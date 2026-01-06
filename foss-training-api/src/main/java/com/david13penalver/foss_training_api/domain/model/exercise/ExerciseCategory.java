package com.david13penalver.foss_training_api.domain.model.exercise;

public enum ExerciseCategory {

        // ============================================
        // PRIMARY TRAINING CATEGORIES (Evidence-based)
        // ============================================

        RESISTANCE(
                        "Resistance Training",
                        "Progressive overload exercises that increase muscular strength, power, and hypertrophy through external resistance"),

        ENDURANCE(
                        "Endurance Training",
                        "Cardiovascular exercises that enhance aerobic capacity, VO2max, lactate threshold, and cardiorespiratory efficiency"),

        MOBILITY(
                        "Mobility & Flexibility",
                        "Dynamic and static movements that improve range of motion, joint mobility, and tissue extensibility"),

        // ============================================
        // FUNCTIONAL & PERFORMANCE CATEGORIES
        // ============================================

        FUNCTIONAL(
                        "Functional Training",
                        "Multi-joint, multi-planar movements that mimic real-world activities and improve movement patterns for daily life tasks"),

        PLYOMETRICS(
                        "Plyometric Training",
                        "Stretch-shortening cycle exercises that develop explosive power, rate of force development, and reactive strength"),

        BALANCE(
                        "Balance Training",
                        "Proprioceptive exercises that improve postural control, stability, and reduce fall risk through single-leg and unstable surface training"),

        CORE(
                        "Core Stability",
                        "Anti-extension, anti-rotation, and anti-lateral flexion exercises that enhance trunk stability and force transfer"),

        // ============================================
        // SPECIALIZED TRAINING MODALITIES
        // ============================================

        CALISTHENICS(
                        "Calisthenics",
                        "Bodyweight exercises using multiple muscle groups simultaneously to develop relative strength and body control"),

        NEUROMUSCULAR(
                        "Neuromuscular Training",
                        "Integrative exercises combining agility, coordination, dynamic stability, and reactive training to optimize movement mechanics and injury prevention"),

        POWER(
                        "Power Training",
                        "High-velocity resistance exercises (30-60% 1RM) that maximize force production rate and peak power output"),

        STABILIZATION(
                        "Stabilization Training",
                        "Motor control and postural exercises using low loads (50-70% 1RM) on unstable surfaces to enhance joint stability and proprioception"),

        AGILITY(
                        "Agility Training",
                        "Change-of-direction speed (CoD) and reactive drills that improve movement efficiency, deceleration control, and perceptual-cognitive decision making"),

        SPEED(
                        "Speed Training",
                        "Sprint mechanics, acceleration, and maximal velocity training to enhance running economy and neuromuscular recruitment patterns"),

        PILATES(
                        "Pilates",
                        "Low-impact exercise system emphasizing core strength, spinal alignment, controlled breathing, and mind-body connection with evidence for pain reduction"),

        ISOMETRIC(
                        "Isometric Training",
                        "Static muscle contractions without joint movement that increase tendon stiffness, joint-angle specific strength, and time-under-tension adaptations"),

        CIRCUIT(
                        "Circuit Training",
                        "Sequential multi-exercise format combining resistance and metabolic conditioning with minimal rest for improved muscular endurance and work capacity"),

        REHABILITATION(
                        "Rehabilitation Exercise",
                        "Therapeutic movements designed to restore function, reduce pain, and correct movement dysfunction following injury or surgery"),

        SPORT_SPECIFIC(
                        "Sport-Specific Training",
                        "Exercises tailored to biomechanical demands of specific sports, targeting relevant energy systems, movement patterns, and performance metrics");

        private final String name;
        private final String description;

        ExerciseCategory(String name, String description) {
                this.name = name;
                this.description = description;
        }

        public String getName() {
                return name;
        }

        public String getDescription() {
                return description;
        }

        // Helper methods for filtering
        public boolean isPrimaryCategory() {
                return this == RESISTANCE || this == ENDURANCE || this == MOBILITY;
        }

        public boolean requiresEquipment() {
                return this != CALISTHENICS && this != BALANCE && this != AGILITY && this != SPEED;
        }

        public boolean isPerformanceOriented() {
                return this == POWER || this == PLYOMETRICS || this == SPEED ||
                                this == AGILITY || this == SPORT_SPECIFIC;
        }

        public boolean isTherapeutic() {
                return this == REHABILITATION || this == STABILIZATION || this == PILATES;
        }

        public static ExerciseCategory fromString(String text) {
                for (ExerciseCategory category : ExerciseCategory.values()) {
                        if (category.name().equalsIgnoreCase(text)) {
                                return category;
                        }
                }
                throw new IllegalArgumentException("No constant with text " + text + " found");
        }
}
