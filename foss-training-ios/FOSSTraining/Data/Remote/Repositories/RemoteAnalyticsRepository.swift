import Foundation

// MARK: - Remote Analytics DTOs

public struct RemoteOneRepMaxResponseDto: Codable, Sendable {
    public let weight: Double
    public let unit: String?
    public let repetitions: Int
    public let formula: OneRepMaxFormula
    public let estimated1Rm: Double
    public let percentages: [String: Double]?

    public init(
        weight: Double,
        unit: String? = "KG",
        repetitions: Int,
        formula: OneRepMaxFormula,
        estimated1Rm: Double,
        percentages: [String: Double]? = nil
    ) {
        self.weight = weight
        self.unit = unit
        self.repetitions = repetitions
        self.formula = formula
        self.estimated1Rm = estimated1Rm
        self.percentages = percentages
    }

    public func toDomain() -> OneRepMaxEstimate {
        var mappedPercentages: [TrainingPercentage] = []
        if let percentages = percentages {
            for (pctStr, w) in percentages {
                if let pctInt = Int(pctStr) {
                    mappedPercentages.append(TrainingPercentage(percentage: pctInt, weightKg: w))
                }
            }
        }
        mappedPercentages.sort { $0.percentage > $1.percentage }
        return OneRepMaxEstimate(
            formula: formula,
            estimatedOneRepMax: estimated1Rm,
            percentages: mappedPercentages
        )
    }
}

public struct RemoteMaxWeightRecordDto: Codable, Sendable {
    public let value: Double
    public let unit: String?
    public let repetitions: Int?
    public let trainingId: Int?
    public let trainingDate: Date?

    public init(value: Double, unit: String? = "kg", repetitions: Int? = nil, trainingId: Int? = nil, trainingDate: Date? = nil) {
        self.value = value
        self.unit = unit
        self.repetitions = repetitions
        self.trainingId = trainingId
        self.trainingDate = trainingDate
    }
}

public struct RemoteBestEstimated1RmRecordDto: Codable, Sendable {
    public let estimated1Rm: Double
    public let unit: String?
    public let sourceWeight: Double?
    public let sourceReps: Int?
    public let formula: OneRepMaxFormula?
    public let trainingId: Int?
    public let trainingDate: Date?

    public init(estimated1Rm: Double, unit: String? = "kg", sourceWeight: Double? = nil, sourceReps: Int? = nil, formula: OneRepMaxFormula? = nil, trainingId: Int? = nil, trainingDate: Date? = nil) {
        self.estimated1Rm = estimated1Rm
        self.unit = unit
        self.sourceWeight = sourceWeight
        self.sourceReps = sourceReps
        self.formula = formula
        self.trainingId = trainingId
        self.trainingDate = trainingDate
    }
}

public struct RemoteMaxSessionVolumeRecordDto: Codable, Sendable {
    public let volume: Double
    public let unit: String?
    public let trainingId: Int?
    public let trainingDate: Date?

    public init(volume: Double, unit: String? = "kg", trainingId: Int? = nil, trainingDate: Date? = nil) {
        self.volume = volume
        self.unit = unit
        self.trainingId = trainingId
        self.trainingDate = trainingDate
    }
}

public struct RemoteMaxRepsRecordDto: Codable, Sendable {
    public let repetitions: Int
    public let weight: Double?
    public let unit: String?
    public let trainingId: Int?
    public let trainingDate: Date?

    public init(repetitions: Int, weight: Double? = nil, unit: String? = "reps", trainingId: Int? = nil, trainingDate: Date? = nil) {
        self.repetitions = repetitions
        self.weight = weight
        self.unit = unit
        self.trainingId = trainingId
        self.trainingDate = trainingDate
    }
}

public struct RemotePersonalRecordResponseDto: Codable, Sendable {
    public let exerciseId: Int
    public let exerciseName: String
    public let maxWeight: RemoteMaxWeightRecordDto?
    public let bestEstimated1Rm: RemoteBestEstimated1RmRecordDto?
    public let maxSessionVolume: RemoteMaxSessionVolumeRecordDto?
    public let maxReps: RemoteMaxRepsRecordDto?

    public init(
        exerciseId: Int,
        exerciseName: String,
        maxWeight: RemoteMaxWeightRecordDto? = nil,
        bestEstimated1Rm: RemoteBestEstimated1RmRecordDto? = nil,
        maxSessionVolume: RemoteMaxSessionVolumeRecordDto? = nil,
        maxReps: RemoteMaxRepsRecordDto? = nil
    ) {
        self.exerciseId = exerciseId
        self.exerciseName = exerciseName
        self.maxWeight = maxWeight
        self.bestEstimated1Rm = bestEstimated1Rm
        self.maxSessionVolume = maxSessionVolume
        self.maxReps = maxReps
    }

