import SwiftUI

@Observable
@MainActor
public final class SessionEditorViewModel {
    private let sessionRepository: SessionRepository
    private let exerciseRepository: ExerciseRepository

    public let existingSessionId: Int?
    public var name: String = ""
    public var descriptionText: String = ""
    public var notes: String = ""
    public var estimatedDurationMinutes: Int = 60
    public var exercises: [SessionExerciseItem] = []

    public var availableExercises: [Exercise] = []
    public var selectedPartForPicker: SessionPartEnum = .main
    public var isPickerPresented: Bool = false
    public var isSaving: Bool = false
    public var errorMessage: String?

    public init(
        sessionRepository: SessionRepository,
        exerciseRepository: ExerciseRepository,
        sessionToEdit: Session? = nil
    ) {
        self.sessionRepository = sessionRepository
        self.exerciseRepository = exerciseRepository

        if let session = sessionToEdit {
            self.existingSessionId = session.id
            self.name = session.name
            self.descriptionText = session.description ?? ""
            self.notes = session.notes ?? ""
            self.estimatedDurationMinutes = session.estimatedDurationMinutes ?? 60
            self.exercises = session.exercises
        } else {
            self.existingSessionId = nil
        }
    }

    public var isNewSession: Bool {
        existingSessionId == nil
    }

    public var isValid: Bool {
        let trimmed = name.trimmingCharacters(in: .whitespacesAndNewlines)
        guard trimmed.count >= 2, trimmed.count <= 100 else { return false }
        guard estimatedDurationMinutes > 0 else { return false }
        guard !exercises.isEmpty else { return false }
        guard exercises.allSatisfy({ !$0.sets.isEmpty }) else { return false }
        return true
    }

    public var warmUpExercises: [SessionExerciseItem] {
        exercises.filter { $0.part == .warmUp }.sorted { $0.orderIndex < $1.orderIndex }
    }

    public var mainExercises: [SessionExerciseItem] {
        exercises.filter { $0.part == .main }.sorted { $0.orderIndex < $1.orderIndex }
    }

    public var coolDownExercises: [SessionExerciseItem] {
        exercises.filter { $0.part == .coolDown }.sorted { $0.orderIndex < $1.orderIndex }
    }

    public func loadCatalogExercises() async {
        do {
            self.availableExercises = try await exerciseRepository.getExercises(category: nil, search: nil)
        } catch {
            self.errorMessage = "Failed to load exercise catalog: \(error.localizedDescription)"
        }
    }

    public func addExercise(exercise: Exercise, to part: SessionPartEnum) {
        let currentPartExercises = exercises.filter { $0.part == part }
        let nextIndex = currentPartExercises.count

        let initialSet = ResistanceSet(
            setNumber: 1,
            setType: .normal,
            weightKg: 20.0,
            repetitions: 10,
            restSeconds: 90
        )

        let newItem = SessionExerciseItem(
            orderIndex: nextIndex,
            exerciseId: exercise.id,
            exerciseName: exercise.name,
            part: part,
            restSeconds: 90,
            sets: [initialSet]
        )

        exercises.append(newItem)
        reindexExercises(in: part)
    }

    public func removeExercise(id: String) {
        guard let item = exercises.first(where: { $0.id == id }) else { return }
        let part = item.part
        exercises.removeAll { $0.id == id }
        reindexExercises(in: part)
    }

    public func moveExercises(from source: IndexSet, to destination: Int, in part: SessionPartEnum) {
        var partItems = exercises.filter { $0.part == part }.sorted { $0.orderIndex < $1.orderIndex }
        partItems.move(fromOffsets: source, toOffset: destination)

        // Remove old part items from exercises
        exercises.removeAll { $0.part == part }

        // Re-index moved items and append back
        for (idx, var item) in partItems.enumerated() {
            item.orderIndex = idx
            exercises.append(item)
        }
    }

    public func addSet(to exerciseItemId: String) {
        guard let index = exercises.firstIndex(where: { $0.id == exerciseItemId }) else { return }
        var currentSets = exercises[index].sets
        let nextSetNum = currentSets.count + 1
        let lastWeight = currentSets.last?.weightKg ?? 20.0
        let lastReps = currentSets.last?.repetitions ?? 10
        let lastRest = currentSets.last?.restSeconds ?? 90

        let newSet = ResistanceSet(
            setNumber: nextSetNum,
            setType: .normal,
            weightKg: lastWeight,
            repetitions: lastReps,
            restSeconds: lastRest
        )
        currentSets.append(newSet)
        exercises[index].sets = currentSets
    }

    public func removeSet(from exerciseItemId: String, setNumber: Int) {
        guard let index = exercises.firstIndex(where: { $0.id == exerciseItemId }) else { return }
        var currentSets = exercises[index].sets
        currentSets.removeAll { $0.setNumber == setNumber }

        // Re-index remaining sets sequentially
        var reindexedSets: [ResistanceSet] = []
        for (idx, var set) in currentSets.enumerated() {
            set.setNumber = idx + 1
            reindexedSets.append(set)
        }
        exercises[index].sets = reindexedSets
    }

    public func updateSet(
        exerciseItemId: String,
        setNumber: Int,
        weightKg: Double,
        repetitions: Int,
        setType: SetType = .normal
    ) {
        guard let exIndex = exercises.firstIndex(where: { $0.id == exerciseItemId }) else { return }
        guard let setIndex = exercises[exIndex].sets.firstIndex(where: { $0.setNumber == setNumber }) else { return }

        exercises[exIndex].sets[setIndex].weightKg = weightKg
        exercises[exIndex].sets[setIndex].repetitions = repetitions
        exercises[exIndex].sets[setIndex].setType = setType
    }

    public func updateRestSeconds(exerciseItemId: String, restSeconds: Int) {
        guard let index = exercises.firstIndex(where: { $0.id == exerciseItemId }) else { return }
        exercises[index].restSeconds = restSeconds
    }

    public func save() async -> Bool {
        guard isValid else {
            errorMessage = "Please enter a valid template name and add at least one exercise with sets."
            return false
        }

        isSaving = true
        errorMessage = nil

        let session = Session(
            id: existingSessionId ?? 0,
            name: name.trimmingCharacters(in: .whitespacesAndNewlines),
            description: descriptionText.isEmpty ? nil : descriptionText,
            notes: notes.isEmpty ? nil : notes,
            estimatedDurationMinutes: estimatedDurationMinutes,
            exercises: exercises
        )

        do {
            _ = try await sessionRepository.saveSession(session)
            isSaving = false
            return true
        } catch {
            self.errorMessage = "Failed to save template: \(error.localizedDescription)"
            isSaving = false
            return false
        }
    }

    private func reindexExercises(in part: SessionPartEnum) {
        var partIndices: [Int] = []
        for i in 0..<exercises.count where exercises[i].part == part {
            partIndices.append(i)
        }
        for (newOrder, originalIndex) in partIndices.enumerated() {
            exercises[originalIndex].orderIndex = newOrder
        }
    }
}
