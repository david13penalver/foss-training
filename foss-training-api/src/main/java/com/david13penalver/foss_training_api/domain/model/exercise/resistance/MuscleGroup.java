package com.david13penalver.foss_training_api.domain.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum MuscleGroup {

    // Upper Body - Chest & Back
    CHEST("Chest", "Pectoralis major and minor", MuscleCategory.UPPER_BODY),
    UPPER_BACK("Upper Back", "Trapezius, rhomboids", MuscleCategory.UPPER_BODY),
    LATS("Lats", "Latissimus dorsi", MuscleCategory.UPPER_BODY),
    LOWER_BACK("Lower Back", "Erector spinae", MuscleCategory.UPPER_BODY),

    // Upper Body - Shoulders & Arms
    SHOULDERS("Shoulders", "Deltoids (anterior, lateral, posterior)", MuscleCategory.UPPER_BODY),
    BICEPS("Biceps", "Biceps brachii", MuscleCategory.UPPER_BODY),
    TRICEPS("Triceps", "Triceps brachii", MuscleCategory.UPPER_BODY),
    FOREARMS("Forearms", "Wrist flexors and extensors", MuscleCategory.UPPER_BODY),

    // Core
    ABS("Abs", "Rectus abdominis, six-pack muscles", MuscleCategory.CORE),
    OBLIQUES("Obliques", "External and internal obliques", MuscleCategory.CORE),
    CORE("Core", "Deep stabilizers including transverse abdominis", MuscleCategory.CORE),

    // Lower Body
    QUADRICEPS("Quadriceps", "Front thigh muscles", MuscleCategory.LOWER_BODY),
    HAMSTRINGS("Hamstrings", "Back thigh muscles", MuscleCategory.LOWER_BODY),
    GLUTES("Glutes", "Gluteus maximus, medius, minimus", MuscleCategory.LOWER_BODY),
    CALVES("Calves", "Gastrocnemius and soleus", MuscleCategory.LOWER_BODY),
    HIP_FLEXORS("Hip Flexors", "Iliopsoas and surrounding muscles", MuscleCategory.LOWER_BODY),
    ADDUCTORS("Adductors", "Inner thigh muscles", MuscleCategory.LOWER_BODY),

    // Full Body
    FULL_BODY("Full Body", "Multiple major muscle groups", MuscleCategory.FULL_BODY);

    private final String name;
    private final String description;
    private final MuscleCategory category;

    MuscleGroup(String name, String description, MuscleCategory category) {
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

    public MuscleCategory getCategory() {
        return category;
    }

    public static List<MuscleGroup> getByCategory(MuscleCategory category) {
        return Arrays.stream(MuscleGroup.values())
                .filter(mg -> mg.category == category)
                .collect(Collectors.toList());
    }
}
