import SwiftUI

public struct SettingsView: View {
    @Environment(\.theme) private var theme
    @Bindable var appEnvironment: AppEnvironment
    @State private var viewModel: SettingsViewModel
    @State private var isServerConfigSheetPresented: Bool = false

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
                    AccentColorPicker()

                    Picker("Surface Style", selection: Binding(
                        get: { theme.surfaceStyle },
                        set: { theme.setSurface($0) }
                    )) {
                        ForEach(SurfaceStyle.allCases) { style in
                            Text(style.rawValue).tag(style)
                        }
                    }
                } header: {
                    Label("Appearance & Theme", systemImage: "paintpalette.fill")
                } footer: {
                    Text("Theme settings are applied instantly with zero startup latency.")
                }

                // Section 2: Gym Ergonomics & Sensory Feedback
                Section {
                    Toggle("Tactile Haptics", isOn: Binding(
                        get: { theme.hapticsEnabled },
                        set: { theme.hapticsEnabled = $0 }
                    ))

                    if theme.hapticsEnabled {
                        Picker("Haptic Intensity", selection: Binding(
                            get: { theme.hapticIntensity },
                            set: { theme.setHapticIntensity($0) }
                        )) {
                            ForEach(HapticIntensity.allCases.filter { $0 != .disabled }) { intensity in
                                Text(intensity.rawValue).tag(intensity)
                            }
                        }
                    }

                    Toggle("Rest Timer Audio Cues", isOn: Binding(
                        get: { theme.soundEffectsEnabled },
                        set: { theme.soundEffectsEnabled = $0 }
                    ))

                    Toggle("Keep Screen Awake in Workouts", isOn: Binding(
                        get: { theme.keepScreenAwake },
                        set: { theme.keepScreenAwake = $0 }
                    ))

                    Picker("Preferred Weight Unit", selection: Binding(
                        get: { theme.weightUnit },
                        set: { theme.setWeightUnit($0) }
                    )) {
                        ForEach(WeightUnit.allCases) { unit in
                            Text(unit.displayName).tag(unit)
                        }
                    }
                } header: {
                    Label("Gym Ergonomics & Sensory", systemImage: "hand.tap.fill")
                } footer: {
                    Text("Haptics and audio alerts let you feel set progression without staring at your screen.")
                }

                // Section 3: Data Connectivity & Backend
                Section {
                    Picker("App Mode", selection: $appEnvironment.tierMode) {
                        ForEach(AppTierMode.allCases) { mode in
                            Text(mode.title).tag(mode)
                        }
                    }
                    .pickerStyle(.segmented)
                    .padding(.vertical, 4)

                    if appEnvironment.tierMode == .premium {
                        Button {
                            isServerConfigSheetPresented = true
                        } label: {
                            HStack {
                                Label("Configure Server Connection", systemImage: "server.rack")
                                Spacer()
                                Image(systemName: "chevron.right")
                                    .font(.caption)
                                    .foregroundStyle(.secondary)
                            }
                        }
                        .tint(theme.selectedAccent.color)

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
                    }
                } header: {
                    Label("Data & Connectivity", systemImage: "network")
                } footer: {
                    Text(appEnvironment.tierMode == .local
                         ? "Local-First tier runs 100% offline using on-device SwiftData."
                         : "Connected tier communicates directly with your self-hosted Spring Boot REST API.")
                }

                // Section 4: Data Sovereignty & Portability
                Section {
                    NavigationLink {
                        DataPortabilityView(portabilityRepository: appEnvironment.dataPortabilityRepository)
                    } label: {
                        HStack(spacing: 12) {
                            ZStack {
                                Circle()
                                    .fill(theme.selectedAccent.color.opacity(0.18))
                                    .frame(width: 40, height: 40)
                                Image(systemName: "arrow.triangle.2.circlepath.circle.fill")
                                    .font(.title3)
                                    .foregroundStyle(theme.selectedAccent.color)
                            }
                            VStack(alignment: .leading, spacing: 2) {
                                Text("Data Sovereignty & Portability")
                                    .font(.headline)
                                Text("JSON backup, CSV export, cloud migration & restore")
                                    .font(.caption)
                                    .foregroundStyle(.secondary)
                            }
                        }
                        .padding(.vertical, 4)
                    }
                } header: {
                    Label("Data Sovereignty", systemImage: "externaldrive.badge.checkmark")
                }

                // Section 5: Diagnostics & Status
                if let err = viewModel.errorMessage {
                    Section("Status") {
                        Text(err)
                            .font(.caption)
                            .foregroundStyle(.red)
                    }
                }

                // Section 6: About & Open Source
                Section {
                    HStack {
                        Text("Application")
                        Spacer()
                        Text("FOSS Training iOS")
                            .foregroundStyle(.secondary)
                    }
                    HStack {
                        Text("Version")
                        Spacer()
                        Text("1.0.0 (Build 26)")
                            .foregroundStyle(.secondary)
                    }
                    HStack {
                        Text("License")
                        Spacer()
                        Text("GPL v3 / Open Source")
                            .foregroundStyle(.secondary)
                    }
                    HStack {
                        Text("Architecture")
                        Spacer()
                        Text("Hexagonal (Ports & Adapters)")
                            .foregroundStyle(.secondary)
                    }
                } header: {
                    Label("About", systemImage: "info.circle.fill")
                }
            }
            .navigationTitle("Settings")
            .sheet(isPresented: $isServerConfigSheetPresented) {
                ServerConfigurationSheet(viewModel: viewModel)
            }
        }
    }
}
