import SwiftUI
import UniformTypeIdentifiers

public struct DataPortabilityView: View {
    @Environment(\.theme) private var theme
    @State private var viewModel: DataPortabilityViewModel
    @State private var isImportingFile = false

    public init(portabilityRepository: DataPortabilityRepository) {
        self._viewModel = State(initialValue: DataPortabilityViewModel(portabilityRepository: portabilityRepository))
    }

    private var todayDateString: String {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        return formatter.string(from: Date())
    }

    public var body: some View {
        Form {
            // Status feedback
            if let success = viewModel.successMessage {
                Section {
                    Label(success, systemImage: "checkmark.circle.fill")
                        .font(.subheadline)
                        .foregroundStyle(.green)
                }
            }

            if let error = viewModel.errorMessage {
                Section {
                    Label(error, systemImage: "exclamationmark.triangle.fill")
                        .font(.subheadline)
                        .foregroundStyle(.red)
                }
            }

            // Section 1: Export Hub
            Section {
                Button {
                    Task { await viewModel.exportJsonBackup() }
                } label: {
                    HStack {
                        Label("Export Full JSON Backup", systemImage: "arrow.down.doc.fill")
                        Spacer()
                        if viewModel.isLoading && viewModel.isExportingJson {
                            ProgressView()
                        }
                    }
                }

                Button {
                    Task { await viewModel.exportWorkoutsCsv() }
                } label: {
                    HStack {
                        Label("Export Workouts as CSV", systemImage: "tablecells.badge.ellipsis")
                        Spacer()
                        if viewModel.isLoading && viewModel.isExportingCsv {
                            ProgressView()
                        }
                    }
                }
            } header: {
                Label("Export Data", systemImage: "square.and.arrow.up")
            } footer: {
                Text("Export complete database snapshot as a single JSON file, or workout execution history formatted for spreadsheet analysis.")
            }

            // Section 2: Import & Restore
            Section {
                Button {
                    isImportingFile = true
                } label: {
                    Label("Restore Backup or Import CSV", systemImage: "square.and.arrow.down.fill")
                }
            } header: {
                Label("Import & Restore", systemImage: "square.and.arrow.down")
            } footer: {
                Text("Select a .json backup file to restore database records with conflict resolution, or import a .csv file of workouts.")
            }

            // Section 3: Cloud Migration Bridge
            Section {
                Button {
                    viewModel.showMigrationSheet = true
                } label: {
                    HStack {
                        VStack(alignment: .leading, spacing: 2) {
                            Text("1-Click Cloud Migration")
                                .font(.headline)
                            Text("Transfer local SwiftData directly to Spring Boot backend")
                                .font(.caption)
                                .foregroundStyle(.secondary)
                        }
                        Spacer()
                        Image(systemName: "chevron.right")
                            .font(.caption)
                            .foregroundStyle(.secondary)
                    }
                }
            } header: {
                Label("Cloud Migration Bridge", systemImage: "cloud.fill")
            }

            // Section 4: Danger Zone (Purge)
            Section {
                Button(role: .destructive) {
                    viewModel.showPurgeConfirmation = true
                } label: {
                    Label("Erase All Local Data", systemImage: "trash.fill")
                        .foregroundStyle(.red)
                }
            } header: {
                Label("Danger Zone", systemImage: "exclamationmark.shield.fill")
            } footer: {
                Text("Permanently removes all exercises, sessions, programs, workouts, and bodyweights stored on this device.")
            }
        }
        .navigationTitle("Data Sovereignty")
        .fileExporter(
            isPresented: $viewModel.isExportingJson,
            document: JSONBackupDocument(data: viewModel.exportedJsonData ?? Data()),
            contentType: .json,
            defaultFilename: "foss-training-backup-\(todayDateString).json"
        ) { _ in }
        .fileExporter(
            isPresented: $viewModel.isExportingCsv,
            document: CSVWorkoutsDocument(text: viewModel.exportedCsvString ?? ""),
            contentType: .commaSeparatedText,
            defaultFilename: "foss-training-workouts-\(todayDateString).csv"
        ) { _ in }
        .fileImporter(
            isPresented: $isImportingFile,
            allowedContentTypes: [.json, .commaSeparatedText, .plainText]
        ) { result in
            switch result {
            case .success(let url):
                guard url.startAccessingSecurityScopedResource() else { return }
                defer { url.stopAccessingSecurityScopedResource() }
                if let data = try? Data(contentsOf: url) {
                    if url.pathExtension.lowercased() == "csv" {
                        if let text = String(data: data, encoding: .utf8) {
                            Task { await viewModel.handleImportedCsvString(text) }
                        }
                    } else {
                        viewModel.handleImportedJsonData(data)
                    }
                }
            case .failure(let error):
                viewModel.errorMessage = "Failed to access file: \(error.localizedDescription)"
            }
        }
        .sheet(isPresented: $viewModel.showConflictModal) {
            ImportConflictModal(selectedMode: $viewModel.selectedImportMode) { mode in
                Task { await viewModel.confirmImport(mode: mode) }
            }
        }
        .sheet(isPresented: $viewModel.showMigrationSheet) {
            CloudMigrationSheet(viewModel: viewModel)
        }
        .alert("Erase All Local Data?", isPresented: $viewModel.showPurgeConfirmation) {
            TextField("Type DELETE to confirm", text: $viewModel.purgeConfirmationText)
            Button("Cancel", role: .cancel) { viewModel.purgeConfirmationText = "" }
            Button("Erase Database", role: .destructive) {
                Task { _ = await viewModel.purgeDatabase() }
            }
        } message: {
            Text("This action cannot be undone. To permanently delete all local training data, type DELETE below.")
        }
    }
}

public struct JSONBackupDocument: FileDocument {
    public static var readableContentTypes: [UTType] { [.json] }
    public var data: Data

    public init(data: Data = Data()) {
        self.data = data
    }

    public init(configuration: ReadConfiguration) throws {
        self.data = configuration.file.regularFileContents ?? Data()
    }

    public func fileWrapper(configuration: WriteConfiguration) throws -> FileWrapper {
        FileWrapper(regularFileWithContents: data)
    }
}

public struct CSVWorkoutsDocument: FileDocument {
    public static var readableContentTypes: [UTType] { [.commaSeparatedText, .plainText] }
    public var text: String

    public init(text: String = "") {
        self.text = text
    }

    public init(configuration: ReadConfiguration) throws {
        if let data = configuration.file.regularFileContents,
           let string = String(data: data, encoding: .utf8) {
            self.text = string
        } else {
            self.text = ""
        }
    }

    public func fileWrapper(configuration: WriteConfiguration) throws -> FileWrapper {
        let data = text.data(using: .utf8) ?? Data()
        return FileWrapper(regularFileWithContents: data)
    }
}
