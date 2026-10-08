import Testing
import Foundation
@testable import FOSSTraining

// MARK: - Mock Analytics & Exercise Repositories

final class MockAnalyticsRepository: AnalyticsRepository, @unchecked Sendable {
    var shouldFailDashboard = false
    var shouldFailProgression = false
    var shouldFail1Rm = false

    var acwrToReturn = WorkloadRatio(
        targetDate: Date(),
        acuteWorkload: 1200.0,
        acuteDailyAverage: 171.43,
        chronicWorkload: 4000.0,
        chronicWeeklyAverage: 1000.0,
        chronicDailyAverage: 142.86,
        acwr: 1.20,
        riskZone: .optimal,
        deloadRecommended: false,
        recommendation: "Optimal workload",
        dailyWorkloads: []
    )

    var volumeToReturn = WeeklyMuscleVolume(
        startDate: Date(),
        endDate: Date().addingTimeInterval(6 * 86400),
        totalWorkingSets: 20,
        totalVolumeKg: 10000.0,
        muscleVolumes: [
            MuscleGroupVolume(muscleGroup: "Chest", muscleGroupName: "Chest", directSets: 10, indirectSets: 4, effectiveSets: 12.0, totalVolumeKg: 5000.0, status: .adaptive)
        ],
        pushPullRatio: 1.0,
        upperLowerRatio: 1.2,
        neglectedMuscleGroups: [],
        optimalMuscleGroups: ["Chest"],
        overtrainedMuscleGroups: [],
        recommendations: []
    )

    var prsToReturn: [PersonalRecord] = [
        PersonalRecord(exerciseId: 1, exerciseName: "Bench Press", recordType: .maxWeight, value: 120.0, unit: "kg", achievedDate: Date(), trainingId: 1)
    ]

    var progressionToReturn = ExerciseProgression(
        exerciseId: 1,
        exerciseName: "Bench Press",
        formula: .epley,
        startDate: Date().addingTimeInterval(-90 * 86400),
        endDate: Date(),
        totalSessions: 8,
        initial1RmKg: 100.0,
        latest1RmKg: 120.0,
        absolute1RmGainKg: 20.0,
        relative1RmGainPercentage: 20.0,
        allTimeBest1RmKg: 120.0,
        allTimeBestTopWeightKg: 110.0,
        allTimeMaxVolumeKg: 2400.0,
        trend: .improving,
        dataPoints: []
    )

    var estimatesToReturn: [OneRepMaxEstimate] = [
        OneRepMaxEstimate(formula: .epley, estimatedOneRepMax: 116.67, percentages: [TrainingPercentage(percentage: 95, weightKg: 110.84)])
    ]

    func calculateOneRepMax(weightKg: Double, reps: Int) async throws -> [OneRepMaxEstimate] {
        if shouldFail1Rm {
            throw NSError(domain: "MockAnalytics", code: 1, userInfo: [NSLocalizedDescriptionKey: "1RM calculation failed"])
        }
        return estimatesToReturn
    }

    func getPersonalRecords(exerciseId: Int?) async throws -> [PersonalRecord] {
        if shouldFailDashboard {
            throw NSError(domain: "MockAnalytics", code: 2, userInfo: [NSLocalizedDescriptionKey: "PRs fetch failed"])
        }
        return prsToReturn
    }

    func calculateAcwr(asOfDate: Date?) async throws -> WorkloadRatio {
        if shouldFailDashboard {
            throw NSError(domain: "MockAnalytics", code: 3, userInfo: [NSLocalizedDescriptionKey: "ACWR calculation failed"])
        }
        return acwrToReturn
    }

    func getWeeklyMuscleVolume(weekStartDate: Date?) async throws -> WeeklyMuscleVolume {
        if shouldFailDashboard {
            throw NSError(domain: "MockAnalytics", code: 4, userInfo: [NSLocalizedDescriptionKey: "Volume fetch failed"])
        }
        return volumeToReturn
    }

