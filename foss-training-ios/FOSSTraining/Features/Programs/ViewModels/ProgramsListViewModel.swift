import SwiftUI

@Observable
@MainActor
public final class ProgramsListViewModel {
    private let programRepository: TrainingProgramRepository

    public var programs: [TrainingProgram] = []
    public var adherences: [Int: ProgramAdherence] = [:]
    public var selectedLevel: ProgramLevel? = nil
    public var searchText: String = ""
    public var isLoading: Bool = false
    public var isGeneratingSchedule: Bool = false
    public var errorMessage: String? = nil
    public var generationSuccessMessage: String? = nil

    public var filteredPrograms: [TrainingProgram] {
        let query = searchText.trimmingCharacters(in: .whitespacesAndNewlines)
        return programs.filter { program in
            let matchesLevel = (selectedLevel == nil || program.level == selectedLevel)
            let matchesSearch: Bool
            if query.isEmpty {
                matchesSearch = true
            } else {
                let nameMatches = program.name.localizedCaseInsensitiveContains(query)
                let descMatches = program.description?.localizedCaseInsensitiveContains(query) ?? false
                matchesSearch = nameMatches || descMatches
            }
            return matchesLevel && matchesSearch
        }
    }

    public init(programRepository: TrainingProgramRepository) {
        self.programRepository = programRepository
    }

    public func loadPrograms() async {
        isLoading = true
        errorMessage = nil
        do {
            let loaded = try await programRepository.getPrograms()
            self.programs = loaded

            var adherenceMap: [Int: ProgramAdherence] = [:]
            for p in loaded {
                if let adh = try? await programRepository.getProgramAdherence(id: p.id) {
                    adherenceMap[p.id] = adh
                }
            }
            self.adherences = adherenceMap
        } catch {
            self.errorMessage = error.localizedDescription
        }
        isLoading = false
    }

    public func deleteProgram(id: Int) async {
        do {
            try await programRepository.deleteProgram(id: id)
            await loadPrograms()
        } catch {
            self.errorMessage = "Failed to delete program: \(error.localizedDescription)"
        }
    }

    public func cloneProgram(id: Int, newName: String? = nil) async {
        do {
            _ = try await programRepository.cloneProgram(id: id, newName: newName)
            await loadPrograms()
        } catch {
            self.errorMessage = "Failed to clone program: \(error.localizedDescription)"
        }
    }

    public func generateSchedule(programId: Int, startDate: Date?) async {
        isGeneratingSchedule = true
        errorMessage = nil
        generationSuccessMessage = nil
        do {
            let generated = try await programRepository.generateSchedule(programId: programId, startDate: startDate)
            self.generationSuccessMessage = "Successfully generated \(generated.count) workouts."
            if let adh = try? await programRepository.getProgramAdherence(id: programId) {
                self.adherences[programId] = adh
            }
        } catch {
            self.errorMessage = "Failed to generate schedule: \(error.localizedDescription)"
        }
        isGeneratingSchedule = false
    }
}
