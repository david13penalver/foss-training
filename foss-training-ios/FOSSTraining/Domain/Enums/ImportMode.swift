import Foundation

public enum ImportMode: String, Codable, CaseIterable, Sendable, Identifiable {
    case merge = "MERGE"
    case overwrite = "OVERWRITE"
    case skipExisting = "SKIP_EXISTING"

    public var id: String { rawValue }

    public var displayName: String {
        switch self {
        case .merge: return "Merge (Keep Both)"
        case .overwrite: return "Overwrite Existing"
        case .skipExisting: return "Skip Existing"
        }
    }

    public var description: String {
        switch self {
        case .merge:
            return "Imports new entities while giving colliding IDs fresh identifiers to keep both copies."
        case .overwrite:
            return "Overwrites local records with matching IDs or keys from the backup snapshot."
        case .skipExisting:
            return "Leaves existing local records untouched and only adds records with new IDs."
        }
    }
}
