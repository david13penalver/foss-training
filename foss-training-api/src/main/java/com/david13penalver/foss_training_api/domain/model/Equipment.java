package com.david13penalver.foss_training_api.domain.model;

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
    RESISTANCE_BAND("Resistance Band", "Elastic bands providing adjustable resistance", EquipmentCategory.RESISTANCE),
    CABLE_MACHINE("Cable Machine", "Pulley system with adjustable weight stack", EquipmentCategory.RESISTANCE),
    SUSPENSION_TRAINER("Suspension Trainer", "Straps for bodyweight exercises (TRX)", EquipmentCategory.RESISTANCE),

    // Weight Machines
    SMITH_MACHINE("Smith Machine", "Barbell fixed on vertical rails for guided movement", EquipmentCategory.MACHINES),
    LEG_PRESS("Leg Press Machine", "Seated machine for lower body pressing", EquipmentCategory.MACHINES),
    CHEST_PRESS("Chest Press Machine", "Seated machine for chest exercises", EquipmentCategory.MACHINES),
    LAT_PULLDOWN("Lat Pulldown Machine", "Overhead cable machine for back exercises", EquipmentCategory.MACHINES),
    LEG_CURL("Leg Curl Machine", "Machine for hamstring isolation", EquipmentCategory.MACHINES),
    LEG_EXTENSION("Leg Extension Machine", "Machine for quadriceps isolation", EquipmentCategory.MACHINES),
    CABLE_CROSSOVER("Cable Crossover", "Dual cable system for chest and shoulder exercises",
            EquipmentCategory.MACHINES),

    // Cardio Equipment
    TREADMILL("Treadmill", "Motorized belt for walking or running", EquipmentCategory.CARDIO),
    STATIONARY_BIKE("Stationary Bike", "Fixed bicycle for cardiovascular exercise", EquipmentCategory.CARDIO),
    ROWING_MACHINE("Rowing Machine", "Full-body cardio machine simulating rowing", EquipmentCategory.CARDIO),
    ELLIPTICAL("Elliptical Machine", "Low-impact cardio machine for full body", EquipmentCategory.CARDIO),
    STAIR_CLIMBER("Stair Climber", "Machine simulating stair climbing", EquipmentCategory.CARDIO),
    ASSAULT_BIKE("Assault Bike", "Air resistance bike for high-intensity intervals", EquipmentCategory.CARDIO),

    // Functional & Bodyweight Equipment
    PULL_UP_BAR("Pull-up Bar", "Horizontal bar for pull-ups and chin-ups", EquipmentCategory.FUNCTIONAL),
    DIP_STATION("Dip Station", "Parallel bars for dips and support holds", EquipmentCategory.FUNCTIONAL),
    BENCH("Bench", "Flat, incline, or decline bench for various exercises", EquipmentCategory.FUNCTIONAL),
    POWER_RACK("Power Rack", "Steel frame with safety bars for barbell exercises", EquipmentCategory.FUNCTIONAL),
    PLYO_BOX("Plyometric Box", "Box for jumping and step exercises", EquipmentCategory.FUNCTIONAL),

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
