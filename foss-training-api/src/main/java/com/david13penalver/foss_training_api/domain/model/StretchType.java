package com.david13penalver.foss_training_api.domain.model;

public enum StretchType {

    STATIC(
            "Static Stretch",
            "Hold a stretch position for 30-90 seconds. Best for post-workout and improving flexibility.",
            "Hamstring stretch, quad stretch, shoulder stretch"),

    DYNAMIC(
            "Dynamic Stretch",
            "Active movements that take joints through full ROM. Best for warm-ups and pre-workout.",
            "Leg swings, arm circles, walking lunges"),

    PNF(
            "PNF (Contract-Relax)",
            "Contract muscle then relax into deeper stretch. Most effective for rapid flexibility gains.",
            "Partner-assisted hamstring stretch with contraction phase"),

    BALLISTIC(
            "Ballistic Stretch",
            "Bouncing movements to push beyond normal ROM. Higher injury risk, not generally recommended.",
            "Bouncing toe touches (use with caution)"),

    ACTIVE(
            "Active Stretch",
            "Hold position using muscle contraction without external force. Improves strength and flexibility.",
            "Holding leg raise position, active pike stretch"),

    PASSIVE(
            "Passive Stretch",
            "External force (partner, gravity, equipment) creates the stretch while muscles relax.",
            "Partner-assisted splits, gravity-assisted shoulder stretch");

    private final String name;
    private final String description;
    private final String examples;

    StretchType(String name, String description, String examples) {
        this.name = name;
        this.description = description;
        this.examples = examples;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getExamples() {
        return examples;
    }

    public boolean isRecommendedForWarmup() {
        return this == DYNAMIC || this == ACTIVE;
    }

    public boolean isRecommendedForCooldown() {
        return this == STATIC || this == PNF || this == PASSIVE;
    }

    public static StretchType fromString(String text) {
        for (StretchType type : StretchType.values()) {
            if (type.name().equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No stretch type with name " + text + " found");
    }
}
