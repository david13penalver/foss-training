import Foundation
import Testing
@testable import FOSSTraining

private final class MockAthleteRepository: AthleteRepository, @unchecked Sendable {
    var profileToReturn: AthleteProfile? = AthleteProfile(
        id: 1,
        displayName: "Test Lifter",
        gender: .male,
        dateOfBirth: nil,
        heightCm: 180.0,
        experienceLevel: .intermediate,
        targetGoal: "Bulk",
        preferredUnit: .kg
    )
    var historyToReturn: [BodyweightEntry] = [
        BodyweightEntry(id: 1, weightKg: 80.0, measuredDate: Date().addingTimeInterval(-86400)),
        BodyweightEntry(id: 2, weightKg: 80.5, measuredDate: Date())
    ]
    var shouldThrow: Bool = false

    func getProfile() async throws -> AthleteProfile? {
        if shouldThrow { throw URLError(.badServerResponse) }
        return profileToReturn
    }

    func saveProfile(_ profile: AthleteProfile) async throws -> AthleteProfile {
        if shouldThrow { throw URLError(.badServerResponse) }
        self.profileToReturn = profile
        return profile
    }

    func getBodyweightHistory() async throws -> [BodyweightEntry] {
        if shouldThrow { throw URLError(.badServerResponse) }
        return historyToReturn
    }

    func logBodyweight(entry: BodyweightEntry) async throws -> BodyweightEntry {
        if shouldThrow { throw URLError(.badServerResponse) }
        let newEntry = BodyweightEntry(id: (historyToReturn.map(\.id).max() ?? 0) + 1, weightKg: entry.weightKg, measuredDate: entry.measuredDate, notes: entry.notes)
        historyToReturn.append(newEntry)
        return newEntry
    }

    func deleteBodyweight(id: Int) async throws {
        if shouldThrow { throw URLError(.badServerResponse) }
        historyToReturn.removeAll { $0.id == id }
    }

    func calculateRelativeStrength(totalKg: Double, bodyweightKg: Double, gender: Gender, formula: ScoringFormula) async throws -> RelativeStrengthScore {
        if shouldThrow { throw URLError(.badServerResponse) }
        return RelativeStrengthCalculator.calculate(totalKg: totalKg, bodyweightKg: bodyweightKg, gender: gender, formula: formula)
    }
}

private final class MockTrainingRepositoryForAthlete: TrainingRepository, @unchecked Sendable {
    var trainingsToReturn: [Training] = []
    var shouldThrow: Bool = false

    func getTrainings() async throws -> [Training] {
        if shouldThrow { throw URLError(.badServerResponse) }
        return trainingsToReturn
    }

    func getTraining(id: Int) async throws -> Training? { nil }
    func createTrainingFromSession(sessionId: Int) async throws -> Training { throw URLError(.unsupportedURL) }
    func startTraining(id: Int) async throws -> Training { throw URLError(.unsupportedURL) }
    func pauseTraining(id: Int) async throws -> Training { throw URLError(.unsupportedURL) }
    func resumeTraining(id: Int) async throws -> Training { throw URLError(.unsupportedURL) }
    func completeTraining(id: Int, overallRpe: Double?, notes: String?) async throws -> Training { throw URLError(.unsupportedURL) }
    func cancelTraining(id: Int) async throws -> Training { throw URLError(.unsupportedURL) }
    func logSet(trainingId: Int, exerciseId: Int, set: ResistanceSet) async throws -> ResistanceSet { set }
    func updateSet(trainingId: Int, exerciseId: Int, set: ResistanceSet) async throws -> ResistanceSet { set }
    func deleteSet(trainingId: Int, exerciseId: Int, setNumber: Int) async throws {}
}

@Suite("Athlete Profile ViewModel Tests")
@MainActor
struct AthleteProfileViewModelTests {

