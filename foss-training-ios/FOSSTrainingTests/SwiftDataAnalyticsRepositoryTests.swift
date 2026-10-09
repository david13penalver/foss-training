import Testing
import Foundation
import SwiftData
@testable import FOSSTraining

@Suite("SwiftData Analytics Repository Tests")
@MainActor
struct SwiftDataAnalyticsRepositoryTests {

    private func createTestContainer() throws -> ModelContainer {
        let schema = Schema([
            SDExercise.self,
            SDSession.self,
            SDSessionExercise.self,
            SDResistanceSet.self,
            SDTraining.self,
            SDBodyweightEntry.self,
            SDTrainingProgram.self,
            SDProgramWorkout.self
        ])
        let config = ModelConfiguration(schema: schema, isStoredInMemoryOnly: true)
        return try ModelContainer(for: schema, configurations: [config])
    }

    private func seedTestData(context: ModelContext) throws {
        // Exercise
        let bench = SDExercise(
            id: 1,
            name: "Bench Press",
            exerciseDescription: "Barbell bench press",
            primaryCategory: .resistance,
            secondaryCategories: [],
            primaryMuscleGroup: "Chest",
            secondaryMuscleGroups: ["Triceps", "Shoulders"],
            movementPattern: .push,
            equipmentRequired: [.barbell],
            difficultyLevel: .intermediate,
            tags: ["compound"]
        )
        context.insert(bench)

        let squat = SDExercise(
            id: 2,
            name: "Squat",
            exerciseDescription: "Barbell back squat",
            primaryCategory: .resistance,
            secondaryCategories: [],
            primaryMuscleGroup: "Quadriceps",
            secondaryMuscleGroups: ["Glutes"],
            movementPattern: .squat,
            equipmentRequired: [.barbell],
            difficultyLevel: .intermediate,
            tags: ["compound"]
        )
        context.insert(squat)

        // Completed Trainings
        let now = Date()
        let calendar = Calendar.current

        let t1Date = calendar.date(byAdding: .day, value: -10, to: now)!
        let t1 = SDTraining(
            id: 1,
            name: "Chest Day 1",
            trainingDescription: "Heavy bench",
            trainingDate: t1Date,
            startTime: t1Date,
            endTime: t1Date.addingTimeInterval(3600),
            status: .completed,
            notes: "Felt strong",
            overallRpe: 8.0,
            loggedExercises: [
                SDSessionExercise(
                    orderIndex: 0,
                    exerciseId: 1,
                    exerciseName: "Bench Press",
                    part: .main,
                    restSeconds: 120,
                    sets: [
                        SDResistanceSet(setNumber: 1, setType: .normal, weightKg: 100, repetitions: 5, rpe: 8.0, restSeconds: 120, isCompleted: true),
                        SDResistanceSet(setNumber: 2, setType: .normal, weightKg: 100, repetitions: 5, rpe: 8.5, restSeconds: 120, isCompleted: true)
                    ]
                )
            ]
        )
        context.insert(t1)

        let t2Date = calendar.date(byAdding: .day, value: -3, to: now)!
        let t2 = SDTraining(
            id: 2,
            name: "Chest Day 2",
            trainingDescription: "PR bench session",
            trainingDate: t2Date,
            startTime: t2Date,
            endTime: t2Date.addingTimeInterval(3600),
            status: .completed,
            notes: "New PR",
            overallRpe: 9.0,
            loggedExercises: [
                SDSessionExercise(
                    orderIndex: 0,
                    exerciseId: 1,
                    exerciseName: "Bench Press",
                    part: .main,
                    restSeconds: 120,
                    sets: [
                        SDResistanceSet(setNumber: 1, setType: .normal, weightKg: 110, repetitions: 5, rpe: 9.0, restSeconds: 120, isCompleted: true)
                    ]
                )
            ]
        )
        context.insert(t2)

        try context.save()
    }

