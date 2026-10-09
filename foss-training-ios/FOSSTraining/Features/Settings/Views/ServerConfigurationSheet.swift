import SwiftUI

public struct ServerConfigurationSheet: View {
    @Environment(\.dismiss) private var dismiss
    @Environment(\.theme) private var theme
    @Bindable var viewModel: SettingsViewModel

    @State private var serverUrlInput: String = ""
    @State private var apiKeyInput: String = ""
    @State private var isPinging: Bool = false

    public init(viewModel: SettingsViewModel) {
        self.viewModel = viewModel
    }

    public var body: some View {
        NavigationStack {
            Form {
                Section {
                    TextField("http://localhost:8080", text: $serverUrlInput)
                        .textContentType(.URL)
                        .keyboardType(.URL)
                        .autocorrectionDisabled()
                        .textInputAutocapitalization(.never)
                } header: {
                    Text("Backend Server URL")
                } footer: {
                    Text("Enter the root URL of your self-hosted Spring Boot backend service.")
                }

                Section {
                    SecureField("Optional API Key", text: $apiKeyInput)
                        .autocorrectionDisabled()
                        .textInputAutocapitalization(.never)
                } header: {
                    Text("Authentication")
                } footer: {
                    Text("If your server requires an Authorization header or API token.")
                }

                Section {
                    Button {
                        guard let url = URL(string: serverUrlInput), !serverUrlInput.isEmpty else { return }
                        isPinging = true
                        Task {
                            await viewModel.pingServer(url: url)
                            isPinging = false
                        }
                    } label: {
                        HStack {
                            Label("Ping Server", systemImage: "bolt.horizontal.circle.fill")
                            Spacer()
                            if isPinging {
                                ProgressView()
                            } else if let latency = viewModel.pingLatencyMs {
                                Text(String(format: "%.0f ms", latency))
                                    .font(.caption.bold())
                                    .foregroundStyle(.green)
                            }
                        }
                    }
                    .disabled(isPinging || serverUrlInput.isEmpty)
                    .tint(theme.selectedAccent.color)

                    if let status = viewModel.connectionTestStatus {
                        HStack {
                            Image(systemName: "checkmark.circle.fill")
                                .foregroundStyle(.green)
                            Text(status)
                                .font(.caption)
                                .foregroundStyle(.green)
                        }
                    }

                    if let err = viewModel.errorMessage {
                        HStack {
                            Image(systemName: "exclamationmark.triangle.fill")
                                .foregroundStyle(.red)
                            Text(err)
                                .font(.caption)
                                .foregroundStyle(.red)
                        }
                    }
                } header: {
                    Text("Diagnostics")
                }
            }
            .navigationTitle("Server Settings")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") {
                        dismiss()
                    }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Save") {
                        viewModel.saveRemoteServerUrl(serverUrlInput)
                        viewModel.saveApiKey(apiKeyInput)
                        dismiss()
                    }
                    .font(.headline)
                    .tint(theme.selectedAccent.color)
                }
            }
            .onAppear {
                serverUrlInput = viewModel.remoteServerUrlString
                apiKeyInput = viewModel.apiKey
            }
        }
    }
}
