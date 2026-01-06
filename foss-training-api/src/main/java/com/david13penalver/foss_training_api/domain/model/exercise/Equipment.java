package com.david13penalver.foss_training_api.domain.model.exercise;

public enum Equipment {

        // No Equipment
        BODYWEIGHT("Bodyweight", "No equipment required, uses body weight only", EquipmentCategory.BODYWEIGHT),

        // Free Weights
        BARBELL("Barbell", "Long bar with weight plates on each end", EquipmentCategory.FREE_WEIGHTS),
        DUMBBELL("Dumbbell", "Hand-held weight for unilateral or bilateral exercises", EquipmentCategory.FREE_WEIGHTS),
        KETTLEBELL("Kettlebell", "Cast iron ball with a handle for dynamic exercises", EquipmentCategory.FREE_WEIGHTS),
        WEIGHT_PLATE("Weight Plate", "Individual plates for loading barbells or plate machines",
                        EquipmentCategory.FREE_WEIGHTS),
        EZ_BAR("EZ Curl Bar", "Curved barbell for bicep and tricep exercises", EquipmentCategory.FREE_WEIGHTS),
        TRAP_BAR("Trap Bar", "Hexagonal bar for deadlifts and shrugs", EquipmentCategory.FREE_WEIGHTS),

        // Resistance Equipment
        RESISTANCE_BAND("Resistance Band", "Elastic bands providing adjustable resistance",
                        EquipmentCategory.RESISTANCE),
        CABLE_MACHINE("Cable Machine", "Pulley system with adjustable weight stack", EquipmentCategory.RESISTANCE),
        SUSPENSION_TRAINER("Suspension Trainer", "Straps for bodyweight exercises (TRX)", EquipmentCategory.RESISTANCE),

        // Weight Machines - Chest
        SMITH_MACHINE("Smith Machine", "Barbell fixed on vertical rails for guided movement",
                        EquipmentCategory.MACHINES),
        CHEST_PRESS("Chest Press Machine", "Seated or lying machine for chest pressing movements",
                        EquipmentCategory.MACHINES),
        INCLINE_CHEST_PRESS("Incline Chest Press Machine", "Targets upper chest with angled pressing",
                        EquipmentCategory.MACHINES),
        DECLINE_CHEST_PRESS("Decline Chest Press Machine", "Targets lower chest with declined angle",
                        EquipmentCategory.MACHINES),
        CHEST_FLY("Chest Fly Machine", "Pec deck for chest isolation with fly motion", EquipmentCategory.MACHINES),
        CABLE_CROSSOVER("Cable Crossover", "Dual cable system for chest and shoulder exercises",
                        EquipmentCategory.MACHINES),

        // Weight Machines - Back
        LAT_PULLDOWN("Lat Pulldown Machine", "Overhead cable machine for latissimus dorsi development",
                        EquipmentCategory.MACHINES),
        SEATED_CABLE_ROW("Seated Cable Row", "Horizontal pulling machine for mid-back thickness",
                        EquipmentCategory.MACHINES),
        SEATED_ROW("Seated Row Machine", "Plate-loaded or selectorized rowing for back", EquipmentCategory.MACHINES),
        T_BAR_ROW("T-Bar Row Machine", "Chest-supported rowing for thick back development", EquipmentCategory.MACHINES),
        BACK_EXTENSION("Back Extension Machine", "Hyperextension for lower back and glutes",
                        EquipmentCategory.MACHINES),
        ASSISTED_PULL_UP("Assisted Pull-up Machine", "Counterbalanced pull-up/chin-up assistance",
                        EquipmentCategory.MACHINES),

        // Weight Machines - Shoulders
        SHOULDER_PRESS("Shoulder Press Machine", "Overhead pressing for deltoid development",
                        EquipmentCategory.MACHINES),
        LATERAL_RAISE("Lateral Raise Machine", "Isolation for side deltoids", EquipmentCategory.MACHINES),
        REAR_DELT_FLY("Rear Delt Fly Machine", "Reverse pec deck for posterior deltoids", EquipmentCategory.MACHINES),
        SHRUG_MACHINE("Shrug Machine", "Trap-focused shoulder elevation machine", EquipmentCategory.MACHINES),

        // Weight Machines - Arms
        BICEP_CURL("Bicep Curl Machine", "Fixed-path curling for bicep isolation", EquipmentCategory.MACHINES),
        PREACHER_CURL("Preacher Curl Machine", "Angled pad for strict bicep curls", EquipmentCategory.MACHINES),
        TRICEP_EXTENSION("Tricep Extension Machine", "Overhead or seated tricep isolation", EquipmentCategory.MACHINES),
        TRICEP_PRESSDOWN("Tricep Pressdown Station", "Cable-based tricep pushdown", EquipmentCategory.MACHINES),
        TRICEP_DIP("Tricep Dip Machine", "Assisted or weighted dip station", EquipmentCategory.MACHINES),

