package com.david13penalver.foss_training_api.domain.model;

public enum MovementPattern {

        PUSH(
                        "Push",
                        "Pressing movements away from the body",
                        "Bench press, push-ups, overhead press"),

        PULL(
                        "Pull",
                        "Pulling movements toward the body",
                        "Pull-ups, rows, lat pulldowns"),

        SQUAT(
                        "Squat",
                        "Knee-dominant lower body movement",
                        "Back squat, front squat, goblet squat, leg press"),

        HINGE(
                        "Hinge",
                        "Hip-dominant posterior chain movement",
                        "Deadlift, Romanian deadlift, kettlebell swings"),

        LUNGE(
                        "Lunge",
                        "Single-leg or split-stance lower body movement",
                        "Walking lunges, Bulgarian split squats, step-ups"),

        CARRY(
                        "Carry",
                        "Loaded carrying or walking movements",
                        "Farmer's walk, suitcase carry, overhead carry"),

        ROTATION(
                        "Rotation",
                        "Rotational or anti-rotational core movements",
                        "Russian twists, cable woodchops, Pallof press"),

        ISOLATION(
                        "Isolation",
                        "Single-joint movement targeting one muscle group",
                        "Bicep curls, tricep extensions, leg curls"),

        COMPOUND(
                        "Compound",
                        "Multi-joint movement using multiple muscle groups",
                        "Deadlifts, squats, bench press");

        private final String displayName;
        private final String description;
        private final String examples;

        MovementPattern(String displayName, String description, String examples) {
                this.displayName = displayName;
                this.description = description;
                this.examples = examples;
        }

        public String getDisplayName() {
                return displayName;
        }

        public String getDescription() {
                return description;
        }

        public String getExamples() {
                return examples;
        }

        public boolean isCompound() {
                return this == PUSH || this == PULL || this == SQUAT ||
                                this == HINGE || this == LUNGE || this == COMPOUND;
        }

        public static MovementPattern fromString(String text) {
                for (MovementPattern pattern : MovementPattern.values()) {
                        if (pattern.name().equalsIgnoreCase(text)) {
                                return pattern;
                        }
                }
                throw new IllegalArgumentException("No movement pattern with name " + text + " found");
        }
}
