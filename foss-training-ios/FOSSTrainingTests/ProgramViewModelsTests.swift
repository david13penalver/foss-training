import Testing
import Foundation
@testable import FOSSTraining

@MainActor
final class MockTrainingProgramRepo: TrainingProgramRepository {
    var programs: [TrainingProgram] = []
    var shouldFailGetPrograms = false
    var shouldFailGetProgram = false
    var shouldFailSave = false
    var shouldFailDelete = false
    var shouldFailClone = false
    var shouldFailGenerateSchedule = false
    var shouldFailAdherence = false

    var adherenceToReturn: ProgramAdherence? = nil

    func getPrograms() async throws -> [TrainingProgram] {
        if shouldFailGetPrograms {
            throw NSError(domain: "MockRepo", code: 500, userInfo: [NSLocalizedDescriptionKey: "Failed to fetch programs"])
        }
        return programs
    }

    func getProgram(id: Int) async throws -> TrainingProgram? {
        if shouldFailGetProgram {
            throw NSError(domain: "MockRepo", code: 500, userInfo: [NSLocalizedDescriptionKey: "Failed to fetch program"])
        }
        return programs.first { $0.id == id }
    }

    func saveProgram(_ program: TrainingProgram) async throws -> TrainingProgram {
        if shouldFailSave {
            throw NSError(domain: "MockRepo", code: 500, userInfo: [NSLocalizedDescriptionKey: "Failed to save program"])
        }
        if let idx = programs.firstIndex(where: { $0.id == program.id }) {
            programs[idx] = program
            return program
        } else {
            let newId = (program.id > 0) ? program.id : (programs.map(\.id).max() ?? 0) + 1
            let saved = TrainingProgram(
                id: newId,
                name: program.name,
                description: program.description,
                durationWeeks: program.durationWeeks,
                periodizationType: program.periodizationType,
                level: program.level,
                workouts: program.workouts,
                isActive: program.isActive
            )
            programs.append(saved)
            return saved
        }
    }

    func deleteProgram(id: Int) async throws {
        if shouldFailDelete {
            throw NSError(domain: "MockRepo", code: 500, userInfo: [NSLocalizedDescriptionKey: "Failed to delete program"])
        }
        programs.removeAll { $0.id == id }
    }

    func programExists(id: Int) async throws -> Bool {
        programs.contains { $0.id == id }
    }

    func cloneProgram(id: Int, newName: String?) async throws -> TrainingProgram {
        if shouldFailClone {
            throw NSError(domain: "MockRepo", code: 500, userInfo: [NSLocalizedDescriptionKey: "Failed to clone program"])
        }
        guard let orig = programs.first(where: { $0.id == id }) else {
            throw NSError(domain: "MockRepo", code: 404, userInfo: [NSLocalizedDescriptionKey: "Program not found"])
        }
        let cloned = TrainingProgram(
            id: (programs.map(\.id).max() ?? 0) + 1,
            name: newName ?? "\(orig.name) (Copy)",
            workouts: orig.workouts
        )
        programs.append(cloned)
        return cloned
    }

    func generateSchedule(programId: Int, startDate: Date?) async throws -> [Training] {
        if shouldFailGenerateSchedule {
            throw NSError(domain: "MockRepo", code: 500, userInfo: [NSLocalizedDescriptionKey: "Failed to generate schedule"])
        }
        guard let program = programs.first(where: { $0.id == programId }) else {
            throw NSError(domain: "MockRepo", code: 404, userInfo: [NSLocalizedDescriptionKey: "Program not found"])
        }
        return program.generateSchedule(startDate: startDate)
    }

    func getProgramAdherence(id: Int) async throws -> ProgramAdherence {
        if shouldFailAdherence {
            throw NSError(domain: "MockRepo", code: 500, userInfo: [NSLocalizedDescriptionKey: "Failed to get adherence"])
        }
        if let adh = adherenceToReturn {
            return adh
        }
        guard let program = programs.first(where: { $0.id == id }) else {
            throw NSError(domain: "MockRepo", code: 404, userInfo: [NSLocalizedDescriptionKey: "Program not found"])
        }
        return ProgramAdherenceCalculator.calculate(program: program, trainings: [])
    }
}

