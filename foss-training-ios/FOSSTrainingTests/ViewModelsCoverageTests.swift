import Testing
import Foundation
import SwiftData
@testable import FOSSTraining

private final class ThrowingExerciseRepository: ExerciseRepository, @unchecked Sendable {
    func getExercises(category: ExerciseCategory?, search: String?) async throws -> [Exercise] {
        throw NSError(domain: "Test", code: 500, userInfo: [NSLocalizedDescriptionKey: "Simulated load failure"])
    }
    func getExercise(id: Int) async throws -> Exercise? {
        throw NSError(domain: "Test", code: 500, userInfo: [NSLocalizedDescriptionKey: "Simulated get failure"])
    }
    func saveExercise(_ exercise: Exercise) async throws -> Exercise {
        throw NSError(domain: "Test", code: 500, userInfo: [NSLocalizedDescriptionKey: "Simulated save failure"])
    }
    func deleteExercise(id: Int) async throws {
        throw NSError(domain: "Test", code: 500, userInfo: [NSLocalizedDescriptionKey: "Simulated delete failure"])
    }
}

private final class ThrowingSessionRepository: SessionRepository, @unchecked Sendable {
    func getSessions() async throws -> [Session] {
        throw NSError(domain: "Test", code: 500, userInfo: [NSLocalizedDescriptionKey: "Simulated session failure"])
    }
    func getSession(id: Int) async throws -> Session? { nil }
    func saveSession(_ session: Session) async throws -> Session { session }
    func deleteSession(id: Int) async throws {}
    func cloneSession(id: Int) async throws -> Session {
        throw NSError(domain: "Test", code: 500, userInfo: [NSLocalizedDescriptionKey: "Clone error"])
    }
}

private final class ThrowingTrainingRepository: TrainingRepository, @unchecked Sendable {
    func getTrainings() async throws -> [Training] { [] }
    func getTraining(id: Int) async throws -> Training? { nil }
    func createTrainingFromSession(sessionId: Int) async throws -> Training {
        throw NSError(domain: "Test", code: 500, userInfo: [NSLocalizedDescriptionKey: "Create error"])
    }
    func startTraining(id: Int) async throws -> Training {
        throw NSError(domain: "Test", code: 500, userInfo: [NSLocalizedDescriptionKey: "Start error"])
    }
    func pauseTraining(id: Int) async throws -> Training {
        throw NSError(domain: "Test", code: 500, userInfo: [NSLocalizedDescriptionKey: "Pause error"])
    }
    func resumeTraining(id: Int) async throws -> Training {
        throw NSError(domain: "Test", code: 500, userInfo: [NSLocalizedDescriptionKey: "Resume error"])
    }
    func completeTraining(id: Int, overallRpe: Double?, notes: String?) async throws -> Training {
        throw NSError(domain: "Test", code: 500, userInfo: [NSLocalizedDescriptionKey: "Complete error"])
    }
    func cancelTraining(id: Int) async throws -> Training {
        throw NSError(domain: "Test", code: 500, userInfo: [NSLocalizedDescriptionKey: "Cancel error"])
    }
    func logSet(trainingId: Int, exerciseId: Int, set: ResistanceSet) async throws -> ResistanceSet { set }
    func updateSet(trainingId: Int, exerciseId: Int, set: ResistanceSet) async throws -> ResistanceSet {
        throw NSError(domain: "Test", code: 500, userInfo: [NSLocalizedDescriptionKey: "Update set error"])
    }
    func deleteSet(trainingId: Int, exerciseId: Int, setNumber: Int) async throws {}
}
private final class SettingsMockURLProtocol: URLProtocol, @unchecked Sendable {
    nonisolated(unsafe) static var requestHandler: ((URLRequest) throws -> (URLResponse, Data?))?

    override class func canInit(with request: URLRequest) -> Bool {
        request.url?.path.contains("/backup") == true
    }
    override class func canonicalRequest(for request: URLRequest) -> URLRequest { request }

    override func startLoading() {
        guard let handler = SettingsMockURLProtocol.requestHandler else {
            client?.urlProtocol(self, didFailWithError: NSError(domain: "SettingsMockURLProtocol", code: -1))
            return
        }

        do {
            let (response, data) = try handler(request)
            client?.urlProtocol(self, didReceive: response, cacheStoragePolicy: .notAllowed)
            if let data = data {
                client?.urlProtocol(self, didLoad: data)
            }
            client?.urlProtocolDidFinishLoading(self)
        } catch {
            client?.urlProtocol(self, didFailWithError: error)
        }
    }

    override func stopLoading() {}
}

@Suite("ViewModels Branch, State & Error Handling Coverage Tests")
@MainActor
struct ViewModelsCoverageTests {

