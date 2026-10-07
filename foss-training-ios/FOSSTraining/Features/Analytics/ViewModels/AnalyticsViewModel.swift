import SwiftUI

public struct MuscleVolumeItem: Identifiable, Sendable {
    public var id: String { muscle }
    public let muscle: String
    public let volumeKg: Double
}

@Observable
@MainActor
public final class AnalyticsViewModel {
    private let trainingRepository: TrainingRepository
    private let athleteRepository: AthleteRepository

    public var muscleVolumes: [MuscleVolumeItem] = []
    public var bodyweightHistory: [BodyweightEntry] = []
    public var currentAcwrRatio: Double = 1.05
    public var newBodyweightString: String = ""
    public var isLoading: Bool = false

    public init(trainingRepository: TrainingRepository, athleteRepository: AthleteRepository) {
        self.trainingRepository = trainingRepository
        self.athleteRepository = athleteRepository
    }

    public func loadAnalytics() async {
        isLoading = true
        do {
            let trainings = try await trainingRepository.getTrainings()
            self.bodyweightHistory = try await athleteRepository.getBodyweightHistory()

            // Calculate volume per muscle group across completed trainings
            var volumeMap: [String: Double] = [
                "Chest": 0.0,
                "Back": 0.0,
                "Legs": 0.0,
                "Shoulders": 0.0,
                "Arms": 0.0
            ]

            for tr in trainings where tr.status == .completed {
                for ex in tr.loggedExercises {
                    let exVol = ex.sets.filter(\.isCompleted).reduce(0.0) { $0 + $1.volumeKg }
                    let nameLower = ex.exerciseName.lowercased()
                    if nameLower.contains("bench") || nameLower.contains("chest") || nameLower.contains("push") {
                        volumeMap["Chest", default: 0] += exVol
                    } else if nameLower.contains("pull") || nameLower.contains("deadlift") || nameLower.contains("row") {
                        volumeMap["Back", default: 0] += exVol
                    } else if nameLower.contains("squat") || nameLower.contains("leg") {
                        volumeMap["Legs", default: 0] += exVol
                    } else if nameLower.contains("press") || nameLower.contains("shoulder") {
                        volumeMap["Shoulders", default: 0] += exVol
                    } else {
                        volumeMap["Arms", default: 0] += exVol
                    }
                }
            }

            self.muscleVolumes = volumeMap.map { MuscleVolumeItem(muscle: $0.key, volumeKg: $0.value) }
                .sorted { $0.volumeKg > $1.volumeKg }
        } catch {}
        isLoading = false
    }

    public func logCurrentBodyweight() async {
        guard let val = Double(newBodyweightString), val > 20 else { return }
        do {
            let entry = BodyweightEntry(id: 0, weightKg: val, measuredDate: Date())
            _ = try await athleteRepository.logBodyweight(entry: entry)
            self.newBodyweightString = ""
            self.bodyweightHistory = try await athleteRepository.getBodyweightHistory()
        } catch {}
    }
}