@MainActor
final class MockSessionRepoForVM: SessionRepository {
    var sessions: [Session] = []
    var shouldFailGetSessions = false

    func getSessions() async throws -> [Session] {
        if shouldFailGetSessions {
            throw NSError(domain: "MockSessionRepo", code: 500, userInfo: [NSLocalizedDescriptionKey: "Failed to get sessions"])
        }
        return sessions
    }

    func getSession(id: Int) async throws -> Session? {
        sessions.first { $0.id == id }
    }

    func saveSession(_ session: Session) async throws -> Session {
        sessions.append(session)
        return session
    }

    func deleteSession(id: Int) async {}

    func sessionExists(id: Int) async throws -> Bool {
        sessions.contains { $0.id == id }
    }

    func cloneSession(id: Int) async throws -> Session {
        let orig = sessions.first { $0.id == id }!
        let clone = Session(id: orig.id + 10, name: "\(orig.name) (Copy)")
        sessions.append(clone)
        return clone
    }
}

@Suite("Program ViewModels Tests")
@MainActor
struct ProgramViewModelsTests {

    @Test("ProgramsListViewModel: load, filter by level and search query, adherence cache")
    func testProgramsListViewModelFilteringAndLoading() async {
        let repo = MockTrainingProgramRepo()
        let p1 = TrainingProgram(id: 1, name: "Hypertrophy Mesocycle", description: "Bodybuilding focus", level: .intermediate)
        let p2 = TrainingProgram(id: 2, name: "Powerlifting Peak", description: "1RM peaking", level: .advanced)
        let p3 = TrainingProgram(id: 3, name: "Foundation", description: nil, level: .beginner)
        repo.programs = [p1, p2, p3]

        let vm = ProgramsListViewModel(programRepository: repo)
        #expect(vm.programs.isEmpty)

        // Load programs
        await vm.loadPrograms()
        #expect(vm.programs.count == 3)
        #expect(vm.adherences[1] != nil)
        #expect(vm.errorMessage == nil)

        // Filter by level
        vm.selectedLevel = .advanced
        #expect(vm.filteredPrograms.count == 1)
        #expect(vm.filteredPrograms[0].name == "Powerlifting Peak")

        vm.selectedLevel = nil
        #expect(vm.filteredPrograms.count == 3)

        // Filter by search query (name match)
        vm.searchText = "Hypertrophy"
        #expect(vm.filteredPrograms.count == 1)
        #expect(vm.filteredPrograms[0].id == 1)

        // Filter by search query (description match)
        vm.searchText = "peaking"
        #expect(vm.filteredPrograms.count == 1)
        #expect(vm.filteredPrograms[0].id == 2)

        // Filter with empty query
        vm.searchText = "   "
        #expect(vm.filteredPrograms.count == 3)

        // Filter with no matches
        vm.searchText = "NonExistent"
        #expect(vm.filteredPrograms.isEmpty)

        // Load failure
        repo.shouldFailGetPrograms = true
        await vm.loadPrograms()
        #expect(vm.errorMessage == "Failed to fetch programs")
    }

    @Test("ProgramsListViewModel: delete, clone, generateSchedule actions and error paths")
    func testProgramsListViewModelActions() async {
        let repo = MockTrainingProgramRepo()
        let p1 = TrainingProgram(
            id: 1,
            name: "Linear Block",
            workouts: [ProgramWorkout(dayOfWeek: 1, focus: "Upper", session: Session(id: 10, name: "Upper"))]
        )
        repo.programs = [p1]

        let vm = ProgramsListViewModel(programRepository: repo)
        await vm.loadPrograms()

        // Clone success
        await vm.cloneProgram(id: 1, newName: "Cloned Linear")
        #expect(vm.programs.count == 2)
        #expect(vm.programs.contains { $0.name == "Cloned Linear" })

        // Clone failure
        repo.shouldFailClone = true
        await vm.cloneProgram(id: 1, newName: "Will Fail")
        #expect(vm.errorMessage?.contains("Failed to clone program") == true)
        repo.shouldFailClone = false

        // Generate schedule success
        await vm.generateSchedule(programId: 1, startDate: Date())
        #expect(vm.generationSuccessMessage?.contains("Successfully generated") == true)
        #expect(vm.isGeneratingSchedule == false)

        // Generate schedule failure
        repo.shouldFailGenerateSchedule = true
        await vm.generateSchedule(programId: 1, startDate: Date())
        #expect(vm.errorMessage?.contains("Failed to generate schedule") == true)
        repo.shouldFailGenerateSchedule = false

        // Delete success
        await vm.deleteProgram(id: 1)
        #expect(!vm.programs.contains { $0.id == 1 })

        // Delete failure
        repo.shouldFailDelete = true
        await vm.deleteProgram(id: 2)
        #expect(vm.errorMessage?.contains("Failed to delete program") == true)
    }