    private func createTestContainer() throws -> ModelContainer {
        let schema = Schema([
            SDExercise.self,
            SDSession.self,
            SDSessionExercise.self,
            SDResistanceSet.self,
            SDTraining.self,
            SDBodyweightEntry.self
        ])
        let config = ModelConfiguration(schema: schema, isStoredInMemoryOnly: true)
        return try ModelContainer(for: schema, configurations: [config])
    }

    @Test("ExerciseListViewModel: error handling and category toggling")
    func testExerciseListViewModelErrors() async {
        let throwingRepo = ThrowingExerciseRepository()
        let vm = ExerciseListViewModel(exerciseRepository: throwingRepo)

        await vm.loadExercises()
        #expect(vm.errorMessage != nil)
        #expect(vm.isLoading == false)

        await vm.deleteExercise(id: 1)
        #expect(vm.errorMessage != nil)

        // Toggle category on and off
        await vm.selectCategory(.resistance)
        #expect(vm.selectedCategory == .resistance)
        await vm.selectCategory(.resistance)
        #expect(vm.selectedCategory == nil)
    }

    @Test("ExerciseEditorViewModel: edit existing, remove instruction, save various categories, error branch")
    func testExerciseEditorViewModelBranches() async throws {
        let container = try createTestContainer()
        let repo = SwiftDataExerciseRepository(modelContext: container.mainContext)

        let existing = Exercise(
            id: 50,
            name: "Overhead Squat",
            description: "Deep squat with bar overhead",
            primaryCategory: .resistance,
            primaryMuscleGroup: "Quads",
            secondaryMuscleGroups: ["Core", "Shoulders"],
            movementPattern: .squat,
            enduranceType: "Running",
            mobilityType: "Dynamic",
            targetJoints: ["Shoulder", "Hip"],
            equipmentRequired: [.barbell],
            difficultyLevel: .advanced,
            stepByStepInstructions: ["Grip bar wide", "Press overhead", "Squat deep"]
        )

        let vm = ExerciseEditorViewModel(exerciseRepository: repo, exerciseToEdit: existing)
        #expect(vm.existingExerciseId == 50)
        #expect(vm.name == "Overhead Squat")
        #expect(vm.stepByStepInstructions.count == 3)

        // Add empty instruction (ignored)
        vm.newInstructionText = "   "
        vm.addInstruction()
        #expect(vm.stepByStepInstructions.count == 3)

        // Add valid instruction
        vm.newInstructionText = "Stand tall"
        vm.addInstruction()
        #expect(vm.stepByStepInstructions.count == 4)

        // Remove instruction
        vm.removeInstruction(at: IndexSet(integer: 0))
        #expect(vm.stepByStepInstructions.count == 3)

        // Save existing
        let saved = await vm.save()
        #expect(saved == true)

        // Test Endurance category save
        let enduranceVM = ExerciseEditorViewModel(exerciseRepository: repo)
        enduranceVM.name = "Rowing Intervals"
        enduranceVM.primaryCategory = .endurance
        enduranceVM.enduranceType = "Rowing"
        let savedEndurance = await enduranceVM.save()
        #expect(savedEndurance == true)

        // Test Mobility category save
        let mobilityVM = ExerciseEditorViewModel(exerciseRepository: repo)
        mobilityVM.name = "Shoulder Dislocates"
        mobilityVM.primaryCategory = .mobility
        mobilityVM.mobilityType = "Dynamic"
        mobilityVM.targetJointsText = "Shoulder"
        let savedMobility = await mobilityVM.save()
        #expect(savedMobility == true)

        // Test Throwing repo failure
        let failingVM = ExerciseEditorViewModel(exerciseRepository: ThrowingExerciseRepository())
        failingVM.name = "Leg Extension"
        failingVM.movementPattern = .isolation
        let failed = await failingVM.save()
        #expect(failed == false)
        #expect(failingVM.errorMessage != nil)

        // Test init with nil optional fields on existing exercise
        let minimalEx = Exercise(id: 60, name: "Minimal", primaryCategory: .resistance)
        let minimalVM = ExerciseEditorViewModel(exerciseRepository: repo, exerciseToEdit: minimalEx)
        #expect(minimalVM.descriptionText == "")
        #expect(minimalVM.primaryMuscleGroup == "")
        #expect(minimalVM.enduranceType == "")
        #expect(minimalVM.mobilityType == "")
    }

