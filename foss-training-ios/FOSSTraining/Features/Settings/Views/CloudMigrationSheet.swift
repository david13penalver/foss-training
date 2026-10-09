import SwiftUI

public struct CloudMigrationSheet: View {
    @Environment(\.dismiss) private var dismiss
    @Environment(\.theme) private var theme
    @Bindable var viewModel: DataPortabilityViewModel

    public init(viewModel: DataPortabilityViewModel) {
        self.viewModel = viewModel
    }

    public var body: some View {
        NavigationStack {
            Form {
                Section {
                    VStack(alignment: .leading, spacing: 6) {
                        Text("1-Click Cloud Migration")
                            .font(.headline)
                        Text("Transfer your complete local SwiftData history (exercises, sessions, programs, workouts, and bodyweights) to a self-hosted Spring Boot backend.")
                            .font(.caption)
                            .foregroundStyle(.secondary)
                    }
                    .padding(.vertical, 4)
                }

                Section("Target Backend Server") {
                    TextField("http://localhost:8080", text: $viewModel.serverUrlString)
                        .textFieldStyle(.plain)
                        .textInputAutocapitalization(.never)
                        .autocorrectionDisabled()

                    Button {
                        Task { await viewModel.testServerConnection() }
                    } label: {
                        HStack {
                            Text("Test Connection")
                            Spacer()
                            if viewModel.isTestingConnection {
                                ProgressView()
                            } else if let success = viewModel.isConnectionSuccessful {
                                Image(systemName: success ? "checkmark.circle.fill" : "xmark.circle.fill")
                                    .foregroundStyle(success ? .green : .red)
                            }
                        }
                    }
                    .disabled(viewModel.isTestingConnection)
                }

                Section("Options") {
                    Toggle("Purge local database after successful migration", isOn: $viewModel.purgeAfterMigration)
                }

                if let err = viewModel.errorMessage {
                    Section {
                        Text(err)
                            .font(.caption)
                            .foregroundStyle(.red)
                    }
                }

                Section {
                    Button {
                        Task { await viewModel.executeMigration() }
                    } label: {
                        HStack {
                            Spacer()
                            if viewModel.isMigrating {
                                ProgressView()
                                    .padding(.trailing, 8)
                                Text("Migrating Data...")
                            } else {
                                Text("Start Cloud Migration")
                                    .font(.headline)
                            }
                            Spacer()
                        }
                    }
                    .disabled(viewModel.isMigrating)
                    .tint(theme.selectedAccent.color)
                }
            }
            .navigationTitle("Cloud Migration")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Close") { dismiss() }
                }
            }
        }
    }
}
