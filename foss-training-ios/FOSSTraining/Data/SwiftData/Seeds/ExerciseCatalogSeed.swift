import Foundation
import SwiftData

@MainActor
public enum ExerciseCatalogSeed {
    public static func seedInitialDataIfNeeded(context: ModelContext) {
        let descriptor = FetchDescriptor<SDExercise>()
        let count = (try? context.fetchCount(descriptor)) ?? 0
        guard count == 0 else { return }

        let defaultExercises: [Exercise] = [
            Exercise(
                id: 1,
                name: "Barbell Back Squat",
                description: "Fundamental lower body compound exercise targeting the quadriceps, glutes, and spinal erectors.",
                primaryCategory: .resistance,
                primaryMuscleGroup: "Quadriceps",
                secondaryMuscleGroups: ["Glutes", "Hamstrings", "Core"],
                movementPattern: .squat,
                equipmentRequired: [.barbell],
                difficultyLevel: .intermediate,
                tags: ["Compound", "Legs", "Barbell"]
            ),
            Exercise(
                id: 2,
                name: "Barbell Bench Press",
                description: "Upper body horizontal push exercise building chest, anterior deltoids, and triceps.",
                primaryCategory: .resistance,
                primaryMuscleGroup: "Chest",
                secondaryMuscleGroups: ["Triceps", "Anterior Deltoid"],
                movementPattern: .push,
                equipmentRequired: [.barbell],
                difficultyLevel: .intermediate,
                tags: ["Compound", "Chest", "Push"]
            ),
            Exercise(
                id: 3,
                name: "Conventional Deadlift",
                description: "Posterior chain movement developing back, hamstrings, and grip strength.",
                primaryCategory: .resistance,
                primaryMuscleGroup: "Hamstrings",
                secondaryMuscleGroups: ["Glutes", "Lower Back", "Traps"],
                movementPattern: .hinge,
                equipmentRequired: [.barbell],
                difficultyLevel: .advanced,
                tags: ["Compound", "Back", "Hinge"]
            ),
            Exercise(
                id: 4,
                name: "Overhead Barbell Press",
                description: "Vertical pressing movement building shoulder strength and core stability.",
                primaryCategory: .resistance,
                primaryMuscleGroup: "Shoulders",
                secondaryMuscleGroups: ["Triceps", "Upper Chest"],
                movementPattern: .push,
                equipmentRequired: [.barbell],
                difficultyLevel: .intermediate,
                tags: ["Compound", "Shoulders", "Press"]
            ),
            Exercise(
                id: 5,
                name: "Pull-Up",
                description: "Bodyweight vertical pull targeting the latissimus dorsi and biceps.",
                primaryCategory: .resistance,
                primaryMuscleGroup: "Lats",
                secondaryMuscleGroups: ["Biceps", "Upper Back"],
                movementPattern: .pull,
                equipmentRequired: [.bodyweight],
                difficultyLevel: .intermediate,
                tags: ["Bodyweight", "Back", "Pull"]
            ),
            Exercise(
                id: 6,
                name: "5km Zone 2 Run",
                description: "Low-intensity steady state aerobic base conditioning.",
                primaryCategory: .endurance,
                enduranceType: "Running",
                equipmentRequired: [.other],
                difficultyLevel: .beginner,
                tags: ["Cardio", "Aerobic", "Endurance"]
            ),
            Exercise(
                id: 7,
                name: "90/90 Hip Mobility Flow",
                description: "Internal and external rotation mobility drill for hips.",
                primaryCategory: .mobility,
                mobilityType: "Dynamic",
                targetJoints: ["Hip"],
                equipmentRequired: [.bodyweight],
                difficultyLevel: .beginner,
                tags: ["Mobility", "Hips", "Recovery"]
            )
        ]

        for ex in defaultExercises {
            let sd = SDExercise.fromDomain(ex)
            context.insert(sd)
        }

        // Add a default template session
        let defaultSession = SDSession(
            id: 1,
            name: "Upper Body Hypertrophy",
            sessionDescription: "Balanced push & pull workout targeting chest, shoulders, and back.",
            estimatedDurationMinutes: 60,
            exercises: [
                SDSessionExercise(
                    orderIndex: 0,
                    exerciseId: 2,
                    exerciseName: "Barbell Bench Press",
                    part: .main,
                    restSeconds: 120,
                    sets: [
                        SDResistanceSet(setNumber: 1, setType: .warmUp, weightKg: 40, repetitions: 10, restSeconds: 90),
                        SDResistanceSet(setNumber: 2, setType: .normal, weightKg: 80, repetitions: 8, restSeconds: 120),
                        SDResistanceSet(setNumber: 3, setType: .normal, weightKg: 80, repetitions: 8, restSeconds: 120),
                        SDResistanceSet(setNumber: 4, setType: .normal, weightKg: 80, repetitions: 7, restSeconds: 120)
                    ]
                ),
                SDSessionExercise(
                    orderIndex: 1,
                    exerciseId: 5,
                    exerciseName: "Pull-Up",
                    part: .main,
                    restSeconds: 90,
                    sets: [
                        SDResistanceSet(setNumber: 1, setType: .normal, weightKg: 0, repetitions: 10, restSeconds: 90),
                        SDResistanceSet(setNumber: 2, setType: .normal, weightKg: 0, repetitions: 9, restSeconds: 90),
                        SDResistanceSet(setNumber: 3, setType: .normal, weightKg: 0, repetitions: 8, restSeconds: 90)
                    ]
                )
            ]
        )
        context.insert(defaultSession)

        try? context.save()
    }
}