    @Test("ProgramDetailViewModel: schedule breakdown, loadDetails, generateSchedule")
    func testProgramDetailViewModel() async {
        let repo = MockTrainingProgramRepo()
        let session = Session(id: 1, name: "Leg Day")
        let program = TrainingProgram(
            id: 10,
            name: "Leg Specialization",
            durationWeeks: 4,
            workouts: [
                ProgramWorkout(dayOfWeek: 2, focus: "Quads", session: session),
                ProgramWorkout(dayOfWeek: 5, focus: "Hamstrings", session: session)
            ]
        )
        repo.programs = [program]

        let vm = ProgramDetailViewModel(programRepository: repo, program: program)

        // Workout for day & days schedule
        #expect(vm.workoutForDay(2)?.focus == "Quads")
        #expect(vm.workoutForDay(3) == nil)

        let schedule = vm.daysOfWeekSchedule
        #expect(schedule.count == 7)
        #expect(schedule[0].id == 1)
        #expect(schedule[0].dayName == "Mon")
        #expect(schedule[0].isRestDay == true)
        #expect(schedule[1].dayName == "Tue")
        #expect(schedule[1].isRestDay == false)
        #expect(schedule[1].workout?.focus == "Quads")
        #expect(schedule[6].dayName == "Sun")

        // Load details success
        await vm.loadDetails()
        #expect(vm.program.name == "Leg Specialization")
        #expect(vm.adherence != nil)
        #expect(vm.errorMessage == nil)

        // Load details when repo returns nil (keeps current)
        repo.programs = []
        await vm.loadDetails()
        #expect(vm.program.name == "Leg Specialization")

        // Load details error
        repo.shouldFailGetProgram = true
        await vm.loadDetails()
        #expect(vm.errorMessage == "Failed to fetch program")
        repo.shouldFailGetProgram = false

        // Generate schedule success
        repo.programs = [program]
        await vm.generateSchedule(startDate: Date())
        #expect(vm.scheduleSuccessMessage?.contains("Successfully generated") == true)
        #expect(!vm.isGenerating)

        // Generate schedule failure
        repo.shouldFailGenerateSchedule = true
        await vm.generateSchedule(startDate: nil)
        #expect(vm.errorMessage?.contains("Failed to generate schedule") == true)
    }

