import SwiftUI
import SwiftData

public enum AppTierMode: String, CaseIterable, Identifiable, Sendable {
    case local = "local"
    case premium = "premium"

    public var id: String { rawValue }

    public var title: String {
        switch self {
        case .local: return "Local (SwiftData)"
        case .premium: return "Premium (Cloud API)"
        }
    }
}

@Observable
@MainActor
public final class AppEnvironment {
    private enum Keys {
        static let tierMode = "app_tier_mode"
        static let backendURL = "app_backend_url"
    }

    public let modelContainer: ModelContainer
    public let modelContext: ModelContext
    public let networkClient: NetworkClient

    public var tierMode: AppTierMode {
        didSet {
            UserDefaults.standard.set(tierMode.rawValue, forKey: Keys.tierMode)
            updateRepositories()
        }
    }

    public var backendURL: String {
        didSet {
            UserDefaults.standard.set(backendURL, forKey: Keys.backendURL)
            Task {
                await networkClient.setBaseURL(backendURL)
            }
        }
    }

    // Active Repositories
    public private(set) var exerciseRepository: ExerciseRepository
    public private(set) var sessionRepository: SessionRepository
    public private(set) var trainingRepository: TrainingRepository
    public private(set) var athleteRepository: AthleteRepository
    public private(set) var dataPortabilityRepository: DataPortabilityRepository
    public private(set) var trainingProgramRepository: TrainingProgramRepository
    public private(set) var analyticsRepository: AnalyticsRepository

    // Local Repositories for Migration Bridge
    public let localPortabilityRepository: SwiftDataPortabilityRepository

    public init(inMemory: Bool = false) {
        let schema = Schema([
            SDExercise.self,
            SDSession.self,
            SDSessionExercise.self,
            SDResistanceSet.self,
            SDTraining.self,
            SDBodyweightEntry.self,
            SDTrainingProgram.self,
            SDProgramWorkout.self,
            SDAthleteProfile.self
        ])

        let config = ModelConfiguration(schema: schema, isStoredInMemoryOnly: inMemory)
        let container = try! ModelContainer(for: schema, configurations: [config])
        self.modelContainer = container
        self.modelContext = container.mainContext

        let savedTier = UserDefaults.standard.string(forKey: Keys.tierMode) ?? AppTierMode.local.rawValue
        let currentMode = AppTierMode(rawValue: savedTier) ?? .local
        self.tierMode = currentMode

        let savedURL = UserDefaults.standard.string(forKey: Keys.backendURL) ?? "http://localhost:8080"
        self.backendURL = savedURL
        self.networkClient = NetworkClient(baseURLString: savedURL)

        self.localPortabilityRepository = SwiftDataPortabilityRepository(modelContext: modelContext)

        // Initial repositories
        let localEx = SwiftDataExerciseRepository(modelContext: modelContext)
        let localSes = SwiftDataSessionRepository(modelContext: modelContext)
        let localTr = SwiftDataTrainingRepository(modelContext: modelContext)
        let localAth = SwiftDataAthleteRepository(modelContext: modelContext)
        let localProg = SwiftDataTrainingProgramRepository(modelContext: modelContext)
        let localAnalytics = SwiftDataAnalyticsRepository(modelContext: modelContext)

        if currentMode == .premium {
            self.exerciseRepository = RemoteExerciseRepository(client: networkClient)
            self.sessionRepository = RemoteSessionRepository(client: networkClient)
            self.trainingRepository = RemoteTrainingRepository(client: networkClient)
            self.athleteRepository = RemoteAthleteRepository(client: networkClient)
            self.dataPortabilityRepository = RemotePortabilityRepository(client: networkClient)
            self.trainingProgramRepository = RemoteTrainingProgramRepository(client: networkClient)
            self.analyticsRepository = RemoteAnalyticsRepository(client: networkClient)
        } else {
            self.exerciseRepository = localEx
            self.sessionRepository = localSes
            self.trainingRepository = localTr
            self.athleteRepository = localAth
            self.dataPortabilityRepository = localPortabilityRepository
            self.trainingProgramRepository = localProg
            self.analyticsRepository = localAnalytics
        }

        // Seed initial data if needed
        ExerciseCatalogSeed.seedInitialDataIfNeeded(context: modelContext)
    }

    private func updateRepositories() {
        if tierMode == .premium {
            self.exerciseRepository = RemoteExerciseRepository(client: networkClient)
            self.sessionRepository = RemoteSessionRepository(client: networkClient)
            self.trainingRepository = RemoteTrainingRepository(client: networkClient)
            self.athleteRepository = RemoteAthleteRepository(client: networkClient)
            self.dataPortabilityRepository = RemotePortabilityRepository(client: networkClient)
            self.trainingProgramRepository = RemoteTrainingProgramRepository(client: networkClient)
            self.analyticsRepository = RemoteAnalyticsRepository(client: networkClient)
        } else {
            self.exerciseRepository = SwiftDataExerciseRepository(modelContext: modelContext)
            self.sessionRepository = SwiftDataSessionRepository(modelContext: modelContext)
            self.trainingRepository = SwiftDataTrainingRepository(modelContext: modelContext)
            self.athleteRepository = SwiftDataAthleteRepository(modelContext: modelContext)
            self.dataPortabilityRepository = localPortabilityRepository
            self.trainingProgramRepository = SwiftDataTrainingProgramRepository(modelContext: modelContext)
            self.analyticsRepository = SwiftDataAnalyticsRepository(modelContext: modelContext)
        }
    }

    public func migrateLocalDataToCloud() async throws -> Int {
        let localPayload = try await localPortabilityRepository.exportFullBackup()
        let remotePortability = RemotePortabilityRepository(client: networkClient)
        return try await remotePortability.importFullBackup(payload: localPayload)
    }
}
