import Foundation

// MARK: - Remote DTOs

public struct RemoteBodyweightRequest: Codable, Sendable {
    public let entryDate: String
    public let weightKg: Double
    public let bodyFatPercentage: Double?
    public let notes: String?

    public init(entryDate: String, weightKg: Double, bodyFatPercentage: Double? = nil, notes: String? = nil) {
        self.entryDate = entryDate
        self.weightKg = weightKg
        self.bodyFatPercentage = bodyFatPercentage
        self.notes = notes
    }
}

public struct RemoteBodyweightResponse: Codable, Sendable {
    public let id: Int
    public let entryDate: String
    public let weightKg: Double
    public let bodyFatPercentage: Double?
    public let notes: String?
    public let createdAt: String?

    public init(
        id: Int,
        entryDate: String,
        weightKg: Double,
        bodyFatPercentage: Double? = nil,
        notes: String? = nil,
        createdAt: String? = nil
    ) {
        self.id = id
        self.entryDate = entryDate
        self.weightKg = weightKg
        self.bodyFatPercentage = bodyFatPercentage
        self.notes = notes
        self.createdAt = createdAt
    }

    public func toDomain() -> BodyweightEntry {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        formatter.timeZone = TimeZone(secondsFromGMT: 0)
        let parsedDate = formatter.date(from: entryDate) ?? Date()
        return BodyweightEntry(
            id: id,
            weightKg: weightKg,
            measuredDate: parsedDate,
            notes: notes
        )
    }
}

public struct RemoteRelativeStrengthRequest: Codable, Sendable {
    public let totalWeightKg: Double
    public let bodyweightKg: Double
    public let gender: String

    public init(totalWeightKg: Double, bodyweightKg: Double, gender: String) {
        self.totalWeightKg = totalWeightKg
        self.bodyweightKg = bodyweightKg
        self.gender = gender
    }
}

public struct RemoteRelativeStrengthResponse: Codable, Sendable {
    public let totalWeightKg: Double
    public let bodyweightKg: Double
    public let gender: String?
    public let ratio: Double?
    public let dots: Double?
    public let wilks: Double?
    public let classification: String?

    public init(
        totalWeightKg: Double,
        bodyweightKg: Double,
        gender: String? = nil,
        ratio: Double? = nil,
        dots: Double? = nil,
        wilks: Double? = nil,
        classification: String? = nil
    ) {
        self.totalWeightKg = totalWeightKg
        self.bodyweightKg = bodyweightKg
        self.gender = gender
        self.ratio = ratio
        self.dots = dots
        self.wilks = wilks
        self.classification = classification
    }

    public func toDomain(formula: ScoringFormula) -> RelativeStrengthScore {
        let g = gender.flatMap { Gender(rawValue: $0) } ?? .male
        let dotsVal = dots ?? RelativeStrengthCalculator.calculateDots(totalKg: totalWeightKg, bwKg: bodyweightKg, gender: g)
        let wilksVal = wilks ?? RelativeStrengthCalculator.calculateWilks(totalKg: totalWeightKg, bwKg: bodyweightKg, gender: g)
        let ratioVal = ratio ?? ((totalWeightKg / bodyweightKg) * 100.0).rounded() / 100.0
        let evaluatedTier = RelativeStrengthTier.evaluate(dotsScore: dotsVal)
        let selectedScore = (formula == .dots) ? dotsVal : wilksVal

        return RelativeStrengthScore(
            formula: formula,
            score: selectedScore,
            totalKg: totalWeightKg,
            bodyweightKg: bodyweightKg,
            gender: g,
            strengthToWeightRatio: ratioVal,
            dotsScore: dotsVal,
            wilksScore: wilksVal,
            tier: evaluatedTier,
            tierDescription: evaluatedTier.description
        )
    }
}

public struct RemoteAthleteProfileDTO: Codable, Sendable {
    public let id: Int
    public let displayName: String
    public let gender: String
    public let dateOfBirth: String?
    public let heightCm: Double?
    public let experienceLevel: String
    public let targetGoal: String?
    public let preferredUnit: String

    public init(
        id: Int,
        displayName: String,
        gender: String,
        dateOfBirth: String? = nil,
        heightCm: Double? = nil,
        experienceLevel: String,
        targetGoal: String? = nil,
        preferredUnit: String
    ) {
        self.id = id
        self.displayName = displayName
        self.gender = gender
        self.dateOfBirth = dateOfBirth
        self.heightCm = heightCm
        self.experienceLevel = experienceLevel
        self.targetGoal = targetGoal
        self.preferredUnit = preferredUnit
    }