    @Test("SessionListViewModel: loadSessions and startWorkout with success and failure")
    func testSessionListViewModelBranches() async throws {
        let container = try createTestContainer()
        let sessionRepo = SwiftDataSessionRepository(modelContext: container.mainContext)
        let trainingRepo = SwiftDataTrainingRepository(modelContext: container.mainContext)

        let vm = SessionListViewModel(sessionRepository: sessionRepo, trainingRepository: trainingRepo)
        await vm.loadSessions()
        #expect(vm.sessions.isEmpty)

        let session = Session(id: 1, name: "Leg Day")
        _ = try await sessionRepo.saveSession(session)

        await vm.loadSessions()
        #expect(vm.sessions.count == 1)

        await vm.startWorkout(from: session)
        #expect(vm.launchedTraining != nil)
        #expect(vm.launchedTraining?.status == .inProgress)

        // Test throwing session repo
        let throwingVM = SessionListViewModel(
            sessionRepository: ThrowingSessionRepository(),
            trainingRepository: ThrowingTrainingRepository()
        )
        await throwingVM.loadSessions()
        #expect(throwingVM.errorMessage != nil)

        await throwingVM.startWorkout(from: session)
        #expect(throwingVM.errorMessage != nil)
    }

    @Test("ActiveWorkoutViewModel: rest timer tick countdown and updateSetValues")
    func testActiveWorkoutViewModelBranches() async throws {
        let container = try createTestContainer()
        let trainingRepo = SwiftDataTrainingRepository(modelContext: container.mainContext)

        var tr = Training(
            id: 200,
            name: "Chest Day",
            status: .inProgress,
            loggedExercises: [
                SessionExerciseItem(
                    orderIndex: 0,
                    exerciseId: 1,
                    exerciseName: "Bench Press",
                    sets: [ResistanceSet(setNumber: 1, weightKg: 80, repetitions: 8)]
                )
            ]
        )
        _ = SDTraining.fromDomain(tr)
        container.mainContext.insert(SDTraining.fromDomain(tr))
        try container.mainContext.save()

        let vm = ActiveWorkoutViewModel(training: tr, trainingRepository: trainingRepo)

        // Complete set to activate rest timer
        await vm.toggleSetCompleted(exerciseId: 1, setNumber: 1)
        #expect(vm.isRestTimerActive == true)
        vm.restTimerSecondsRemaining = 2

        // Tick rest timer down
        vm.tickElapsed()
        #expect(vm.restTimerSecondsRemaining == 1)

        vm.tickElapsed()
        #expect(vm.restTimerSecondsRemaining == 0)
        #expect(vm.isRestTimerActive == false)

        // updateSetValues
        await vm.updateSetValues(exerciseId: 1, setNumber: 1, weight: 85.0, reps: 10)
        #expect(vm.training.loggedExercises.first?.sets.first?.weightKg == 85.0)
        #expect(vm.training.loggedExercises.first?.sets.first?.repetitions == 10)

        // Guard exits for non-existent exercise/set
        await vm.toggleSetCompleted(exerciseId: 9999, setNumber: 1)
        await vm.toggleSetCompleted(exerciseId: 1, setNumber: 9999)
        await vm.updateSetValues(exerciseId: 9999, setNumber: 1, weight: 50, reps: 5)
        await vm.updateSetValues(exerciseId: 1, setNumber: 9999, weight: 50, reps: 5)

        // Finish workout without notes
        vm.completionNotes = ""
        await vm.finishWorkout()
        #expect(vm.isFinished == true)
        #expect(vm.isTimerRunning == false)

        // Finish workout with notes
        vm.completionNotes = "Great session"
        await vm.finishWorkout()
        #expect(vm.isFinished == true)

        // Throwing repo failure paths
        let throwingVM = ActiveWorkoutViewModel(training: tr, trainingRepository: ThrowingTrainingRepository())
        await throwingVM.toggleSetCompleted(exerciseId: 1, setNumber: 1)
        #expect(throwingVM.errorMessage?.contains("Failed to update set") == true)

        await throwingVM.updateSetValues(exerciseId: 1, setNumber: 1, weight: 90, reps: 5)
        #expect(throwingVM.errorMessage?.contains("Failed to update set values") == true)

        await throwingVM.finishWorkout()
        #expect(throwingVM.errorMessage?.contains("Failed to finish workout") == true)
    }