    @Test("AthleteProfileViewModel: load, Big 3 PRs extraction, and relative strength computation")
    func testLoadAndCompute() async {
        let athRepo = MockAthleteRepository()
        let trRepo = MockTrainingRepositoryForAthlete()

        // Create completed training with Squat, Bench, Deadlift
        let set1 = ResistanceSet(setNumber: 1, setType: .normal, weightKg: 140.0, repetitions: 5, rpe: 8.0, isCompleted: true)
        let set2 = ResistanceSet(setNumber: 2, setType: .normal, weightKg: 160.0, repetitions: 3, rpe: 9.0, isCompleted: true)
        let ex1 = SessionExerciseItem(orderIndex: 1, exerciseId: 1, exerciseName: "Barbell Back Squat", sets: [set1, set2])

        let set3 = ResistanceSet(setNumber: 1, setType: .normal, weightKg: 100.0, repetitions: 5, rpe: 8.0, isCompleted: true)
        let ex2 = SessionExerciseItem(orderIndex: 2, exerciseId: 2, exerciseName: "Barbell Bench Press", sets: [set3])

        let set4 = ResistanceSet(setNumber: 1, setType: .normal, weightKg: 200.0, repetitions: 5, rpe: 8.5, isCompleted: true)
        let ex3 = SessionExerciseItem(orderIndex: 3, exerciseId: 3, exerciseName: "Conventional Deadlift", sets: [set4])

        let training = Training(
            id: 1,
            name: "Powerlifting Test",
            trainingDate: Date(),
            status: .completed,
            loggedExercises: [ex1, ex2, ex3]
        )
        trRepo.trainingsToReturn = [training]

        let vm = AthleteProfileViewModel(athleteRepository: athRepo, trainingRepository: trRepo)
        await vm.loadProfile()

        #expect(vm.profile.displayName == "Test Lifter")
        #expect(vm.bodyweightHistory.count == 2)
        #expect(vm.trendPoints.count == 2)
        #expect(vm.squatPrKg == 160.0)
        #expect(vm.benchPrKg == 100.0)
        #expect(vm.deadliftPrKg == 200.0)
        #expect(vm.bigThreeTotalKg == 460.0)
        #expect(vm.currentWeightKg == 80.5)
        #expect(vm.currentEmaKg > 80.0)
        #expect(vm.relativeRatio > 5.0)
        #expect(vm.relativeStrengthScore != nil)
        #expect(vm.relativeStrengthScore?.tier == .intermediate || vm.relativeStrengthScore?.tier == .advanced)

        // Switch formula
        await vm.setScoringFormula(.wilks)
        #expect(vm.selectedFormula == .wilks)
        #expect(vm.relativeStrengthScore?.formula == .wilks)

        // Save profile
        var updated = vm.profile
        updated.displayName = "Super Athlete"
        await vm.saveProfile(updated)
        #expect(vm.profile.displayName == "Super Athlete")

        // Log bodyweight
        await vm.logBodyweight(weightKg: 81.0, notes: "Morning")
        #expect(vm.bodyweightHistory.count == 3)
        #expect(vm.currentWeightKg == 81.0)

        // Delete bodyweight
        await vm.deleteBodyweight(id: 1)
        #expect(vm.bodyweightHistory.count == 2)
    }

    @Test("AthleteProfileViewModel: error handling branches")
    func testErrorHandling() async {
        let athRepo = MockAthleteRepository()
        let trRepo = MockTrainingRepositoryForAthlete()
        athRepo.shouldThrow = true

        let vm = AthleteProfileViewModel(athleteRepository: athRepo, trainingRepository: trRepo)
        await vm.loadProfile()
        #expect(vm.errorMessage != nil)

        await vm.saveProfile(AthleteProfile(id: 1, displayName: "Fail"))
        #expect(vm.errorMessage != nil)

        await vm.logBodyweight(weightKg: 85.0)
        #expect(vm.errorMessage != nil)

        await vm.deleteBodyweight(id: 2)
        #expect(vm.errorMessage != nil)

        // Directly call recalculateRelativeStrength when repository throws
        await vm.recalculateRelativeStrength()
        #expect(vm.relativeStrengthScore != nil)
    }

    @Test("AthleteProfileViewModel: fallback when no history exists and zero weight ratio")
    func testFallbackWithoutHistory() async {
        let athRepo = MockAthleteRepository()
        athRepo.historyToReturn = []
        let trRepo = MockTrainingRepositoryForAthlete()

        let vm = AthleteProfileViewModel(athleteRepository: athRepo, trainingRepository: trRepo)
        await vm.loadProfile()

        #expect(vm.currentWeightKg == 75.0)
        #expect(vm.currentEmaKg == 75.0)
        #expect(vm.bigThreeTotalKg == 0.0)

        // Zero weight ratio guard branch
        vm.bodyweightHistory = [BodyweightEntry(id: 99, weightKg: 0.0, measuredDate: Date())]
        #expect(vm.relativeRatio == 0.0)
    }
}
