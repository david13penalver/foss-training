import Foundation
import Testing
@testable import FOSSTraining

private final class MockAthleteRepoForLog: AthleteRepository, @unchecked Sendable {
    var loggedEntries: [BodyweightEntry] = []
    var shouldThrow: Bool = false

    func getProfile() async throws -> AthleteProfile? { nil }
    func saveProfile(_ profile: AthleteProfile) async throws -> AthleteProfile { profile }
    func getBodyweightHistory() async throws -> [BodyweightEntry] { loggedEntries }

    func logBodyweight(entry: BodyweightEntry) async throws -> BodyweightEntry {
        if shouldThrow { throw URLError(.badServerResponse) }
        loggedEntries.append(entry)
        return entry
    }

    func deleteBodyweight(id: Int) async throws {}
    func calculateRelativeStrength(totalKg: Double, bodyweightKg: Double, gender: Gender, formula: ScoringFormula) async throws -> RelativeStrengthScore {
        RelativeStrengthCalculator.calculate(totalKg: totalKg, bodyweightKg: bodyweightKg, gender: gender, formula: formula)
    }
}

@Suite("Bodyweight Log ViewModel Tests")
@MainActor
struct BodyweightLogViewModelTests {

    @Test("BodyweightLogViewModel: quick adjust buttons, same as yesterday, and string input")
    func testQuickAdjustments() {
        let repo = MockAthleteRepoForLog()
        let vm = BodyweightLogViewModel(
            athleteRepository: repo,
            initialWeight: 80.0,
            lastWeight: 79.5
        )

        #expect(vm.weightKg == 80.0)
        #expect(vm.weightString == "80.0")

        // Adjust +0.2
        vm.adjustWeight(delta: 0.2)
        #expect(vm.weightKg == 80.2)
        #expect(vm.weightString == "80.2")

        // Adjust -0.2
        vm.adjustWeight(delta: -0.2)
        #expect(vm.weightKg == 80.0)

        // Same as yesterday
        vm.setSameAsYesterday()
        #expect(vm.weightKg == 79.5)
        #expect(vm.weightString == "79.5")

        // Clamping min / max
        vm.adjustWeight(delta: -100.0)
        #expect(vm.weightKg == 20.0)

        vm.adjustWeight(delta: 500.0)
        #expect(vm.weightKg == 300.0)

        // String update with comma
        vm.updateWeightFromString("82,4")
        #expect(vm.weightKg == 82.4)

        // String update with invalid text does not update weightKg
        vm.updateWeightFromString("abc")
        #expect(vm.weightKg == 82.4)

        // Nil initialWeight fallbacks
        let vmLastOnly = BodyweightLogViewModel(athleteRepository: repo, initialWeight: nil, lastWeight: 85.0)
        #expect(vmLastOnly.weightKg == 85.0)

        let vmAllNil = BodyweightLogViewModel(athleteRepository: repo, initialWeight: nil, lastWeight: nil)
        #expect(vmAllNil.weightKg == 75.0)
        vmAllNil.setSameAsYesterday()
        #expect(vmAllNil.weightKg == 75.0)
    }

    @Test("BodyweightLogViewModel: save validation, whitespace notes, and success")
    func testSaveSuccessAndValidation() async {
        let repo = MockAthleteRepoForLog()
        let vm = BodyweightLogViewModel(athleteRepository: repo, initialWeight: 82.0)
        vm.notes = "  Morning fasted weigh-in  "

        let success = await vm.save()
        #expect(success == true)
        #expect(vm.isSaved == true)
        #expect(repo.loggedEntries.count == 1)
        #expect(repo.loggedEntries[0].weightKg == 82.0)
        #expect(repo.loggedEntries[0].notes == "Morning fasted weigh-in")

        // Empty notes become nil
        let vmEmptyNotes = BodyweightLogViewModel(athleteRepository: repo, initialWeight: 82.0)
        vmEmptyNotes.notes = "   "
        let success2 = await vmEmptyNotes.save()
        #expect(success2 == true)
        #expect(repo.loggedEntries[1].notes == nil)

        // Invalid weight validation
        let vmInvalid = BodyweightLogViewModel(athleteRepository: repo, initialWeight: 10.0)
        vmInvalid.weightString = "10.0"
        let invalidLow = await vmInvalid.save()
        #expect(invalidLow == false)
        #expect(vmInvalid.errorMessage != nil)

        vmInvalid.weightString = "500.0"
        let invalidHigh = await vmInvalid.save()
        #expect(invalidHigh == false)

        vmInvalid.weightString = "not-a-number"
        let invalidNaN = await vmInvalid.save()
        #expect(invalidNaN == false)
    }

    @Test("BodyweightLogViewModel: repository error handling")
    func testRepositoryError() async {
        let repo = MockAthleteRepoForLog()
        repo.shouldThrow = true
        let vm = BodyweightLogViewModel(athleteRepository: repo, initialWeight: 80.0)

        let success = await vm.save()
        #expect(success == false)
        #expect(vm.isSaved == false)
        #expect(vm.errorMessage != nil)
    }
}