        // Weight Machines - Legs (Quads/Hamstrings/Glutes)
        LEG_PRESS("Leg Press Machine", "Seated or 45-degree angle lower body pressing", EquipmentCategory.MACHINES),
        HACK_SQUAT("Hack Squat Machine", "Angled squat machine for quad emphasis", EquipmentCategory.MACHINES),
        LEG_EXTENSION("Leg Extension Machine", "Seated quadriceps isolation machine", EquipmentCategory.MACHINES),
        LEG_CURL("Leg Curl Machine", "Lying or seated hamstring isolation", EquipmentCategory.MACHINES),
        SEATED_LEG_CURL("Seated Leg Curl", "Seated position hamstring curl", EquipmentCategory.MACHINES),
        LYING_LEG_CURL("Lying Leg Curl", "Prone position hamstring curl", EquipmentCategory.MACHINES),
        HIP_ABDUCTOR("Hip Abductor Machine", "Outer thigh/glute medius machine", EquipmentCategory.MACHINES),
        HIP_ADDUCTOR("Hip Adductor Machine", "Inner thigh adduction machine", EquipmentCategory.MACHINES),
        GLUTE_KICKBACK("Glute Kickback Machine", "Hip extension for glute isolation", EquipmentCategory.MACHINES),
        PENDULUM_SQUAT("Pendulum Squat Machine", "Arc-motion squat for deep quad activation",
                        EquipmentCategory.MACHINES),

        // Weight Machines - Calves
        STANDING_CALF_RAISE("Standing Calf Raise Machine", "Vertical calf raise for gastrocnemius",
                        EquipmentCategory.MACHINES),
        SEATED_CALF_RAISE("Seated Calf Raise Machine", "Seated calf raise targeting soleus",
                        EquipmentCategory.MACHINES),
        LEG_PRESS_CALF_RAISE("Leg Press Calf Raise", "Calf raises performed on leg press", EquipmentCategory.MACHINES),

        // Weight Machines - Core/Abs
        AB_CRUNCH_MACHINE("Ab Crunch Machine", "Weighted crunch for rectus abdominis", EquipmentCategory.MACHINES),
        TORSO_ROTATION("Torso Rotation Machine", "Seated oblique rotation machine", EquipmentCategory.MACHINES),
        CAPTAIN_CHAIR("Captain's Chair", "Vertical knee raise station for abs", EquipmentCategory.MACHINES),

        // Cardio Equipment
        TREADMILL("Treadmill", "Motorized belt for walking or running", EquipmentCategory.CARDIO),
        STATIONARY_BIKE("Stationary Bike", "Fixed bicycle for cardiovascular exercise", EquipmentCategory.CARDIO),
        ROWING_MACHINE("Rowing Machine", "Full-body cardio machine simulating rowing", EquipmentCategory.CARDIO),
        ELLIPTICAL("Elliptical Machine", "Low-impact cardio machine for full body", EquipmentCategory.CARDIO),
        STAIR_CLIMBER("Stair Climber", "Machine simulating stair climbing", EquipmentCategory.CARDIO),
        ASSAULT_BIKE("Assault Bike", "Air resistance bike for high-intensity intervals", EquipmentCategory.CARDIO),
        SKI_MACHINE(" Ski Machine", "Full-body cardio machine simulating skiing", EquipmentCategory.CARDIO),

        // Functional & Bodyweight Equipment
        PULL_UP_BAR("Pull-up Bar", "Horizontal bar for pull-ups and chin-ups", EquipmentCategory.FUNCTIONAL),
        DIP_STATION("Dip Station", "Parallel bars for dips and support holds", EquipmentCategory.FUNCTIONAL),
        BENCH("Bench", "Flat, incline, or decline bench for various exercises", EquipmentCategory.FUNCTIONAL),
        POWER_RACK("Power Rack", "Steel frame with safety bars for barbell exercises", EquipmentCategory.FUNCTIONAL),
        PLYO_BOX("Plyometric Box", "Box for jumping and step exercises", EquipmentCategory.FUNCTIONAL),
        TRX("TRX", "Suspension trainer for bodyweight exercises", EquipmentCategory.FUNCTIONAL),

        // Accessories
        MEDICINE_BALL("Medicine Ball", "Weighted ball for dynamic and core exercises", EquipmentCategory.ACCESSORIES),
        SLAM_BALL("Slam Ball", "Heavy ball designed for throwing and slamming", EquipmentCategory.ACCESSORIES),
        FOAM_ROLLER("Foam Roller", "Cylindrical foam for self-myofascial release", EquipmentCategory.ACCESSORIES),
        YOGA_MAT("Yoga Mat", "Cushioned mat for floor exercises and stretching", EquipmentCategory.ACCESSORIES),
        AB_WHEEL("Ab Wheel", "Wheel with handles for core strengthening", EquipmentCategory.ACCESSORIES),
        BATTLE_ROPE("Battle Rope", "Heavy rope for conditioning and strength", EquipmentCategory.ACCESSORIES);

        private final String name;
        private final String description;
        private final EquipmentCategory category;

        Equipment(String name, String description, EquipmentCategory category) {
                this.name = name;
                this.description = description;
                this.category = category;
        }

        public String getName() {
                return name;
        }

        public String getDescription() {
                return description;
        }

        public EquipmentCategory getCategory() {
                return category;
        }

        public static Equipment fromString(String text) {
                for (Equipment equipment : Equipment.values()) {
                        if (equipment.name().equalsIgnoreCase(text)) {
                                return equipment;
                        }
                }
                throw new IllegalArgumentException("No equipment with name " + text + " found");
        }
}
