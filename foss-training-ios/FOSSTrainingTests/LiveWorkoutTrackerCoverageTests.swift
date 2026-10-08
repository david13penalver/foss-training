import Testing
import Foundation
import SwiftData
@testable import FOSSTraining

private final class LiveWorkoutMockURLProtocol: URLProtocol, @unchecked Sendable {
    nonisolated(unsafe) static var requestHandler: ((URLRequest) throws -> (URLResponse, Data?))?

    override class func canInit(with request: URLRequest) -> Bool {
        request.url?.path.contains("/api/trainings") == true
    }
    override class func canonicalRequest(for request: URLRequest) -> URLRequest { request }

    override func startLoading() {
        guard let handler = LiveWorkoutMockURLProtocol.requestHandler else {
            client?.urlProtocol(self, didFailWithError: NSError(domain: "LiveWorkoutMockURLProtocol", code: -1))
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

private final class FailingLiveWorkoutTrainingRepo: TrainingRepository, @unchecked Sendable {
    func getTrainings() async throws -> [Training] {
        throw NSError(domain: "Test", code: 500, userInfo: [NSLocalizedDescriptionKey: "Failed to load trainings"])
    }
    func getTraining(id: Int) async throws -> Training? { nil }
    func createTrainingFromSession(sessionId: Int) async throws -> Training {
        throw NSError(domain: "Test", code: 500, userInfo: [NSLocalizedDescriptionKey: "Failed to create from session"])
    }
    func startTraining(id: Int) async throws -> Training {
        throw NSError(domain: "Test", code: 500, userInfo: [NSLocalizedDescriptionKey: "Failed to start training"])
    }
    func pauseTraining(id: Int) async throws -> Training {
        throw NSError(domain: "Test", code: 500, userInfo: [NSLocalizedDescriptionKey: "Failed to pause training"])
    }
    func resumeTraining(id: Int) async throws -> Training {
        throw NSError(domain: "Test", code: 500, userInfo: [NSLocalizedDescriptionKey: "Failed to resume training"])
    }
    func completeTraining(id: Int, overallRpe: Double?, notes: String?) async throws -> Training {
        throw NSError(domain: "Test", code: 500, userInfo: [NSLocalizedDescriptionKey: "Failed to complete training"])
    }
    func cancelTraining(id: Int) async throws -> Training {
        throw NSError(domain: "Test", code: 500, userInfo: [NSLocalizedDescriptionKey: "Failed to cancel training"])
    }
    func logSet(trainingId: Int, exerciseId: Int, set: ResistanceSet) async throws -> ResistanceSet {
        throw NSError(domain: "Test", code: 500, userInfo: [NSLocalizedDescriptionKey: "Failed to log set"])
    }
    func updateSet(trainingId: Int, exerciseId: Int, set: ResistanceSet) async throws -> ResistanceSet {
        throw NSError(domain: "Test", code: 500, userInfo: [NSLocalizedDescriptionKey: "Failed to update set"])
    }
    func deleteSet(trainingId: Int, exerciseId: Int, setNumber: Int) async throws {
        throw NSError(domain: "Test", code: 500, userInfo: [NSLocalizedDescriptionKey: "Failed to delete set"])
    }
}

private final class DefaultTrainingRepo: TrainingRepository, @unchecked Sendable {
    func getTrainings() async throws -> [Training] { [] }
    func getTraining(id: Int) async throws -> Training? {
        if id == 42 {
            return Training(id: 42, name: "Found")
        }
        return nil
    }
    func createTrainingFromSession(sessionId: Int) async throws -> Training { Training(id: 1, name: "T") }
    func startTraining(id: Int) async throws -> Training { Training(id: id, name: "T") }
    func pauseTraining(id: Int) async throws -> Training { Training(id: id, name: "T") }
    func resumeTraining(id: Int) async throws -> Training { Training(id: id, name: "T") }
    func completeTraining(id: Int, overallRpe: Double?, notes: String?) async throws -> Training { Training(id: id, name: "T") }
    func cancelTraining(id: Int) async throws -> Training { Training(id: id, name: "T") }
    func logSet(trainingId: Int, exerciseId: Int, set: ResistanceSet) async throws -> ResistanceSet { set }
    func updateSet(trainingId: Int, exerciseId: Int, set: ResistanceSet) async throws -> ResistanceSet { set }
    func deleteSet(trainingId: Int, exerciseId: Int, setNumber: Int) async throws {}
}

@Suite("SPEC-03: Live Workout Tracker Full Coverage Tests", .serialized)
@MainActor
struct LiveWorkoutTrackerCoverageTests {

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

    private func createMockClient() -> NetworkClient {
        let configuration = URLSessionConfiguration.ephemeral
        configuration.protocolClasses = [LiveWorkoutMockURLProtocol.self]
        let session = URLSession(configuration: configuration)
        return NetworkClient(baseURLString: "http://mock.local", session: session)
    }

    private func createSampleTraining(id: Int = 1, status: TrainingStatus = .inProgress) -> Training {
        let set1 = ResistanceSet(setNumber: 1, setType: .normal, weightKg: 80, repetitions: 8, restSeconds: 60, isCompleted: false)
        let set2 = ResistanceSet(setNumber: 2, setType: .normal, weightKg: 85, repetitions: 6, restSeconds: 90, isCompleted: false)
        let ex = SessionExerciseItem(
            orderIndex: 0,
            exerciseId: 5,
            exerciseName: "Bench Press",
            part: .main,
            restSeconds: 90,
            sets: [set1, set2]
        )
        return Training(
            id: id,
            name: "Chest Strength",
            description: "Heavy bench day",
            trainingDate: Date(),
            startTime: Date().addingTimeInterval(-3700), // > 1 hour ago
            status: status,
            notes: "Felt strong",
            loggedExercises: [ex]
        )
    }

    // MARK: - SwiftDataTrainingRepository Coverage

    @Test("SwiftDataTrainingRepository: trainingExists, session instantiation, and edge error cases")
    func testSwiftDataTrainingRepoEdgeCases() async throws {
        let container = try createTestContainer()
        let sessionRepo = SwiftDataSessionRepository(modelContext: container.mainContext)
        let trainingRepo = SwiftDataTrainingRepository(modelContext: container.mainContext)

        // 1. Initial state
        let initialTrainings = try await trainingRepo.getTrainings()
        #expect(initialTrainings.isEmpty)

        // 2. createTrainingFromSession with missing session throws 404
        await #expect(throws: NSError.self) {
            _ = try await trainingRepo.createTrainingFromSession(sessionId: 9999)
        }

        // 3. Save a session and instantiate training
        let sessionEx = SessionExerciseItem(
            orderIndex: 0,
            exerciseId: 42,
            exerciseName: "Pull-Up",
            part: .main,
            restSeconds: 60,
            sets: [ResistanceSet(setNumber: 1, weightKg: 0, repetitions: 10, restSeconds: 60)]
        )
        let template = Session(id: 7, name: "Pull Day", exercises: [sessionEx])
        _ = try await sessionRepo.saveSession(template)

        let createdTraining = try await trainingRepo.createTrainingFromSession(sessionId: 7)
        #expect(createdTraining.id > 0)
        #expect(createdTraining.name == "Pull Day")
        #expect(createdTraining.status == .planned)

        // 4. trainingExists
        let exists = try await trainingRepo.trainingExists(id: createdTraining.id)
        #expect(exists == true)
        let notExists = try await trainingRepo.trainingExists(id: 9999)
        #expect(notExists == false)

        // 5. Missing training transitions throw error
        await #expect(throws: NSError.self) { _ = try await trainingRepo.startTraining(id: 9999) }
        await #expect(throws: NSError.self) { _ = try await trainingRepo.pauseTraining(id: 9999) }
        await #expect(throws: NSError.self) { _ = try await trainingRepo.resumeTraining(id: 9999) }
        await #expect(throws: NSError.self) { _ = try await trainingRepo.completeTraining(id: 9999, overallRpe: 8.0, notes: nil) }
        await #expect(throws: NSError.self) { _ = try await trainingRepo.cancelTraining(id: 9999) }

        // 6. Set operations missing training or missing exercise throw error
        let set = ResistanceSet(setNumber: 1, weightKg: 50, repetitions: 5)
        await #expect(throws: NSError.self) { _ = try await trainingRepo.logSet(trainingId: 9999, exerciseId: 42, set: set) }
        await #expect(throws: NSError.self) { _ = try await trainingRepo.logSet(trainingId: createdTraining.id, exerciseId: 9999, set: set) }
        await #expect(throws: NSError.self) { _ = try await trainingRepo.updateSet(trainingId: 9999, exerciseId: 42, set: set) }
        await #expect(throws: NSError.self) { _ = try await trainingRepo.updateSet(trainingId: createdTraining.id, exerciseId: 9999, set: set) }
        await #expect(throws: NSError.self) {
            let badSet = ResistanceSet(setNumber: 99, weightKg: 50, repetitions: 5)
            _ = try await trainingRepo.updateSet(trainingId: createdTraining.id, exerciseId: 42, set: badSet)
        }
        await #expect(throws: NSError.self) { try await trainingRepo.deleteSet(trainingId: 9999, exerciseId: 42, setNumber: 1) }

        // 7. Non-existent set delete does not throw
        try await trainingRepo.deleteSet(trainingId: createdTraining.id, exerciseId: 42, setNumber: 999)
        try await trainingRepo.deleteSet(trainingId: createdTraining.id, exerciseId: 9999, setNumber: 1)

        // 8. Default protocol extension trainingExists
        let defaultRepo = DefaultTrainingRepo()
        let foundDefault = try await defaultRepo.trainingExists(id: 42)
        #expect(foundDefault == true)
        let notFoundDefault = try await defaultRepo.trainingExists(id: 100)
        #expect(notFoundDefault == false)
    }

    // MARK: - RemoteTrainingRepository Coverage

    @Test("RemoteTrainingRepository: PUT updateSet, CompleteTrainingRequest with RpeDto, exists, and summary")
    func testRemoteTrainingRepositoryParity() async throws {
        let client = createMockClient()
        let repo = RemoteTrainingRepository(client: client)

        let sample = createSampleTraining(id: 12)

        let isoEncoder = JSONEncoder()
        isoEncoder.dateEncodingStrategy = .iso8601

        // 1. updateSet uses PUT
        LiveWorkoutMockURLProtocol.requestHandler = { request in
            #expect(request.httpMethod == "PUT")
            #expect(request.url?.path.contains("/api/trainings/12/exercises/5/sets/1") == true)
            let data = try! isoEncoder.encode(sample.loggedExercises[0].sets[0])
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }
        let updatedSet = try await repo.updateSet(trainingId: 12, exerciseId: 5, set: sample.loggedExercises[0].sets[0])
        #expect(updatedSet.setNumber == 1)

        // 2. completeTraining encodes RpeDto
        LiveWorkoutMockURLProtocol.requestHandler = { request in
            #expect(request.httpMethod == "POST")
            #expect(request.url?.path.contains("/api/trainings/12/complete") == true)
            let data = try! isoEncoder.encode(sample)
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }
        let completed = try await repo.completeTraining(id: 12, overallRpe: 8.5, notes: "Top set crushed")
        #expect(completed.id == 12)

        // 3. trainingExists
        LiveWorkoutMockURLProtocol.requestHandler = { request in
            #expect(request.httpMethod == "GET")
            #expect(request.url?.path.contains("/api/trainings/12/exists") == true)
            let data = try! JSONEncoder().encode(true)
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }
        let exists = try await repo.trainingExists(id: 12)
        #expect(exists == true)

        // 4. getWorkoutSummary
        let summaryDto = WorkoutSummaryDto(
            trainingId: 12,
            totalVolumeKg: 2450.0,
            totalSets: 12,
            totalReps: 80,
            durationMinutes: 52
        )
        LiveWorkoutMockURLProtocol.requestHandler = { request in
            #expect(request.httpMethod == "GET")
            #expect(request.url?.path.contains("/api/trainings/12/summary") == true)
            let data = try! JSONEncoder().encode(summaryDto)
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }
        let summary = try await repo.getWorkoutSummary(id: 12)
        #expect(summary.totalVolumeKg == 2450.0)
        #expect(summary.durationMinutes == 52)

        // 5. DTO models initializers
        let rpe = RpeDto(value: 9.0)
        #expect(rpe.value == 9.0)
        let completeReqWithRpe = CompleteTrainingRequest(rpe: rpe, notes: "Max effort")
        #expect(completeReqWithRpe.rpe?.value == 9.0)
        #expect(completeReqWithRpe.notes == "Max effort")
    }

    // MARK: - ActivityKit Live Activity Coverage

    @Test("ActivityKit: WorkoutActivityAttributes and WorkoutActivityManager")
    func testActivityKitManager() async throws {
        let attributes = WorkoutActivityAttributes(workoutName: "Hypertrophy Push", startTime: Date())
        #expect(attributes.workoutName == "Hypertrophy Push")

        let contentState = WorkoutActivityAttributes.ContentState(
            currentExerciseName: "Incline Press",
            currentSetNumber: 2,
            totalSets: 4,
            restTimeRemaining: 90,
            isRestActive: true
        )
        #expect(contentState.currentExerciseName == "Incline Press")
        #expect(contentState.isRestActive == true)
        #expect(contentState.restTimeRemaining == 90)

        // 1. areActivitiesEnabled default
        WorkoutActivityManager.forceActivitiesEnabled = nil
        let manager = WorkoutActivityManager.shared
        _ = manager.areActivitiesEnabled

        // 2. Default closures exercise with real ActivityKit lifecycle
        let realId = try WorkoutActivityManager.defaultActivityStarter(attributes, contentState)
        #expect(!realId.isEmpty)
        await WorkoutActivityManager.defaultActivityUpdater(realId, contentState)
        await WorkoutActivityManager.defaultActivityEnder(realId)

        // 3. Early return when disabled
        WorkoutActivityManager.forceActivitiesEnabled = false
        manager.startActivity(workoutName: "Disabled")
        #expect(manager.activeActivityId == nil)

        // 4. Real activity through manager lifecycle
        WorkoutActivityManager.forceActivitiesEnabled = true
        WorkoutActivityManager.activityStarter = WorkoutActivityManager.defaultActivityStarter
        WorkoutActivityManager.activityUpdater = WorkoutActivityManager.defaultActivityUpdater
        WorkoutActivityManager.activityEnder = WorkoutActivityManager.defaultActivityEnder
        manager.startActivity(workoutName: "Live")
        #expect(manager.activeActivityId != nil)
        await manager.updateActivity(exerciseName: "Deadlift", currentSet: 1, totalSets: 3, restRemaining: 90, isRestActive: true)
        await manager.endActivity()
        #expect(manager.activeActivityId == nil)

        // 5. Custom activityStarter throwing
        WorkoutActivityManager.activityStarter = { _, _ in
            throw NSError(domain: "test", code: 1)
        }
        manager.startActivity(workoutName: "Live Error")
        #expect(manager.activeActivityId == nil)

        // 6. Custom activityStarter succeeding
        WorkoutActivityManager.activityStarter = { _, _ in "test-act-123" }
        manager.startActivity(workoutName: "Live Success")
        #expect(manager.activeActivityId == "test-act-123")

        // 7. Custom activityUpdater
        nonisolated(unsafe) var updatedId: String? = nil
        WorkoutActivityManager.activityUpdater = { id, state in
            updatedId = id
        }
        await manager.updateActivity(exerciseName: "Squat", currentSet: 1, totalSets: 3, restRemaining: 60, isRestActive: true)
        #expect(updatedId == "test-act-123")

        // 8. Custom activityEnder
        nonisolated(unsafe) var endedId: String? = nil
        WorkoutActivityManager.activityEnder = { id in
            endedId = id
        }
        await manager.endActivity()
        #expect(endedId == "test-act-123")
        #expect(manager.activeActivityId == nil)

        // 9. Early return when activeActivityId is nil
        await manager.updateActivity(exerciseName: "Squat", currentSet: 1, totalSets: 3, restRemaining: nil, isRestActive: false)
        await manager.endActivity()

        // Reset
        WorkoutActivityManager.forceActivitiesEnabled = nil
        WorkoutActivityManager.activityStarter = WorkoutActivityManager.defaultActivityStarter
        WorkoutActivityManager.activityUpdater = WorkoutActivityManager.defaultActivityUpdater
        WorkoutActivityManager.activityEnder = WorkoutActivityManager.defaultActivityEnder
    }

    // MARK: - ActiveWorkoutViewModel Full Coverage

    @Test("ActiveWorkoutViewModel: stopwatch, rest timer adjustments, set CRUD, lifecycle, error branches")
    func testActiveWorkoutViewModel() async throws {
        let container = try createTestContainer()
        let trainingRepo = SwiftDataTrainingRepository(modelContext: container.mainContext)

        let sessionEx = SessionExerciseItem(
            orderIndex: 0,
            exerciseId: 10,
            exerciseName: "Deadlift",
            part: .main,
            restSeconds: 120,
            sets: [
                ResistanceSet(setNumber: 1, weightKg: 140, repetitions: 5, restSeconds: 120),
                ResistanceSet(setNumber: 2, weightKg: 150, repetitions: 3, restSeconds: 0) // zero rest interval
            ]
        )
        let training = Training(
            id: 20,
            name: "Deadlift Day",
            trainingDate: Date(),
            startTime: Date().addingTimeInterval(-4000), // > 1h
            status: .inProgress,
            loggedExercises: [sessionEx]
        )
        let savedSD = SDTraining.fromDomain(training)
        container.mainContext.insert(savedSD)
        try container.mainContext.save()

        let vm = ActiveWorkoutViewModel(training: training, trainingRepository: trainingRepo)
        #expect(vm.isTimerRunning == true)
        #expect(vm.elapsedSeconds >= 4000)
        #expect(vm.formattedElapsed.contains(":"))

        // 1. Stopwatch tick
        let prevElapsed = vm.elapsedSeconds
        vm.tickElapsed()
        #expect(vm.elapsedSeconds == prevElapsed + 1)

        // 2. Rest timer countdown
        vm.restTimerSecondsRemaining = 2
        vm.isRestTimerActive = true
        #expect(vm.formattedRestTimer == "00:02")

        vm.tickElapsed()
        #expect(vm.restTimerSecondsRemaining == 1)
        #expect(vm.isRestTimerActive == true)

        vm.tickElapsed()
        #expect(vm.restTimerSecondsRemaining == 0)
        #expect(vm.isRestTimerActive == false)

        // 3. Rest timer adjustments
        vm.adjustRestTimer(by: 30)
        #expect(vm.restTimerSecondsRemaining == 30)
        #expect(vm.isRestTimerActive == true)

        vm.adjustRestTimer(by: -15)
        #expect(vm.restTimerSecondsRemaining == 15)

        vm.adjustRestTimer(by: -30) // goes below 0 -> clamped to 0
        #expect(vm.restTimerSecondsRemaining == 0)
        #expect(vm.isRestTimerActive == false)

        vm.adjustRestTimer(by: 45)
        vm.skipRestTimer()
        #expect(vm.restTimerSecondsRemaining == 0)
        #expect(vm.isRestTimerActive == false)

        // Elapsed format with 0 hours
        vm.elapsedSeconds = 250 // 04:10
        #expect(vm.formattedElapsed == "04:10")

        // 4. toggleSetCompleted (set 1 has restSeconds: 120)
        await vm.toggleSetCompleted(exerciseId: 10, setNumber: 1)
        #expect(vm.training.loggedExercises[0].sets[0].isCompleted == true)
        #expect(vm.isRestTimerActive == true)
        #expect(vm.restTimerSecondsRemaining == 120)

        // Untoggle set 1
        await vm.toggleSetCompleted(exerciseId: 10, setNumber: 1)
        #expect(vm.training.loggedExercises[0].sets[0].isCompleted == false)
        #expect(vm.isRestTimerActive == false)

        // Toggle set 2 with restSeconds: 0 -> fallback 90s
        await vm.toggleSetCompleted(exerciseId: 10, setNumber: 2)
        #expect(vm.isRestTimerActive == true)
        #expect(vm.restTimerSecondsRemaining == 90)

        // Toggle non-existent set/exercise (no-op)
        await vm.toggleSetCompleted(exerciseId: 999, setNumber: 1)
        await vm.toggleSetCompleted(exerciseId: 10, setNumber: 999)

        // 5. updateSetValues
        await vm.updateSetValues(exerciseId: 10, setNumber: 1, weight: 145, reps: 6, rpe: 8.5, setType: .warmUp)
        let updatedSet = vm.training.loggedExercises[0].sets[0]
        #expect(updatedSet.weightKg == 145)
        #expect(updatedSet.repetitions == 6)
        #expect(updatedSet.rpe == 8.5)
        #expect(updatedSet.setType == .warmUp)

        // updateSetValues non-existent (no-op)
        await vm.updateSetValues(exerciseId: 999, setNumber: 1, weight: 100, reps: 5)
        await vm.updateSetValues(exerciseId: 10, setNumber: 999, weight: 100, reps: 5)

        // 6. addSet
        await vm.addSet(to: 10)
        #expect(vm.training.loggedExercises[0].sets.count == 3)
        #expect(vm.training.loggedExercises[0].sets.last?.setNumber == 3)
        #expect(vm.training.loggedExercises[0].sets.last?.weightKg == 150) // inherited from last set

        // addSet with empty sets exercise (triggers 20.0, 10, 90 fallbacks)
        let emptySetsEx = SessionExerciseItem(
            orderIndex: 1,
            exerciseId: 55,
            exerciseName: "Bicep Curl",
            sets: []
        )
        vm.training.loggedExercises.append(emptySetsEx)
        await vm.addSet(to: 55)
        #expect(vm.training.loggedExercises.last?.sets.first?.weightKg == 20.0)
        #expect(vm.training.loggedExercises.last?.sets.first?.repetitions == 10)
        #expect(vm.training.loggedExercises.last?.sets.first?.restSeconds == 90)

        // addSet non-existent (no-op)
        await vm.addSet(to: 999)

        // 7. deleteSet
        await vm.deleteSet(exerciseId: 10, setNumber: 2)
        #expect(vm.training.loggedExercises[0].sets.count == 2)
        #expect(vm.training.loggedExercises[0].sets.last?.setNumber == 2) // re-indexed

        // deleteSet non-existent (no-op)
        await vm.deleteSet(exerciseId: 999, setNumber: 1)

        // 8. pauseWorkout & resumeWorkout
        await vm.pauseWorkout()
        #expect(vm.isTimerRunning == false)
        #expect(vm.training.status == .paused)

        await vm.resumeWorkout()
        #expect(vm.isTimerRunning == true)
        #expect(vm.training.status == .inProgress)

        // 9. finishWorkout
        vm.selectedRpe = 8.0
        vm.completionNotes = "Great back engagement"
        await vm.finishWorkout()
        #expect(vm.isFinished == true)
        #expect(vm.isTimerRunning == false)
        #expect(vm.training.status == .completed)

        // 10. cancelWorkout
        let activeForCancel = Training(id: 21, name: "Short Workout", status: .inProgress)
        let sdCancel = SDTraining.fromDomain(activeForCancel)
        container.mainContext.insert(sdCancel)
        try container.mainContext.save()

        let cancelVM = ActiveWorkoutViewModel(training: activeForCancel, trainingRepository: trainingRepo)
        await cancelVM.cancelWorkout()
        #expect(cancelVM.isCancelled == true)
        #expect(cancelVM.training.status == .cancelled)

        // 11. Failing repository error handling paths
        let failingRepo = FailingLiveWorkoutTrainingRepo()
        let failingVM = ActiveWorkoutViewModel(training: training, trainingRepository: failingRepo)

        await failingVM.toggleSetCompleted(exerciseId: 10, setNumber: 1)
        #expect(failingVM.errorMessage?.contains("Failed to update set") == true)

        await failingVM.updateSetValues(exerciseId: 10, setNumber: 1, weight: 100, reps: 5)
        #expect(failingVM.errorMessage?.contains("Failed to update set") == true)

        await failingVM.addSet(to: 10)
        #expect(failingVM.errorMessage?.contains("Failed to add set") == true)

        await failingVM.deleteSet(exerciseId: 10, setNumber: 1)
        #expect(failingVM.errorMessage?.contains("Failed to delete set") == true)

        await failingVM.pauseWorkout()
        #expect(failingVM.errorMessage?.contains("Failed to pause") == true)

        await failingVM.resumeWorkout()
        #expect(failingVM.errorMessage?.contains("Failed to resume") == true)

        await failingVM.finishWorkout()
        #expect(failingVM.errorMessage?.contains("Failed to finish") == true)

        await failingVM.cancelWorkout()
        #expect(failingVM.errorMessage?.contains("Failed to cancel") == true)
    }

    // MARK: - WorkoutDashboardViewModel Full Coverage

    @Test("WorkoutDashboardViewModel: active, planned, completed filtering, search, start from template")
    func testWorkoutDashboardViewModel() async throws {
        let container = try createTestContainer()
        let sessionRepo = SwiftDataSessionRepository(modelContext: container.mainContext)
        let trainingRepo = SwiftDataTrainingRepository(modelContext: container.mainContext)

        let session = Session(id: 50, name: "Full Body Template")
        _ = try await sessionRepo.saveSession(session)

        let tPlanned = Training(id: 1, name: "Planned Monday", description: "Legs", status: .planned)
        let tActive = Training(id: 2, name: "Active Tuesday", status: .inProgress)
        let tCompleted = Training(id: 3, name: "Completed Wednesday", description: "Chest", status: .completed, notes: "Hit PR")
        let tCancelled = Training(id: 4, name: "Cancelled Thursday", status: .cancelled)

        container.mainContext.insert(SDTraining.fromDomain(tPlanned))
        container.mainContext.insert(SDTraining.fromDomain(tActive))
        container.mainContext.insert(SDTraining.fromDomain(tCompleted))
        container.mainContext.insert(SDTraining.fromDomain(tCancelled))
        try container.mainContext.save()

        let vm = WorkoutDashboardViewModel(trainingRepository: trainingRepo)
        await vm.loadTrainings()

        #expect(vm.trainings.count == 4)
        #expect(vm.activeWorkout?.id == 2)
        #expect(vm.plannedTrainings.count == 1)
        #expect(vm.completedTrainings.count == 2) // completed + cancelled

        // Active workout with paused status executes right-hand side of ||
        vm.trainings = [Training(id: 99, name: "Paused Workout", status: .paused)]
        #expect(vm.activeWorkout?.id == 99)
        vm.trainings = []
        #expect(vm.activeWorkout == nil)
        await vm.loadTrainings()

        // Search filtering: empty search
        vm.searchText = ""
        #expect(vm.filteredCompletedTrainings.count == 2)

        // Search by name
        vm.searchText = "Completed"
        #expect(vm.filteredCompletedTrainings.count == 1)
        #expect(vm.filteredCompletedTrainings.first?.id == 3)

        // Search by description
        vm.searchText = "Chest"
        #expect(vm.filteredCompletedTrainings.count == 1)

        // Search by notes
        vm.searchText = "Hit PR"
        #expect(vm.filteredCompletedTrainings.count == 1)

        // Search non-matching
        vm.searchText = "Non-existent"
        #expect(vm.filteredCompletedTrainings.isEmpty)

        // Delete training
        await vm.deleteTraining(id: 4)
        #expect(vm.completedTrainings.count == 1)

        // Start workout from session
        let started = await vm.startWorkoutFromSession(sessionId: 50)
        #expect(started != nil)
        #expect(started?.status == .inProgress)
        #expect(vm.activeWorkout?.id == started?.id)

        // Start workout from session error path
        let failStart = await vm.startWorkoutFromSession(sessionId: 9999)
        #expect(failStart == nil)
        #expect(vm.errorMessage != nil)

        // Failing repository on load
        let failingRepo = FailingLiveWorkoutTrainingRepo()
        let failingVM = WorkoutDashboardViewModel(trainingRepository: failingRepo)
        await failingVM.loadTrainings()
        #expect(failingVM.errorMessage != nil)
    }
}
