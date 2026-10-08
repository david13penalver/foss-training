import Testing
import Foundation
import SwiftData
@testable import FOSSTraining

@Suite("SwiftData Training Program Repository Tests")
@MainActor
struct SwiftDataTrainingProgramRepositoryTests {

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

    private func seedTestSession(context: ModelContext, id: Int = 1, name: String = "Upper Power") -> SDSession {
        let session = SDSession(
            id: id,
            name: name,
            sessionDescription: "Upper body strength session",
            notes: "Warmup thoroughly",
            estimatedDurationMinutes: 60,
            exercises: [
                SDSessionExercise(
                    orderIndex: 0,
                    exerciseId: 101,
                    exerciseName: "Bench Press",
                    part: .main,
                    restSeconds: 180,
                    sets: [
                        SDResistanceSet(setNumber: 1, setType: .normal, weightKg: 100, repetitions: 5, rpe: 8.0, isCompleted: false)
                    ]
                )
            ]
        )
        context.insert(session)
        try? context.save()
        return session
    }

    @Test("Save and fetch programs with auto ID and custom ID")
    func testSaveAndFetchPrograms() async throws {
        let container = try createTestContainer()
        let context = container.mainContext
        let repo = SwiftDataTrainingProgramRepository(modelContext: context)

        let session = seedTestSession(context: context)

        // Program 1: id: 0 when repository is empty -> auto assigned ID = 1
        let p1 = TrainingProgram(
            id: 0,
            name: "Beginner Linear",
            description: "Novice linear progression",
            durationWeeks: 4,
            periodizationType: .linear,
            level: .beginner,
            workouts: [
                ProgramWorkout(dayOfWeek: 1, focus: "Upper", session: session.toDomain())
            ],
            isActive: true
        )
        let saved1 = try await repo.saveProgram(p1)
        #expect(saved1.id == 1)
        #expect(saved1.name == "Beginner Linear")

        // Program 2: custom ID = 50
        let p2 = TrainingProgram(
            id: 50,
            name: "Block Periodization",
            durationWeeks: 6,
            periodizationType: .block,
            level: .advanced,
            workouts: []
        )
        let saved2 = try await repo.saveProgram(p2)
        #expect(saved2.id == 50)

        // Program 3: id: 0 when programs exist -> auto assigned max + 1 = 51
        let p3 = TrainingProgram(
            id: 0,
            name: "Undulating Hypertrophy",
            durationWeeks: 8,
            periodizationType: .undulating,
            level: .intermediate,
            workouts: []
        )
        let saved3 = try await repo.saveProgram(p3)
        #expect(saved3.id == 51)

        // Fetch all
        let all = try await repo.getPrograms()
        #expect(all.count == 3)
        #expect(all[0].name == "Beginner Linear")

        // Fetch single
        let fetched1 = try await repo.getProgram(id: 1)
        #expect(fetched1?.name == "Beginner Linear")
        #expect(fetched1?.workouts.count == 1)

        let notFound = try await repo.getProgram(id: 999)
        #expect(notFound == nil)
    }

    @Test("Update existing program and its workouts")
    func testUpdateProgram() async throws {
        let container = try createTestContainer()
        let context = container.mainContext
        let repo = SwiftDataTrainingProgramRepository(modelContext: context)

        let session1 = seedTestSession(context: context, id: 1, name: "Push Day")
        let session2 = seedTestSession(context: context, id: 2, name: "Pull Day")

        let initial = TrainingProgram(
            id: 10,
            name: "Push Pull Legs",
            description: "Original description",
            durationWeeks: 4,
            periodizationType: .linear,
            level: .intermediate,
            workouts: [
                ProgramWorkout(dayOfWeek: 1, focus: "Push", session: session1.toDomain())
            ]
        )
        _ = try await repo.saveProgram(initial)

        // Update
        var updated = initial
        updated.name = "PPL Mesocycle 2"
        updated.description = "Updated description"
        updated.durationWeeks = 6
        updated.periodizationType = .undulating
        updated.level = .advanced
        updated.isActive = false
        updated.workouts = [
            ProgramWorkout(dayOfWeek: 1, focus: "Push", session: session1.toDomain()),
            ProgramWorkout(dayOfWeek: 3, focus: "Pull", session: session2.toDomain())
        ]

        let result = try await repo.saveProgram(updated)
        #expect(result.name == "PPL Mesocycle 2")
        #expect(result.description == "Updated description")
        #expect(result.durationWeeks == 6)
        #expect(result.periodizationType == .undulating)
        #expect(result.level == .advanced)
        #expect(!result.isActive)
        #expect(result.workouts.count == 2)
    }