    @Test("ProgramEditorViewModel: validation rules, add/remove/update workout, loadSessions, save")
    func testProgramEditorViewModel() async {
        let repo = MockTrainingProgramRepo()
        let sessionRepo = MockSessionRepoForVM()
        let s1 = Session(id: 101, name: "Full Body A")
        let s2 = Session(id: 102, name: "Full Body B")
        sessionRepo.sessions = [s1, s2]

        // 1. New Program initialization
        let vm = ProgramEditorViewModel(programRepository: repo, sessionRepository: sessionRepo)
        #expect(vm.isNewProgram == true)
        #expect(vm.existingProgramId == nil)
        #expect(vm.isValid == false) // Name is empty, no workouts

        // Load sessions
        await vm.loadSessions()
        #expect(vm.availableSessions.count == 2)

        // Load sessions failure
        sessionRepo.shouldFailGetSessions = true
        await vm.loadSessions()
        #expect(vm.errorMessage?.contains("Failed to load sessions") == true)
        sessionRepo.shouldFailGetSessions = false

        // Validation: name length checks
        vm.name = "A" // too short
        #expect(!vm.isValid)
        vm.name = String(repeating: "X", count: 101) // too long
        #expect(!vm.isValid)
        vm.name = "Valid Program Name"

        // Validation: duration weeks checks
        vm.durationWeeks = 0
        #expect(!vm.isValid)
        vm.durationWeeks = 53
        #expect(!vm.isValid)
        vm.durationWeeks = 6

        // Add workouts
        #expect(!vm.isValid) // still empty workouts
        vm.addWorkout(dayOfWeek: 3, session: s1, focus: "Full Body")
        vm.addWorkout(dayOfWeek: 1, session: s2, focus: "Upper")
        #expect(vm.workouts.count == 2)
        #expect(vm.workouts[0].dayOfWeek == 1)
        #expect(vm.workouts[1].dayOfWeek == 3)
        #expect(vm.isValid == true)

        let workoutId = vm.workouts[0].id
        vm.updateWorkout(id: workoutId, dayOfWeek: 5, session: s2, focus: "Focus Updated")
        #expect(vm.workouts[0].dayOfWeek == 3)
        #expect(vm.workouts[1].dayOfWeek == 5)
        #expect(vm.workouts[1].focus == "Focus Updated")
        #expect(vm.workouts[1].session.name == "Full Body B")

        // Update workout with focus = nil and re-order back
        vm.updateWorkout(id: workoutId, dayOfWeek: 1, session: s1, focus: nil)
        #expect(vm.workouts[0].dayOfWeek == 1)
        #expect(vm.workouts[0].focus == nil)

        // Update non-existent workout ID (no-op)
        vm.updateWorkout(id: UUID(), dayOfWeek: 5, session: s1, focus: nil)
        #expect(vm.workouts.count == 2)

        // Remove second workout to return to 1 workout for remainder of test
        vm.removeWorkout(id: vm.workouts[1].id)
        #expect(vm.workouts.count == 1)

        // Save invalid program
        vm.name = " "
        let saveInvalid = await vm.saveProgram()
        #expect(!saveInvalid)
        #expect(vm.errorMessage != nil)

        // Save valid new program
        vm.name = "Full Body Novice"
        vm.descriptionText = "A simple routine"
        let saveSuccess = await vm.saveProgram()
        #expect(saveSuccess == true)
        #expect(repo.programs.count == 1)
        #expect(repo.programs[0].name == "Full Body Novice")
        #expect(repo.programs[0].description == "A simple routine")

        // Save failure
        repo.shouldFailSave = true
        let saveFailure = await vm.saveProgram()
        #expect(!saveFailure)
        #expect(vm.errorMessage?.contains("Failed to save program") == true)
        repo.shouldFailSave = false

        // 2. Existing Program initialization
        let existing = repo.programs[0]
        let editVm = ProgramEditorViewModel(
            programRepository: repo,
            sessionRepository: sessionRepo,
            programToEdit: existing
        )
        #expect(editVm.isNewProgram == false)
        #expect(editVm.existingProgramId == existing.id)
        #expect(editVm.name == "Full Body Novice")
        #expect(editVm.isValid == true)

        // Initialize with nil description
        let progNilDesc = TrainingProgram(id: 99, name: "No Description", description: nil)
        let editNilDescVm = ProgramEditorViewModel(programRepository: repo, sessionRepository: sessionRepo, programToEdit: progNilDesc)
        #expect(editNilDescVm.descriptionText == "")

        // Remove workout
        editVm.removeWorkout(id: editVm.workouts[0].id)
        #expect(editVm.workouts.isEmpty)
        #expect(!editVm.isValid)

        // Add workout back and save with empty description (nil in domain)
        editVm.addWorkout(dayOfWeek: 2, session: s1)
        editVm.descriptionText = ""
        let editSaveSuccess = await editVm.saveProgram()
        #expect(editSaveSuccess == true)
        let updated = repo.programs.first { $0.id == existing.id }
        #expect(updated?.description == nil)
    }
}