    func getExerciseProgression(exerciseId: Int, months: Int) async throws -> ExerciseProgression {
        if shouldFailProgression {
            throw NSError(domain: "MockAnalytics", code: 5, userInfo: [NSLocalizedDescriptionKey: "Progression fetch failed"])
        }
        return progressionToReturn
    }

    func calculateHeartRateZones(restingHr: Int, maxHr: Int, method: HeartRateZoneMethod) async throws -> HeartRateZones {
        HeartRateZones(maxHr: maxHr, method: method, zones: [])
    }
}

final class MockExerciseRepository: ExerciseRepository, @unchecked Sendable {
    var shouldFail = false
    var exercisesToReturn: [Exercise] = []

    func getExercises(category: ExerciseCategory?, search: String?) async throws -> [Exercise] {
        if shouldFail {
            throw NSError(domain: "MockExercise", code: 1, userInfo: [NSLocalizedDescriptionKey: "Exercise fetch failed"])
        }
        return exercisesToReturn
    }

    func getExercise(id: Int) async throws -> Exercise? { nil }
    func saveExercise(_ exercise: Exercise) async throws -> Exercise { exercise }
    func deleteExercise(id: Int) async throws {}
}

// MARK: - Unit Tests

@Suite("Analytics Dashboard & 1RM Calculator ViewModels Tests")
@MainActor
struct AnalyticsDashboardViewModelTests {

    private func createTestExercise(id: Int = 1, name: String = "Bench Press") -> Exercise {
        Exercise(
            id: id,
            name: name,
            description: "Chest press",
            primaryCategory: .resistance,
            secondaryCategories: [],
            primaryMuscleGroup: "Chest",
            secondaryMuscleGroups: ["Triceps"],
            movementPattern: .push,
            equipmentRequired: [.barbell],
            difficultyLevel: .intermediate,
            tags: ["compound"]
        )
    }

    @Test("AnalyticsTab enum cases, ids, and displayNames")
    func testAnalyticsTabEnum() {
        for tab in AnalyticsTab.allCases {
            #expect(!tab.id.isEmpty)
            #expect(!tab.displayName.isEmpty)
        }
    }

    @Test("AnalyticsDashboardViewModel: loadDashboard success with automatic exercise selection")
    func testLoadDashboardSuccess() async {
        let analyticsMock = MockAnalyticsRepository()
        let exerciseMock = MockExerciseRepository()
        let ex1 = createTestExercise(id: 1, name: "Bench Press")
        let ex2 = createTestExercise(id: 2, name: "Squat")
        exerciseMock.exercisesToReturn = [ex1, ex2]

        let vm = AnalyticsDashboardViewModel(
            analyticsRepository: analyticsMock,
            exerciseRepository: exerciseMock
        )

        vm.selectedTab = .hypertrophy
        #expect(vm.selectedTab == .hypertrophy)

        #expect(vm.isLoading == false)
        await vm.loadDashboard()

        #expect(vm.isLoading == false)
        #expect(vm.errorMessage == nil)
        #expect(vm.workloadRatio != nil)
        #expect(vm.weeklyMuscleVolume != nil)
        #expect(vm.personalRecords.count == 1)
        #expect(vm.exercises.count == 2)
        #expect(vm.selectedExerciseForProgression?.id == 1)
        #expect(vm.exerciseProgression != nil)

        // Load dashboard again with pre-selected exercise (tests if selectedExerciseForProgression == nil false branch)
        await vm.loadDashboard()
        #expect(vm.selectedExerciseForProgression?.id == 1)
    }

    @Test("AnalyticsDashboardViewModel: loadDashboard with empty exercises leaves selection nil")
    func testLoadDashboardEmptyExercises() async {
        let analyticsMock = MockAnalyticsRepository()
        let exerciseMock = MockExerciseRepository()
        exerciseMock.exercisesToReturn = []

        let vm = AnalyticsDashboardViewModel(
            analyticsRepository: analyticsMock,
            exerciseRepository: exerciseMock
        )

        await vm.loadDashboard()
        #expect(vm.exercises.isEmpty)
        #expect(vm.selectedExerciseForProgression == nil)
        #expect(vm.exerciseProgression == nil)

        // setProgressionMonths when selected exercise is nil (tests if let selected false branch)
        await vm.setProgressionMonths(6)
        #expect(vm.progressionMonths == 6)
    }

