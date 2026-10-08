import SwiftUI

public struct SettingsView: View {
    @Environment(\.theme) private var theme
    @Bindable var appEnvironment: AppEnvironment
    @State private var viewModel: SettingsViewModel

    public init(appEnvironment: AppEnvironment) {
        self.appEnvironment = appEnvironment
        self._viewModel = State(initialValue: SettingsViewModel(appEnvironment: appEnvironment))
    }

    public var body: some View {
        NavigationStack {
            Form {
                // Section 0: Athlete Profile & Powerlifting
                Section {
                    NavigationLink {
                        AthleteProfileView(
                            athleteRepository: appEnvironment.athleteRepository,
                            trainingRepository: appEnvironment.trainingRepository
                        )
                    } label: {
                        HStack(spacing: 12) {
                            ZStack {
                                Circle()
                                    .fill(theme.selectedAccent.color.opacity(0.18))
                                    .frame(width: 40, height: 40)
                                Image(systemName: "person.crop.circle.fill")
                                    .font(.title3)
                                    .foregroundStyle(theme.selectedAccent.color)
                            }
                            VStack(alignment: .leading, spacing: 2) {
                                Text("Athlete Profile & Powerlifting")
                                    .font(.headline)
                                Text("Biometrics, Big 3 PRs, DOTS & Wilks scoring")
                                    .font(.caption)
                                    .foregroundStyle(.secondary)
                            }
                        }
                        .padding(.vertical, 4)
                    }
                } header: {
                    Label("Athlete Profile", systemImage: "person.fill")
                }

                // Section 1: Appearance & Color Personalization
                Section {
                    VStack(alignment: .leading, spacing: 12) {
                        Text("Accent Color")
                            .font(.subheadline)
                            .foregroundStyle(.secondary)

                        LazyVGrid(columns: Array(repeating: GridItem(.flexible(), spacing: 12), count: 6), spacing: 12) {
                            ForEach(AppAccentColor.allCases) { accent in
                                Button {
                                    theme.setAccent(accent)
                                } label: {
                                    ZStack {
                                        Circle()
                                            .fill(accent.color)
                                            .frame(width: 44, height: 44)
                                            .shadow(color: theme.selectedAccent == accent ? accent.glowColor : .clear, radius: 8)

                                        if theme.selectedAccent == accent {
                                            Circle()
                                                .strokeBorder(Color.white, lineWidth: 3)
                                                .frame(width: 48, height: 48)
                                            Image(systemName: "checkmark")
                                                .font(.headline.weight(.bold))
                                                .foregroundStyle(accent.badgeTextColor)
                                        }
                                    }
                                }
                                .buttonStyle(.plain)
                            }
                        }
                        .padding(.vertical, 4)

                        Text("Current: \(theme.selectedAccent.rawValue)")
                            .font(.caption)
                            .foregroundStyle(theme.selectedAccent.color)
                    }

                    Picker("Surface Style", selection: Binding(
                        get: { theme.surfaceStyle },
                        set: { theme.setSurface($0) }
                    )) {
                        ForEach(SurfaceStyle.allCases) { style in
                            Text(style.rawValue).tag(style)
                        }
                    }

                    Toggle("Tactile Haptic Feedback", isOn: Binding(
                        get: { theme.hapticsEnabled },
                        set: { theme.hapticsEnabled = $0 }
                    ))
                } header: {
                    Label("Personalization & Appearance", systemImage: "paintpalette.fill")
                } footer: {
                    Text("Colors and surface styles are instantly saved to local storage (@AppStorage) with zero startup latency.")
                }

                // Section 2: Operating Mode (Local vs Premium)
                Section {
                    Picker("App Mode", selection: $appEnvironment.tierMode) {
                        ForEach(AppTierMode.allCases) { mode in
                            Text(mode.title).tag(mode)
                        }
                    }
                    .pickerStyle(.segmented)
                    .padding(.vertical, 4)

                    if appEnvironment.tierMode == .premium {
                        VStack(alignment: .leading, spacing: 8) {
                            Text("Backend API Host")
                                .font(.caption)
                                .foregroundStyle(.secondary)
                            TextField("http://localhost:8080", text: $appEnvironment.backendURL)
                                .textFieldStyle(.roundedBorder)
                                .textInputAutocapitalization(.never)
                                .autocorrectionDisabled()

                            Button {
                                Task { await viewModel.testConnection() }
                            } label: {
                                Label("Test Backend Connection", systemImage: "network")
                            }
                            .buttonStyle(.bordered)
                            .tint(theme.selectedAccent.color)

                            if let status = viewModel.connectionTestStatus {
                                Text(status)
                                    .font(.caption)
                                    .foregroundStyle(.green)
                            }
                        }
                    }
                } header: {
                    Label("Operating Mode", systemImage: "externaldrive.fill")
                } footer: {
                    Text(appEnvironment.tierMode == .local
                         ? "Local tier runs 100% offline using SwiftData on your iPhone."
                         : "Premium tier communicates directly with your Java Spring Boot REST API.")
                }

                // Section 3: Data Migration Bridge
                if appEnvironment.tierMode == .premium {
                    Section {
                        Button {
                            Task { await viewModel.syncLocalDataToCloud() }
                        } label: {
                            HStack {
                                Label("Migrate Local Data to Cloud", systemImage: "arrow.triangle.2.circlepath.circle.fill")
                                Spacer()
                                if viewModel.isMigrating {
                                    ProgressView()
                                }
                            }
                        }
                        .disabled(viewModel.isMigrating)
                        .tint(theme.selectedAccent.color)

                        if let msg = viewModel.migrationSuccessMessage {
                            Text(msg)
                                .font(.caption)
                                .foregroundStyle(.green)
                        }
                    } header: {
                        Label("Cloud Data Synchronization", systemImage: "icloud.and.arrow.up.fill")
                    } footer: {
                        Text("Uploads all exercises, templates, completed workouts, and bodyweight history from SwiftData to your Spring Boot database.")
                    }
                }

                // Section 4: Diagnostics & Errors
                if let err = viewModel.errorMessage {
                    Section("Status") {
                        Text(err)
                            .font(.caption)
                            .foregroundStyle(.red)
                    }
                }
            }
            .navigationTitle("Settings")
        }
    }
}
