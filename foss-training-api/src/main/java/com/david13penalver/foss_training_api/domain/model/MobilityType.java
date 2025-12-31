package com.david13penalver.foss_training_api.domain.model;

public enum MobilityType {

    STATIC_STRETCHING(
            "Static Stretching",
            "Holding a stretch position for 30-90 seconds to increase muscle length and flexibility"),

    DYNAMIC_MOBILITY(
            "Dynamic Mobility",
            "Active movements through full range of motion to prepare muscles and joints for activity"),

    FOAM_ROLLING(
            "Foam Rolling",
            "Self-myofascial release using foam roller to reduce muscle tension and improve tissue quality"),

    YOGA(
            "Yoga",
            "Mind-body practice combining poses, breathing, and meditation for flexibility and balance"),

    PNF_STRETCHING(
            "PNF Stretching",
            "Proprioceptive Neuromuscular Facilitation - contract-relax technique for rapid ROM gains"),

    ACTIVE_STRETCHING(
            "Active Stretching",
            "Using muscle contraction to hold a stretch position without external assistance"),

    MOBILITY_DRILLS(
            "Mobility Drills",
            "Controlled articular rotations and joint-specific movements to improve functional ROM");

    private final String name;
    private final String description;

    MobilityType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static MobilityType fromString(String text) {
        for (MobilityType type : MobilityType.values()) {
            if (type.name().equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No mobility type with name " + text + " found");
    }
}
