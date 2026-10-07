import Testing
import Foundation
import SwiftData
@testable import FOSSTraining

private final class SessionMockURLProtocol: URLProtocol, @unchecked Sendable {
    nonisolated(unsafe) static var requestHandler: ((URLRequest) throws -> (URLResponse, Data?))?

    override class func canInit(with request: URLRequest) -> Bool {
        request.url?.path.contains("/api/sessions") == true
    }
    override class func canonicalRequest(for request: URLRequest) -> URLRequest { request }

    override func startLoading() {
        guard let handler = SessionMockURLProtocol.requestHandler else {
            client?.urlProtocol(self, didFailWithError: NSError(domain: "SessionMockURLProtocol", code: -1))
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

private final class FailingSessionRepo: SessionRepository, @unchecked Sendable {
    func getSessions() async throws -> [Session] {
        throw NSError(domain: "Test", code: 500, userInfo: [NSLocalizedDescriptionKey: "Failed to load"])
    }
    func getSession(id: Int) async throws -> Session? { nil }
    func saveSession(_ session: Session) async throws -> Session {
        throw NSError(domain: "Test", code: 500, userInfo: [NSLocalizedDescriptionKey: "Failed to save"])
    }
    func deleteSession(id: Int) async throws {
        throw NSError(domain: "Test", code: 500, userInfo: [NSLocalizedDescriptionKey: "Failed to delete"])
    }
    func cloneSession(id: Int) async throws -> Session {
        throw NSError(domain: "Test", code: 500, userInfo: [NSLocalizedDescriptionKey: "Failed to clone"])
    }
}

private final class FailingExerciseCatalogRepo: ExerciseRepository, @unchecked Sendable {
    func getExercises(category: ExerciseCategory?, search: String?) async throws -> [Exercise] {
        throw NSError(domain: "Test", code: 500, userInfo: [NSLocalizedDescriptionKey: "Failed to load exercises"])
    }
    func getExercise(id: Int) async throws -> Exercise? { nil }
    func saveExercise(_ exercise: Exercise) async throws -> Exercise { exercise }
    func deleteExercise(id: Int) async throws {}
}

private final class DefaultSessionRepo: SessionRepository, @unchecked Sendable {
    func getSessions() async throws -> [Session] { [] }
    func getSession(id: Int) async throws -> Session? {
        if id == 42 {
            return Session(id: 42, name: "Found")
        }
        return nil
    }
    func saveSession(_ session: Session) async throws -> Session { session }
    func deleteSession(id: Int) async throws {}
    func cloneSession(id: Int) async throws -> Session { Session(id: id, name: "Cloned") }
}

@Suite("SPEC-02: Session Templates & Builder Coverage Tests", .serialized)
@MainActor
struct SessionTemplatesCoverageTests {

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
        configuration.protocolClasses = [SessionMockURLProtocol.self]
        let session = URLSession(configuration: configuration)
        return NetworkClient(baseURLString: "http://mock.local", session: session)
    }

    // MARK: - SwiftDataSessionRepository Tests

    @Test("SwiftDataSessionRepository: CRUD, sessionExists, and cloning")
    func testSwiftDataSessionRepositoryCRUD() async throws {
        let container = try createTestContainer()
        let repo = SwiftDataSessionRepository(modelContext: container.mainContext)

        // 1. Initial empty state
        let empty = try await repo.getSessions()
        #expect(empty.isEmpty)

        // 2. Save new session
        let exercise = SessionExerciseItem(
            orderIndex: 0,
            exerciseId: 10,
            exerciseName: "Squat",
            part: .main,
            restSeconds: 120,
            sets: [
                ResistanceSet(setNumber: 1, setType: .normal, weightKg: 100, repetitions: 5, restSeconds: 120)
            ]
        )
        let session = Session(
            id: 1,
            name: "Leg Day Alpha",
            description: "Heavy squats",
            notes: "Wear lifting belt",
            estimatedDurationMinutes: 75,
            exercises: [exercise]
        )
        let saved = try await repo.saveSession(session)
        #expect(saved.id == 1)
        #expect(saved.name == "Leg Day Alpha")
        #expect(saved.exercises.count == 1)

        // 3. sessionExists
        let exists = try await repo.sessionExists(id: 1)
        #expect(exists == true)
        let notExists = try await repo.sessionExists(id: 999)
        #expect(notExists == false)

        // 4. Update existing session
        var updated = saved
        updated.name = "Leg Day Alpha Modified"
        updated.description = "Updated description"
        let updatedResult = try await repo.saveSession(updated)
        #expect(updatedResult.name == "Leg Day Alpha Modified")
        #expect(updatedResult.description == "Updated description")

        // 5. getSession(id:)
        let fetched = try await repo.getSession(id: 1)
        #expect(fetched?.name == "Leg Day Alpha Modified")
        let nonExistent = try await repo.getSession(id: 999)
        #expect(nonExistent == nil)

        // 6. cloneSession(id:)
        let cloned = try await repo.cloneSession(id: 1)
        #expect(cloned.id == 2)
        #expect(cloned.name == "Leg Day Alpha Modified (Copy)")
        #expect(cloned.exercises.count == 1)

        // 7. cloneSession failure when original not found
        await #expect(throws: NSError.self) {
            _ = try await repo.cloneSession(id: 9999)
        }

        // 8. deleteSession
        try await repo.deleteSession(id: 1)
        let afterDelete = try await repo.getSession(id: 1)
        #expect(afterDelete == nil)

        // Delete non-existent does not crash
        try await repo.deleteSession(id: 9999)

        // Default protocol extension sessionExists
        let defaultRepo = DefaultSessionRepo()
        let foundViaDefault = try await defaultRepo.sessionExists(id: 42)
        #expect(foundViaDefault == true)
        let notFoundViaDefault = try await defaultRepo.sessionExists(id: 100)
        #expect(notFoundViaDefault == false)
    }

    // MARK: - RemoteSessionRepository Tests

    @Test("RemoteSessionRepository: API endpoints, 404 handling, DTO mapping")
    func testRemoteSessionRepository() async throws {
        let client = createMockClient()
        let repo = RemoteSessionRepository(client: client)

        let sampleDto = SessionResponseDto(
            id: 10,
            name: "Upper Body Hypertrophy",
            description: "Chest and back",
            notes: "Focus on mind-muscle connection",
            estimatedDurationMinutes: 60,
            sessionExercises: [
                SessionExerciseDto(item: SessionExerciseItem(
                    orderIndex: 0,
                    exerciseId: 5,
                    exerciseName: "Bench Press",
                    part: .main,
                    restSeconds: 90,
                    sets: [ResistanceSet(setNumber: 1, setType: .normal, weightKg: 80, repetitions: 8)]
                ))
            ]
        )

        // 1. getSessions
        SessionMockURLProtocol.requestHandler = { request in
            #expect(request.httpMethod == "GET")
            let data = try! JSONEncoder().encode([sampleDto])
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }
        let sessions = try await repo.getSessions()
        #expect(sessions.count == 1)
        #expect(sessions.first?.name == "Upper Body Hypertrophy")

        // 2. getSession found
        SessionMockURLProtocol.requestHandler = { request in
            let data = try! JSONEncoder().encode(sampleDto)
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }
        let found = try await repo.getSession(id: 10)
        #expect(found?.id == 10)

        // 3. getSession 404 returns nil
        SessionMockURLProtocol.requestHandler = { request in
            let response = HTTPURLResponse(url: request.url!, statusCode: 404, httpVersion: nil, headerFields: nil)!
            return (response, "Not found".data(using: .utf8))
        }
        let missing = try await repo.getSession(id: 999)
        #expect(missing == nil)

        // 4. saveSession (create: id == 0)
        SessionMockURLProtocol.requestHandler = { request in
            #expect(request.httpMethod == "POST")
            let data = try! JSONEncoder().encode(sampleDto)
            let response = HTTPURLResponse(url: request.url!, statusCode: 201, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }
        let newSession = Session(id: 0, name: "New Workout")
        let created = try await repo.saveSession(newSession)
        #expect(created.id == 10)

        // 5. saveSession (update: id > 0)
        SessionMockURLProtocol.requestHandler = { request in
            #expect(request.httpMethod == "PUT")
            let data = try! JSONEncoder().encode(sampleDto)
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }
        let updated = try await repo.saveSession(sessions.first!)
        #expect(updated.id == 10)

        // 6. deleteSession
        SessionMockURLProtocol.requestHandler = { request in
            #expect(request.httpMethod == "DELETE")
            let response = HTTPURLResponse(url: request.url!, statusCode: 204, httpVersion: nil, headerFields: nil)!
            return (response, nil)
        }
        try await repo.deleteSession(id: 10)

        // 7. cloneSession
        SessionMockURLProtocol.requestHandler = { request in
            #expect(request.httpMethod == "POST")
            let clonedDto = SessionResponseDto(
                id: 11,
                name: "Upper Body Hypertrophy (Copy)",
                description: nil,
                notes: nil,
                estimatedDurationMinutes: nil,
                sessionExercises: nil
            )
            let data = try! JSONEncoder().encode(clonedDto)
            let response = HTTPURLResponse(url: request.url!, statusCode: 201, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }
        let cloned = try await repo.cloneSession(id: 10)
        #expect(cloned.id == 11)
        #expect(cloned.name.contains("(Copy)"))

        // 8. sessionExists
        SessionMockURLProtocol.requestHandler = { request in
            #expect(request.httpMethod == "GET")
            let data = try! JSONEncoder().encode(true)
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, data)
        }
        let exists = try await repo.sessionExists(id: 10)
        #expect(exists == true)

        // 9. DTO constructors
        let cloneReq = CloneSessionRequestDto(name: "Custom Clone")
        #expect(cloneReq.name == "Custom Clone")
        let defaultCloneReq = CloneSessionRequestDto()
        #expect(defaultCloneReq.name == nil)

        // 10. DTO decoding with null/unknown fallback fields
        let fallbackJson = """
        {
            "id": 99,
            "name": "Fallback Session",
            "estimatedDurationMinutes": null,
            "sessionExercises": [
                {
                    "exerciseId": 42,
                    "exerciseName": "Custom Move",
                    "orderIndex": 0,
                    "part": "NON_EXISTENT_PART",
                    "restSeconds": 60,
                    "sets": null
                }
            ]
        }
        """.data(using: .utf8)!
        let fallbackDto = try JSONDecoder().decode(SessionResponseDto.self, from: fallbackJson)
        let fallbackDomain = fallbackDto.toDomain()
        #expect(fallbackDomain.estimatedDurationMinutes == 60)
        #expect(fallbackDomain.exercises.count == 1)
        #expect(fallbackDomain.exercises.first?.part == .main)
        #expect(fallbackDomain.exercises.first?.sets.isEmpty == true)
    }

    // MARK: - SessionListViewModel Tests

    @Test("SessionListViewModel: load, search, delete, clone, start workout")
    func testSessionListViewModel() async throws {
        let container = try createTestContainer()
        let sessionRepo = SwiftDataSessionRepository(modelContext: container.mainContext)
        let trainingRepo = SwiftDataTrainingRepository(modelContext: container.mainContext)

        let s1 = Session(id: 1, name: "Push Strength", description: "Chest focus")
        let s2 = Session(id: 2, name: "Pull Hypertrophy", description: "Back and biceps")
        _ = try await sessionRepo.saveSession(s1)
        _ = try await sessionRepo.saveSession(s2)

        let vm = SessionListViewModel(sessionRepository: sessionRepo, trainingRepository: trainingRepo)
        await vm.loadSessions()
        #expect(vm.sessions.count == 2)
        #expect(vm.filteredSessions.count == 2)

        // Search filtering by name
        vm.searchText = "Push"
        #expect(vm.filteredSessions.count == 1)
        #expect(vm.filteredSessions.first?.name == "Push Strength")

        // Search filtering by description
        vm.searchText = "biceps"
        #expect(vm.filteredSessions.count == 1)
        #expect(vm.filteredSessions.first?.name == "Pull Hypertrophy")

        // Search non-matching
        vm.searchText = "Non-existent"
        #expect(vm.filteredSessions.isEmpty)

        // Reset search
        vm.searchText = "   "
        #expect(vm.filteredSessions.count == 2)

        // Clone
        await vm.cloneSession(id: 1)
        #expect(vm.sessions.count == 3)

        // Delete
        await vm.deleteSession(id: 1)
        #expect(vm.sessions.count == 2)

        // Start workout
        await vm.startWorkout(from: s2)
        #expect(vm.launchedTraining != nil)

        // Throwing repo paths
        let throwingRepo = FailingSessionRepo()
        let failingVM = SessionListViewModel(sessionRepository: throwingRepo, trainingRepository: trainingRepo)

        await failingVM.loadSessions()
        #expect(failingVM.errorMessage != nil)

        await failingVM.deleteSession(id: 1)
        #expect(failingVM.errorMessage?.contains("Failed to delete") == true)

        await failingVM.cloneSession(id: 1)
        #expect(failingVM.errorMessage?.contains("Failed to clone") == true)
    }

    // MARK: - SessionEditorViewModel Tests

    @Test("SessionEditorViewModel: creation, validation, exercises manipulation, sets editing, saving")
    func testSessionEditorViewModel() async throws {
        let container = try createTestContainer()
        let sessionRepo = SwiftDataSessionRepository(modelContext: container.mainContext)
        let exerciseRepo = SwiftDataExerciseRepository(modelContext: container.mainContext)

        let sampleExercise = Exercise(
            id: 100,
            name: "Barbell Squat",
            primaryCategory: .resistance,
            primaryMuscleGroup: "Quads",
            movementPattern: .squat
        )
        let warmUpExercise = Exercise(id: 101, name: "Leg Swings", primaryCategory: .mobility)
        let coolDownExercise = Exercise(id: 102, name: "Quad Stretch", primaryCategory: .mobility)
        _ = try await exerciseRepo.saveExercise(sampleExercise)

        // 1. Initial State for new session
        let newVM = SessionEditorViewModel(sessionRepository: sessionRepo, exerciseRepository: exerciseRepo)
        #expect(newVM.isNewSession == true)
        #expect(newVM.existingSessionId == nil)
        #expect(newVM.name == "")
        #expect(newVM.isValid == false)

        // 2. Load catalog exercises
        await newVM.loadCatalogExercises()
        #expect(!newVM.availableExercises.isEmpty)

        // 3. Validation branches
        newVM.name = "A" // too short
        #expect(newVM.isValid == false)

        newVM.name = String(repeating: "Z", count: 101) // too long
        #expect(newVM.isValid == false)

        newVM.name = "Leg Day Heavy"
        newVM.estimatedDurationMinutes = -10 // invalid duration
        #expect(newVM.isValid == false)

        newVM.estimatedDurationMinutes = 60
        #expect(newVM.isValid == false) // no exercises yet

        // 4. Add exercise to Warm-Up, Main, and Cool-Down
        newVM.addExercise(exercise: warmUpExercise, to: .warmUp)
        newVM.addExercise(exercise: sampleExercise, to: .main)
        newVM.addExercise(exercise: coolDownExercise, to: .coolDown)

        // Add second warm-up and second cool-down exercises to exercise .sorted closures
        let warmUp2 = Exercise(id: 104, name: "Torso Twists", primaryCategory: .mobility)
        let coolDown2 = Exercise(id: 105, name: "Calf Stretch", primaryCategory: .mobility)
        newVM.addExercise(exercise: warmUp2, to: .warmUp)
        newVM.addExercise(exercise: coolDown2, to: .coolDown)

        #expect(newVM.warmUpExercises.count == 2)
        #expect(newVM.mainExercises.count == 1)
        #expect(newVM.coolDownExercises.count == 2)
        #expect(newVM.isValid == true)

        // Test empty sets invalidation
        let warmUp2Item = newVM.warmUpExercises.last!
        newVM.removeSet(from: warmUp2Item.id, setNumber: 1)
        #expect(newVM.isValid == false) // triggered guard exercises.allSatisfy({ !$0.sets.isEmpty })
        newVM.addSet(to: warmUp2Item.id)
        #expect(newVM.isValid == true)

        let mainItem = newVM.mainExercises.first!
        #expect(mainItem.sets.count == 1)

        // 5. Add set
        newVM.addSet(to: mainItem.id)
        let itemWithTwoSets = newVM.mainExercises.first!
        #expect(itemWithTwoSets.sets.count == 2)
        #expect(itemWithTwoSets.sets.last?.setNumber == 2)

        // Add set to non-existent item (graceful no-op)
        newVM.addSet(to: "non-existent")

        // 6. Update set values
        newVM.updateSet(
            exerciseItemId: mainItem.id,
            setNumber: 1,
            weightKg: 120,
            repetitions: 6,
            setType: .warmUp
        )
        let updatedSet = newVM.mainExercises.first!.sets.first!
        #expect(updatedSet.weightKg == 120)
        #expect(updatedSet.repetitions == 6)
        #expect(updatedSet.setType == .warmUp)

        // Update non-existent set or item (graceful no-op)
        newVM.updateSet(exerciseItemId: "fake", setNumber: 1, weightKg: 100, repetitions: 5)
        newVM.updateSet(exerciseItemId: mainItem.id, setNumber: 999, weightKg: 100, repetitions: 5)

        // 7. Update rest seconds
        newVM.updateRestSeconds(exerciseItemId: mainItem.id, restSeconds: 180)
        #expect(newVM.mainExercises.first?.restSeconds == 180)
        newVM.updateRestSeconds(exerciseItemId: "fake", restSeconds: 180)

        // 8. Remove set
        newVM.removeSet(from: mainItem.id, setNumber: 1)
        #expect(newVM.mainExercises.first?.sets.count == 1)
        #expect(newVM.mainExercises.first?.sets.first?.setNumber == 1) // re-indexed
        newVM.removeSet(from: "fake", setNumber: 1)

        // 9. Move exercises in part
        let secondEx = Exercise(id: 103, name: "Leg Press", primaryCategory: .resistance, movementPattern: .squat)
        newVM.addExercise(exercise: secondEx, to: .main)
        #expect(newVM.mainExercises.count == 2)

        newVM.moveExercises(from: IndexSet(integer: 0), to: 2, in: .main)
        #expect(newVM.mainExercises.first?.exerciseName == "Leg Press")

        // 10. Remove exercise
        let itemToRemove = newVM.mainExercises.first(where: { $0.exerciseId == 100 })!
        newVM.removeExercise(id: itemToRemove.id)
        #expect(newVM.mainExercises.count == 1)
        newVM.removeExercise(id: "non-existent")

        // 11. Save valid session with empty description & notes (tests nil ternary branch)
        newVM.descriptionText = ""
        newVM.notes = ""
        let savedEmpty = await newVM.save()
        #expect(savedEmpty == true)

        // Save with non-empty description & notes
        newVM.descriptionText = "Squats and leg press"
        newVM.notes = "Warm up knees properly"
        let saved = await newVM.save()
        #expect(saved == true)
        #expect(newVM.isSaving == false)

        // 12. Save invalid session (should return false)
        newVM.name = ""
        let saveInvalid = await newVM.save()
        #expect(saveInvalid == false)
        #expect(newVM.errorMessage != nil)

        // 13. Edit existing session initialization
        let existingSession = Session(
            id: 88,
            name: "Full Body A",
            description: "Full body workout",
            notes: "Quick session",
            estimatedDurationMinutes: 45,
            exercises: [newVM.exercises.first!]
        )
        let editVM = SessionEditorViewModel(
            sessionRepository: sessionRepo,
            exerciseRepository: exerciseRepo,
            sessionToEdit: existingSession
        )
        #expect(editVM.isNewSession == false)
        #expect(editVM.existingSessionId == 88)
        #expect(editVM.name == "Full Body A")
        #expect(editVM.descriptionText == "Full body workout")
        #expect(editVM.notes == "Quick session")
        #expect(editVM.estimatedDurationMinutes == 45)
        #expect(editVM.exercises.count == 1)

        let editSaved = await editVM.save()
        #expect(editSaved == true)

        // 13b. Edit existing session with nil optional fields (tests fallback coalescing)
        let nilFieldsSession = Session(
            id: 89,
            name: "Bare Minimum",
            description: nil,
            notes: nil,
            estimatedDurationMinutes: nil,
            exercises: [newVM.exercises.first!]
        )
        let nilFieldsVM = SessionEditorViewModel(
            sessionRepository: sessionRepo,
            exerciseRepository: exerciseRepo,
            sessionToEdit: nilFieldsSession
        )
        #expect(nilFieldsVM.descriptionText == "")
        #expect(nilFieldsVM.notes == "")
        #expect(nilFieldsVM.estimatedDurationMinutes == 60)

        // 14. Error handling in save & catalog loading
        let failingRepo = FailingSessionRepo()
        let failingCatalogRepo = FailingExerciseCatalogRepo()
        let failingVM = SessionEditorViewModel(
            sessionRepository: failingRepo,
            exerciseRepository: failingCatalogRepo,
            sessionToEdit: existingSession
        )

        await failingVM.loadCatalogExercises()
        #expect(failingVM.errorMessage?.contains("Failed to load") == true)

        let failSave = await failingVM.save()
        #expect(failSave == false)
        #expect(failingVM.errorMessage?.contains("Failed to save") == true)
    }
}
