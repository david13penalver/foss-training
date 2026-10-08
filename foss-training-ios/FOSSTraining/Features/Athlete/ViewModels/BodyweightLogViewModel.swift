import Foundation
import SwiftUI

@Observable
@MainActor
public final class BodyweightLogViewModel {
    private let athleteRepository: AthleteRepository

    public var weightKg: Double = 75.0
    public var weightString: String = "75.0"
    public var entryDate: Date = Date()
    public var notes: String = ""
    public var lastLoggedWeight: Double?
    public var isLoading: Bool = false
    public var isSaved: Bool = false
    public var errorMessage: String?

    public init(
        athleteRepository: AthleteRepository,
        initialWeight: Double? = nil,
        lastWeight: Double? = nil
    ) {
        self.athleteRepository = athleteRepository
        let startingWeight = initialWeight ?? lastWeight ?? 75.0
        self.weightKg = startingWeight
        self.weightString = String(format: "%.1f", startingWeight)
        self.lastLoggedWeight = lastWeight
    }

    public func adjustWeight(delta: Double) {
        let newWeight = max(20.0, min(300.0, ((weightKg + delta) * 10.0).rounded() / 10.0))
        self.weightKg = newWeight
        self.weightString = String(format: "%.1f", newWeight)
    }

    public func setSameAsYesterday() {
        if let last = lastLoggedWeight {
            self.weightKg = last
            self.weightString = String(format: "%.1f", last)
        }
    }

    public func updateWeightFromString(_ text: String) {
        self.weightString = text
        if let val = Double(text.replacingOccurrences(of: ",", with: ".")), val > 0 {
            self.weightKg = val
        }
    }

    public func save() async -> Bool {
        guard let parsed = Double(weightString.replacingOccurrences(of: ",", with: ".")), parsed >= 20.0, parsed <= 350.0 else {
            self.errorMessage = "Please enter a valid weight between 20.0 and 350.0 kg."
            return false
        }

        self.isLoading = true
        self.errorMessage = nil

        do {
            let entry = BodyweightEntry(
                id: 0,
                weightKg: parsed,
                measuredDate: entryDate,
                notes: notes.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty ? nil : notes.trimmingCharacters(in: .whitespacesAndNewlines)
            )
            _ = try await athleteRepository.logBodyweight(entry: entry)
            self.isSaved = true
            self.isLoading = false
            return true
        } catch {
            self.errorMessage = error.localizedDescription
            self.isLoading = false
            return false
        }
    }
}