    @Test("AnalyticsDashboardViewModel: loadDashboard failure sets errorMessage")
    func testLoadDashboardFailure() async {
        let analyticsMock = MockAnalyticsRepository()
        analyticsMock.shouldFailDashboard = true
        let exerciseMock = MockExerciseRepository()

        let vm = AnalyticsDashboardViewModel(
            analyticsRepository: analyticsMock,
            exerciseRepository: exerciseMock
        )

        await vm.loadDashboard()
        #expect(vm.errorMessage != nil)
        #expect(vm.isLoading == false)
    }

    @Test("AnalyticsDashboardViewModel: selectExerciseForProgression and setProgressionMonths")
    func testSelectExerciseAndSetProgressionMonths() async {
        let analyticsMock = MockAnalyticsRepository()
        let exerciseMock = MockExerciseRepository()
        let ex1 = createTestExercise(id: 1, name: "Bench Press")
        let ex2 = createTestExercise(id: 2, name: "Squat")

        let vm = AnalyticsDashboardViewModel(
            analyticsRepository: analyticsMock,
            exerciseRepository: exerciseMock
        )

        await vm.selectExerciseForProgression(ex2)
        #expect(vm.selectedExerciseForProgression?.id == 2)
        #expect(vm.exerciseProgression != nil)

        await vm.setProgressionMonths(6)
        #expect(vm.progressionMonths == 6)

        // Test progression failure
        analyticsMock.shouldFailProgression = true
        await vm.loadProgression(for: ex1)
        #expect(vm.errorMessage != nil)
    }

    @Test("OneRepMaxCalculatorViewModel: synchronous calculation, weight, reps, formula mutations")
    func testOneRepMaxCalculatorViewModelSynchronous() {
        let analyticsMock = MockAnalyticsRepository()
        let vm = OneRepMaxCalculatorViewModel(analyticsRepository: analyticsMock)

        #expect(vm.weightKg == 100.0)
        #expect(vm.repetitions == 5)
        #expect(vm.selectedFormula == .epley)
        #expect(vm.currentEstimated1Rm == 116.67)
        #expect(!vm.percentages.isEmpty)

        // Mutate weight
        vm.setWeight(120.0)
        #expect(vm.weightKg == 120.0)
        #expect(vm.currentEstimated1Rm == 140.0)

        // Negative weight clamps to 0
        vm.setWeight(-50.0)
        #expect(vm.weightKg == 0.0)

        // Mutate reps
        vm.setReps(10)
        #expect(vm.repetitions == 10)

        // Clamping reps
        vm.setReps(0)
        #expect(vm.repetitions == 1)
        vm.setReps(50)
        #expect(vm.repetitions == 36)

        // Mutate formula
        vm.setWeight(100.0)
        vm.setReps(5)
        vm.setFormula(.brzycki)
        #expect(vm.selectedFormula == .brzycki)
        #expect(vm.currentEstimated1Rm == 112.5)

        // Test updateActiveEstimate fallback branch when no matching formula is in estimates
        vm.estimates = []
        vm.updateActiveEstimate()
        #expect(vm.currentEstimated1Rm == 112.5)
        #expect(!vm.percentages.isEmpty)
    }

    @Test("OneRepMaxCalculatorViewModel: calculateAsync success and failure")
    func testOneRepMaxCalculatorViewModelAsync() async {
        let analyticsMock = MockAnalyticsRepository()
        let vm = OneRepMaxCalculatorViewModel(analyticsRepository: analyticsMock)

        // Async success
        await vm.calculateAsync()
        #expect(vm.errorMessage == nil)
        #expect(vm.isLoading == false)
        #expect(!vm.estimates.isEmpty)

        // Async failure
        analyticsMock.shouldFail1Rm = true
        await vm.calculateAsync()
        #expect(vm.errorMessage != nil)
        #expect(vm.isLoading == false)
    }
}