    @Test("calculateOneRepMax delegates to OneRepMaxCalculator")
    func testCalculateOneRepMax() async throws {
        let container = try createTestContainer()
        let repo = SwiftDataAnalyticsRepository(modelContext: container.mainContext)

        let estimates = try await repo.calculateOneRepMax(weightKg: 100.0, reps: 5)
        #expect(estimates.count == 6)
        let epley = estimates.first { $0.formula == .epley }
        #expect(epley?.estimatedOneRepMax == 116.67)
    }

    @Test("calculateAcwr with explicit asOfDate and nil asOfDate")
    func testCalculateAcwr() async throws {
        let container = try createTestContainer()
        try seedTestData(context: container.mainContext)
        let repo = SwiftDataAnalyticsRepository(modelContext: container.mainContext)

        let result = try await repo.calculateAcwr(asOfDate: Date())
        #expect(result.dailyWorkloads.count == 28)
        #expect(result.acuteWorkload >= 0.0)

        let resultDefault = try await repo.calculateAcwr(asOfDate: nil)
        #expect(resultDefault.dailyWorkloads.count == 28)
    }

    @Test("getPersonalRecords with and without exerciseId filter")
    func testGetPersonalRecords() async throws {
        let container = try createTestContainer()
        try seedTestData(context: container.mainContext)
        let repo = SwiftDataAnalyticsRepository(modelContext: container.mainContext)

        let allPrs = try await repo.getPersonalRecords(exerciseId: nil)
        #expect(!allPrs.isEmpty)
        #expect(allPrs.allSatisfy { $0.exerciseId == 1 })

        let filteredPrs = try await repo.getPersonalRecords(exerciseId: 1)
        #expect(!filteredPrs.isEmpty)

        let emptyPrs = try await repo.getPersonalRecords(exerciseId: 999)
        #expect(emptyPrs.isEmpty)
    }

    @Test("getWeeklyMuscleVolume with and without weekStartDate")
    func testGetWeeklyMuscleVolume() async throws {
        let container = try createTestContainer()
        try seedTestData(context: container.mainContext)
        let repo = SwiftDataAnalyticsRepository(modelContext: container.mainContext)

        let now = Date()
        let volumeWithDate = try await repo.getWeeklyMuscleVolume(weekStartDate: now)
        #expect(volumeWithDate.muscleVolumes.contains { $0.muscleGroup == "Chest" })

        let volumeNil = try await repo.getWeeklyMuscleVolume(weekStartDate: nil)
        #expect(volumeNil.muscleVolumes.contains { $0.muscleGroup == "Chest" })
    }

    @Test("getExerciseProgression success with months > 0 and months <= 0, and not found error")
    func testGetExerciseProgression() async throws {
        let container = try createTestContainer()
        try seedTestData(context: container.mainContext)
        let repo = SwiftDataAnalyticsRepository(modelContext: container.mainContext)

        // Success months > 0
        let prog3m = try await repo.getExerciseProgression(exerciseId: 1, months: 3)
        #expect(prog3m.exerciseId == 1)
        #expect(prog3m.totalSessions == 2)
        #expect(prog3m.trend == .improving)

        // Success months <= 0 (all time)
        let progAll = try await repo.getExerciseProgression(exerciseId: 1, months: 0)
        #expect(progAll.totalSessions == 2)

        // Error: exercise not found
        do {
            _ = try await repo.getExerciseProgression(exerciseId: 999, months: 3)
            Issue.record("Expected 404 error when exercise does not exist")
        } catch {
            let nsError = error as NSError
            #expect(nsError.code == 404)
        }
    }

    @Test("calculateHeartRateZones Karvonen and percentage of max HR")
    func testCalculateHeartRateZones() async throws {
        let container = try createTestContainer()
        let repo = SwiftDataAnalyticsRepository(modelContext: container.mainContext)

        let karvonen = try await repo.calculateHeartRateZones(restingHr: 60, maxHr: 190, method: .karvonen)
        #expect(karvonen.zones.count == 5)
        #expect(karvonen.method == .karvonen)

        let pctMax = try await repo.calculateHeartRateZones(restingHr: 60, maxHr: 190, method: .percentMaxHr)
        #expect(pctMax.zones.count == 5)
        #expect(pctMax.method == .percentMaxHr)
    }
}