    public func toDomain() -> AthleteProfile {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        formatter.timeZone = TimeZone(secondsFromGMT: 0)
        let dob = dateOfBirth.flatMap { formatter.date(from: $0) }

        return AthleteProfile(
            id: id,
            displayName: displayName,
            gender: Gender(rawValue: gender) ?? .male,
            dateOfBirth: dob,
            heightCm: heightCm,
            experienceLevel: ProgramLevel(rawValue: experienceLevel) ?? .intermediate,
            targetGoal: targetGoal,
            preferredUnit: WeightUnit(rawValue: preferredUnit) ?? .kg
        )
    }

    public static func fromDomain(_ profile: AthleteProfile) -> RemoteAthleteProfileDTO {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        formatter.timeZone = TimeZone(secondsFromGMT: 0)
        let dobString = profile.dateOfBirth.map { formatter.string(from: $0) }

        return RemoteAthleteProfileDTO(
            id: profile.id,
            displayName: profile.displayName,
            gender: profile.gender.rawValue,
            dateOfBirth: dobString,
            heightCm: profile.heightCm,
            experienceLevel: profile.experienceLevel.rawValue,
            targetGoal: profile.targetGoal,
            preferredUnit: profile.preferredUnit.rawValue
        )
    }
}

// MARK: - RemoteAthleteRepository

public final class RemoteAthleteRepository: AthleteRepository, @unchecked Sendable {
    private let client: NetworkClient
    private let lock = NSLock()
    private var _cachedProfile: AthleteProfile = .default

    public init(client: NetworkClient) {
        self.client = client
    }

    private func readCachedProfile() -> AthleteProfile {
        lock.lock()
        defer { lock.unlock() }
        return _cachedProfile
    }

    private func writeCachedProfile(_ profile: AthleteProfile) {
        lock.lock()
        defer { lock.unlock() }
        _cachedProfile = profile
    }

    public func getProfile() async throws -> AthleteProfile? {
        do {
            let dto: RemoteAthleteProfileDTO = try await client.get(endpoint: "/api/athlete/profile")
            let domain = dto.toDomain()
            writeCachedProfile(domain)
            return domain
        } catch {
            return readCachedProfile()
        }
    }

    public func saveProfile(_ profile: AthleteProfile) async throws -> AthleteProfile {
        writeCachedProfile(profile)

        do {
            let request = RemoteAthleteProfileDTO.fromDomain(profile)
            let response: RemoteAthleteProfileDTO = try await client.put(endpoint: "/api/athlete/profile", body: request)
            let updated = response.toDomain()
            writeCachedProfile(updated)
            return updated
        } catch {
            return profile
        }
    }

    public func getBodyweightHistory() async throws -> [BodyweightEntry] {
        let list: [RemoteBodyweightResponse] = try await client.get(endpoint: "/api/athlete/bodyweight/history")
        return list.map { $0.toDomain() }
    }

    public func logBodyweight(entry: BodyweightEntry) async throws -> BodyweightEntry {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        formatter.timeZone = TimeZone(secondsFromGMT: 0)
        let dateString = formatter.string(from: entry.measuredDate)

        let request = RemoteBodyweightRequest(
            entryDate: dateString,
            weightKg: entry.weightKg,
            bodyFatPercentage: nil,
            notes: entry.notes
        )

        let response: RemoteBodyweightResponse = try await client.post(endpoint: "/api/athlete/bodyweight", body: request)
        return response.toDomain()
    }

    public func deleteBodyweight(id: Int) async throws {
        try await client.delete(endpoint: "/api/athlete/bodyweight/\(id)")
    }

    public func calculateRelativeStrength(
        totalKg: Double,
        bodyweightKg: Double,
        gender: Gender,
        formula: ScoringFormula
    ) async throws -> RelativeStrengthScore {
        let request = RemoteRelativeStrengthRequest(
            totalWeightKg: totalKg,
            bodyweightKg: bodyweightKg,
            gender: gender.rawValue
        )

        do {
            let response: RemoteRelativeStrengthResponse = try await client.post(
                endpoint: "/api/athlete/relative-strength",
                body: request
            )
            return response.toDomain(formula: formula)
        } catch {
            return RelativeStrengthCalculator.calculate(
                totalKg: totalKg,
                bodyweightKg: bodyweightKg,
                gender: gender,
                formula: formula
            )
        }
    }
}