    @Test("AnalyticsViewModel: muscle volume calculation across all categories and bodyweight logging")
    func testAnalyticsViewModelBranches() async throws {
        let container = try createTestContainer()
        let trainingRepo = SwiftDataTrainingRepository(modelContext: container.mainContext)
        let athleteRepo = SwiftDataAthleteRepository(modelContext: container.mainContext)

        let completedTraining = Training(
            id: 300,
            name: "Full Body",
            status: .completed,
            loggedExercises: [
                SessionExerciseItem(
                    orderIndex: 0,
                    exerciseId: 1,
                    exerciseName: "Bench Press",
                    sets: [ResistanceSet(setNumber: 1, weightKg: 100, repetitions: 5, isCompleted: true)]
                ),
                SessionExerciseItem(
                    orderIndex: 1,
                    exerciseId: 2,
                    exerciseName: "Deadlift",
                    sets: [ResistanceSet(setNumber: 1, weightKg: 150, repetitions: 5, isCompleted: true)]
                ),
                SessionExerciseItem(
                    orderIndex: 2,
                    exerciseId: 3,
                    exerciseName: "Squat",
                    sets: [ResistanceSet(setNumber: 1, weightKg: 120, repetitions: 5, isCompleted: true)]
                ),
                SessionExerciseItem(
                    orderIndex: 3,
                    exerciseId: 4,
                    exerciseName: "Overhead Shoulder Press",
                    sets: [ResistanceSet(setNumber: 1, weightKg: 60, repetitions: 5, isCompleted: true)]
                ),
                SessionExerciseItem(
                    orderIndex: 4,
                    exerciseId: 5,
                    exerciseName: "Bicep Curl",
                    sets: [ResistanceSet(setNumber: 1, weightKg: 30, repetitions: 10, isCompleted: true)]
                ),
                SessionExerciseItem(
                    orderIndex: 5,
                    exerciseId: 6,
                    exerciseName: "Chest Fly",
                    sets: [ResistanceSet(setNumber: 1, weightKg: 20, repetitions: 10, isCompleted: true)]
                ),
                SessionExerciseItem(
                    orderIndex: 6,
                    exerciseId: 7,
                    exerciseName: "Push Up",
                    sets: [ResistanceSet(setNumber: 1, weightKg: 0, repetitions: 20, isCompleted: true)]
                ),
                SessionExerciseItem(
                    orderIndex: 7,
                    exerciseId: 8,
                    exerciseName: "Barbell Row",
                    sets: [ResistanceSet(setNumber: 1, weightKg: 70, repetitions: 8, isCompleted: true)]
                ),
                SessionExerciseItem(
                    orderIndex: 8,
                    exerciseId: 9,
                    exerciseName: "Leg Extension",
                    sets: [ResistanceSet(setNumber: 1, weightKg: 50, repetitions: 12, isCompleted: true)]
                ),
                SessionExerciseItem(
                    orderIndex: 9,
                    exerciseId: 10,
                    exerciseName: "Shoulder Raise",
                    sets: [ResistanceSet(setNumber: 1, weightKg: 10, repetitions: 15, isCompleted: true)]
                )
            ]
        )
        container.mainContext.insert(SDTraining.fromDomain(completedTraining))
        try container.mainContext.save()

        let vm = AnalyticsViewModel(trainingRepository: trainingRepo, athleteRepository: athleteRepo)
        await vm.loadAnalytics()
        #expect(!vm.muscleVolumes.isEmpty)
        #expect(vm.muscleVolumes.first?.id != nil)

        // Bodyweight logging: invalid value <= 20
        vm.newBodyweightString = "15"
        await vm.logCurrentBodyweight()
        #expect(vm.bodyweightHistory.isEmpty)

        // Bodyweight logging: valid value
        vm.newBodyweightString = "81.5"
        await vm.logCurrentBodyweight()
        #expect(vm.bodyweightHistory.count == 1)
        #expect(vm.bodyweightHistory.first?.weightKg == 81.5)
    }

    @Test("SettingsViewModel: test connection and local data migration")
    func testSettingsViewModelBranches() async throws {
        let env = AppEnvironment(inMemory: true)
        let vm = SettingsViewModel(appEnvironment: env)

        // Local tier succeeds connection test
        await vm.testConnection()
        #expect(vm.connectionTestStatus != nil)
        #expect(vm.errorMessage == nil)

        // Premium tier with uncontactable backend fails connection test
        env.tierMode = .premium
        await vm.testConnection()
        #expect(vm.errorMessage != nil)
        #expect(vm.connectionTestStatus == nil)

        // Test migration failure
        await vm.syncLocalDataToCloud()
        #expect(vm.errorMessage != nil)
        #expect(vm.isMigrating == false)

        // Test migration success using SettingsMockURLProtocol on URLSession.shared
        URLProtocol.registerClass(SettingsMockURLProtocol.self)
        defer { URLProtocol.unregisterClass(SettingsMockURLProtocol.self) }
        SettingsMockURLProtocol.requestHandler = { request in
            let summary = try! JSONEncoder().encode(ImportSummaryDTO(exercisesImported: 2, sessionsImported: 1, trainingsImported: 1, bodyweightsImported: 0))
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, summary)
        }
        await vm.syncLocalDataToCloud()
        #expect(vm.migrationSuccessMessage?.contains("Successfully migrated") == true)
        #expect(vm.isMigrating == false)
    }
}
