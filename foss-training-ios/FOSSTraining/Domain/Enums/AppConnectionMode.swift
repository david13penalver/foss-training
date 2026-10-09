import Foundation

public enum AppConnectionMode: String, Codable, CaseIterable, Identifiable, Sendable {
    case localOffline = "LOCAL_OFFLINE"
    case remoteCloud = "REMOTE_CLOUD"

    public var id: String { rawValue }

    public var displayName: String {
        switch self {
        case .localOffline:
            return "Local-First (100% Offline)"
        case .remoteCloud:
            return "Connected (Spring Boot API)"
        }
    }
}