    @Test("Delete program and programExists check")
    func testDeleteAndExists() async throws {
        let container = try createTestContainer()
        let context = container.mainContext
        let repo = SwiftDataTrainingProgramRepository(modelContext: context)

        let program = TrainingProgram(id: 25, name: "Strength Block")
        _ = try await repo.saveProgram(program)

        #expect(try await repo.programExists(id: 25))
        #expect(try await !repo.programExists(id: 99))

        try await repo.deleteProgram(id: 25)
        #expect(try await !repo.programExists(id: 25))

        // Deleting nonexistent program completes without error
        try await repo.deleteProgram(id: 999)
    }

    @Test("Clone program with custom name, empty name, nil name, and nonexistent id")
    func testCloneProgram() async throws {
        let container = try createTestContainer()
        let context = container.mainContext
        let repo = SwiftDataTrainingProgramRepository(modelContext: context)

        let session = seedTestSession(context: context)
        let original = TrainingProgram(
            id: 1,
            name: "Linear Power",
            description: "Cycle 1",
            durationWeeks: 4,
            periodizationType: .linear,
            level: .intermediate,
            workouts: [
                ProgramWorkout(dayOfWeek: 1, focus: "Power", session: session.toDomain())
            ]
        )
        _ = try await repo.saveProgram(original)

        // 1. Clone with custom name
        let clone1 = try await repo.cloneProgram(id: 1, newName: "Linear Power Phase 2")
        #expect(clone1.id == 2)
        #expect(clone1.name == "Linear Power Phase 2")
        #expect(clone1.workouts.count == 1)
        #expect(clone1.workouts[0].focus == "Power")

        // 2. Clone with nil name (defaults to "<original> (Copy)")
        let clone2 = try await repo.cloneProgram(id: 1, newName: nil)
        #expect(clone2.id == 3)
        #expect(clone2.name == "Linear Power (Copy)")

        // 3. Clone with whitespace-only name
        let clone3 = try await repo.cloneProgram(id: 1, newName: "   ")
        #expect(clone3.id == 4)
        #expect(clone3.name == "Linear Power (Copy)")

        // 4. Clone non-existent throws
        await #expect(throws: NSError.self) {
            try await repo.cloneProgram(id: 999, newName: "Fail")
        }
    }

    @Test("Clone program when no programs exist uses nextId = 1")
    func testCloneWhenOnlyOneProgramExists() async throws {
        let container = try createTestContainer()
        let context = container.mainContext
        let repo = SwiftDataTrainingProgramRepository(modelContext: context)

        let original = TrainingProgram(id: 10, name: "Solo Program")
        _ = try await repo.saveProgram(original)

        let clone = try await repo.cloneProgram(id: 10, newName: "Cloned")
        #expect(clone.id == 11)
    }

    @Test("Generate schedule persists SDTrainings correctly")
    func testGenerateSchedulePersistence() async throws {
        let container = try createTestContainer()
        let context = container.mainContext
        let repo = SwiftDataTrainingProgramRepository(modelContext: context)

        let session = seedTestSession(context: context)
        let program = TrainingProgram(
            id: 1,
            name: "4-Week Cycle",
            durationWeeks: 4,
            workouts: [
                ProgramWorkout(dayOfWeek: 1, focus: "Heavy", session: session.toDomain()),
                ProgramWorkout(dayOfWeek: 4, focus: "Light", session: session.toDomain())
            ]
        )
        _ = try await repo.saveProgram(program)

        let startDate = Date(timeIntervalSince1970: 1774915200) // 2026-03-30 (Monday)
        let created = try await repo.generateSchedule(programId: 1, startDate: startDate)

        #expect(created.count == 8) // 4 weeks * 2 workouts
        #expect(created[0].id == 1)
        #expect(created[0].programId == 1)
        #expect(created[0].status == .planned)
        #expect(created[0].name.contains("W1D1"))

        // Verify persisted in modelContext
        let sdTrainings = try context.fetch(FetchDescriptor<SDTraining>())
        #expect(sdTrainings.count == 8)

        // Second generation generates incremental IDs
        let secondRound = try await repo.generateSchedule(programId: 1, startDate: nil)
        #expect(secondRound.count == 8)
        #expect(secondRound[0].id == 9)

        // Error cases
        await #expect(throws: NSError.self) {
            try await repo.generateSchedule(programId: 999, startDate: nil)
        }

        let emptyWorkoutProgram = TrainingProgram(id: 2, name: "Empty Program", workouts: [])
        _ = try await repo.saveProgram(emptyWorkoutProgram)
        await #expect(throws: NSError.self) {
            try await repo.generateSchedule(programId: 2, startDate: nil)
        }
    }

    @Test("Get program adherence calculates correctly")
    func testGetProgramAdherence() async throws {
        let container = try createTestContainer()
        let context = container.mainContext
        let repo = SwiftDataTrainingProgramRepository(modelContext: context)

        let session = seedTestSession(context: context)
        let program = TrainingProgram(
            id: 1,
            name: "Hypertrophy Mesocycle",
            durationWeeks: 2,
            workouts: [
                ProgramWorkout(dayOfWeek: 1, focus: "Upper", session: session.toDomain()),
                ProgramWorkout(dayOfWeek: 3, focus: "Lower", session: session.toDomain())
            ]
        )
        _ = try await repo.saveProgram(program)

        // Before schedule generation
        let initialAdherence = try await repo.getProgramAdherence(id: 1)
        #expect(initialAdherence.status == .notStarted)
        #expect(initialAdherence.totalScheduledWorkouts == 4)

        // After generating schedule
        let trainings = try await repo.generateSchedule(programId: 1, startDate: Date())
        #expect(trainings.count == 4)

        // Mark one completed
        let fetchedSD = try context.fetch(FetchDescriptor<SDTraining>())
        fetchedSD[0].statusRaw = TrainingStatus.completed.rawValue
        fetchedSD[0].endTime = Date()
        try context.save()

        let updatedAdherence = try await repo.getProgramAdherence(id: 1)
        #expect(updatedAdherence.completedWorkouts == 1)
        #expect(updatedAdherence.totalScheduledWorkouts == 4)

        // Adherence for non-existent program throws
        await #expect(throws: NSError.self) {
            try await repo.getProgramAdherence(id: 999)
        }
    }

    @Test("SDProgramWorkout and SDTrainingProgram fallback models")
    func testSDModelsFallbackCoverage() throws {
        let workoutModel = SDProgramWorkout(dayOfWeek: 2, focus: "Core", session: nil)
        let domainWorkout = workoutModel.toDomain()
        #expect(domainWorkout.dayOfWeek == 2)
        #expect(domainWorkout.session.name == "Workout")

        let fromDomainWorkout = SDProgramWorkout.fromDomain(domainWorkout, sessionModel: nil)
        #expect(fromDomainWorkout.focus == "Core")

        let programModel = SDTrainingProgram(
            id: 10,
            name: "Custom",
            programDescription: "Desc",
            durationWeeks: 3,
            periodizationType: .block,
            level: .elite,
            workouts: [workoutModel],
            isActive: true
        )
        let domainProg = programModel.toDomain()
        #expect(domainProg.durationWeeks == 3)
        #expect(domainProg.level == .elite)

        let roundtrip = SDTrainingProgram.fromDomain(domainProg, workoutModels: [workoutModel])
        #expect(roundtrip.name == "Custom")

        // Test fallback raw values in toDomain()
        let fallbackModel = SDTrainingProgram(
            id: 99,
            name: "Invalid Raw Values",
            workouts: []
        )
        fallbackModel.periodizationTypeRaw = "NON_EXISTENT_TYPE"
        fallbackModel.levelRaw = "NON_EXISTENT_LEVEL"
        let fallbackDomain = fallbackModel.toDomain()
        #expect(fallbackDomain.periodizationType == .linear)
        #expect(fallbackDomain.level == .intermediate)
    }
}