    public func toDomain() -> [PersonalRecord] {
        var records: [PersonalRecord] = []
        if let mw = maxWeight {
            let u = mw.unit != nil ? mw.unit! : "kg"
            let d = mw.trainingDate != nil ? mw.trainingDate! : Date()
            let tid = mw.trainingId != nil ? mw.trainingId! : 0
            records.append(PersonalRecord(
                exerciseId: exerciseId,
                exerciseName: exerciseName,
                recordType: .maxWeight,
                value: mw.value,
                unit: u,
                achievedDate: d,
                trainingId: tid
            ))
        }
        if let b1 = bestEstimated1Rm {
            let u = b1.unit != nil ? b1.unit! : "kg"
            let d = b1.trainingDate != nil ? b1.trainingDate! : Date()
            let tid = b1.trainingId != nil ? b1.trainingId! : 0
            records.append(PersonalRecord(
                exerciseId: exerciseId,
                exerciseName: exerciseName,
                recordType: .maxEstimated1RM,
                value: b1.estimated1Rm,
                unit: u,
                achievedDate: d,
                trainingId: tid
            ))
        }
        if let mv = maxSessionVolume {
            let u = mv.unit != nil ? mv.unit! : "kg"
            let d = mv.trainingDate != nil ? mv.trainingDate! : Date()
            let tid = mv.trainingId != nil ? mv.trainingId! : 0
            records.append(PersonalRecord(
                exerciseId: exerciseId,
                exerciseName: exerciseName,
                recordType: .maxVolume,
                value: mv.volume,
                unit: u,
                achievedDate: d,
                trainingId: tid
            ))
        }
        if let mr = maxReps {
            let u = mr.unit != nil ? mr.unit! : "reps"
            let d = mr.trainingDate != nil ? mr.trainingDate! : Date()
            let tid = mr.trainingId != nil ? mr.trainingId! : 0
            records.append(PersonalRecord(
                exerciseId: exerciseId,
                exerciseName: exerciseName,
                recordType: .maxReps,
                value: Double(mr.repetitions),
                unit: u,
                achievedDate: d,
                trainingId: tid
            ))
        }
        return records
    }
}

// MARK: - Remote Analytics Repository Implementation

public final class RemoteAnalyticsRepository: AnalyticsRepository, @unchecked Sendable {
    private let client: NetworkClient

    public init(client: NetworkClient) {
        self.client = client
    }

    public func calculateOneRepMax(weightKg: Double, reps: Int) async throws -> [OneRepMaxEstimate] {
        var estimates: [OneRepMaxEstimate] = []
        for formula in OneRepMaxFormula.allCases {
            let dto: RemoteOneRepMaxResponseDto = try await client.get(
                endpoint: "/api/analytics/1rm?weight=\(weightKg)&reps=\(reps)&formula=\(formula.rawValue)"
            )
            estimates.append(dto.toDomain())
        }
        return estimates
    }

    public func getPersonalRecords(exerciseId: Int?) async throws -> [PersonalRecord] {
        if let id = exerciseId {
            do {
                let dto: RemotePersonalRecordResponseDto = try await client.get(
                    endpoint: "/api/analytics/personal-records/exercise/\(id)"
                )
                return dto.toDomain()
            } catch APIError.serverError(let code, _) where code == 404 {
                return []
            }
        } else {
            let dtos: [RemotePersonalRecordResponseDto] = try await client.get(
                endpoint: "/api/analytics/personal-records"
            )
            return dtos.flatMap { $0.toDomain() }
        }
    }

    public func calculateAcwr(asOfDate: Date?) async throws -> WorkloadRatio {
        var endpoint = "/api/analytics/acwr"
        if let date = asOfDate {
            let formatter = ISO8601DateFormatter()
            formatter.formatOptions = [.withFullDate]
            let dateStr = formatter.string(from: date)
            endpoint += "?targetDate=\(dateStr)"
        }
        return try await client.get(endpoint: endpoint)
    }

    public func getWeeklyMuscleVolume(weekStartDate: Date?) async throws -> WeeklyMuscleVolume {
        var endpoint = "/api/analytics/muscle-volume"
        if let start = weekStartDate {
            let formatter = ISO8601DateFormatter()
            formatter.formatOptions = [.withFullDate]
            let startStr = formatter.string(from: start)
            let endDate = Calendar.current.date(byAdding: .day, value: 6, to: start)!
            let endStr = formatter.string(from: endDate)
            endpoint += "?startDate=\(startStr)&endDate=\(endStr)"
        }
        return try await client.get(endpoint: endpoint)
    }

    public func getExerciseProgression(exerciseId: Int, months: Int) async throws -> ExerciseProgression {
        var endpoint = "/api/analytics/progression/\(exerciseId)"
        if months > 0 {
            let formatter = ISO8601DateFormatter()
            formatter.formatOptions = [.withFullDate]
            let startDate = Calendar.current.date(byAdding: .month, value: -months, to: Date())!
            let startStr = formatter.string(from: startDate)
            endpoint += "?startDate=\(startStr)"
        }
        return try await client.get(endpoint: endpoint)
    }

    public func calculateHeartRateZones(restingHr: Int, maxHr: Int, method: HeartRateZoneMethod) async throws -> HeartRateZones {
        var endpoint = "/api/analytics/heart-rate-zones?maxHr=\(maxHr)"
        if method == .karvonen {
            endpoint += "&restingHr=\(restingHr)"
        }
        return try await client.get(endpoint: endpoint)
    }
}
