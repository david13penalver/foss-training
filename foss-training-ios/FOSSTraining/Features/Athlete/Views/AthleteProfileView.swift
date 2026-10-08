import SwiftUI

public struct AthleteProfileView: View {
    @Environment(\.theme) private var theme
    @State private var viewModel: AthleteProfileViewModel
    @State private var showLogWeightSheet: Bool = false
    @State private var showEditProfileSheet: Bool = false
    private let athleteRepository: AthleteRepository

    public init(
        athleteRepository: AthleteRepository,
        trainingRepository: TrainingRepository
    ) {
        self.athleteRepository = athleteRepository
        self._viewModel = State(initialValue: AthleteProfileViewModel(
            athleteRepository: athleteRepository,
            trainingRepository: trainingRepository
        ))
    }

    public var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 20) {
                    // Profile Header & Biometrics Card
                    profileHeaderCard

                    // Powerlifting Big 3 Total Card
                    bigThreeTotalCard

                    // Relative Strength Scoring Card (DOTS / Wilks)
                    RelativeStrengthCard(
                        score: viewModel.relativeStrengthScore,
                        selectedFormula: viewModel.selectedFormula,
                        onFormulaChange: { newFormula in
                            Task { await viewModel.setScoringFormula(newFormula) }
                        }
                    )

                    // 7-Day EMA Bodyweight Trend Chart
                    BodyweightTrendChart(trendPoints: viewModel.trendPoints)

                    // Quick Actions & Recent Weigh-ins Card
                    recentWeighInsCard
                }
                .padding()
            }
            .background(theme.surfaceStyle.backgroundColor)
            .navigationTitle("Athlete Profile")
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button {
                        showLogWeightSheet = true
                    } label: {
                        Label("Log Weight", systemImage: "plus")
                    }
                    .tint(theme.selectedAccent.color)
                }

                ToolbarItem(placement: .topBarLeading) {
                    Button {
                        showEditProfileSheet = true
                    } label: {
                        Image(systemName: "pencil")
                    }
                }
            }
            .sheet(isPresented: $showLogWeightSheet) {
                LogWeightSheet(
                    athleteRepository: athleteRepository,
                    lastWeight: viewModel.bodyweightHistory.last?.weightKg,
                    onSaved: {
                        Task { await viewModel.loadProfile() }
                    }
                )
            }
            .sheet(isPresented: $showEditProfileSheet) {
                EditProfileSheet(profile: viewModel.profile) { updated in
                    Task { await viewModel.saveProfile(updated) }
                }
            }
            .task {
                await viewModel.loadProfile()
            }
            .refreshable {
                await viewModel.loadProfile()
            }
        }
    }

    // MARK: - Subviews

    @ViewBuilder
    private var profileHeaderCard: some View {
        HStack(spacing: 16) {
            // Avatar Circle
            ZStack {
                Circle()
                    .fill(theme.selectedAccent.color.opacity(0.18))
                    .frame(width: 64, height: 64)

                Text(String(viewModel.profile.displayName.prefix(2)).uppercased())
                    .font(.title2.weight(.bold))
                    .foregroundStyle(theme.selectedAccent.color)
            }

            VStack(alignment: .leading, spacing: 4) {
                Text(viewModel.profile.displayName)
                    .font(.title3.weight(.bold))

                HStack(spacing: 6) {
                    Text(viewModel.profile.gender.displayName)
                        .themedBadge()

                    Text(viewModel.profile.experienceLevel.rawValue.capitalized)
                        .font(.caption2.weight(.medium))
                        .padding(.horizontal, 8)
                        .padding(.vertical, 3)
                        .background(theme.surfaceStyle.backgroundColor)
                        .clipShape(Capsule())
                }

                if let goal = viewModel.profile.targetGoal, !goal.isEmpty {
                    Text(goal)
                        .font(.caption)
                        .foregroundStyle(.secondary)
                }
            }

            Spacer()

            // Current Weight Display
            VStack(alignment: .trailing, spacing: 2) {
                Text("\(String(format: "%.1f", viewModel.currentWeightKg)) kg")
                    .font(.title3.monospacedDigit().weight(.bold))
                    .foregroundStyle(theme.selectedAccent.color)

                Text("Current")
                    .font(.caption2)
                    .foregroundStyle(.secondary)
            }
        }
        .themedCard()
    }

    @ViewBuilder
    private var bigThreeTotalCard: some View {
        VStack(alignment: .leading, spacing: 14) {
            HStack {
                VStack(alignment: .leading, spacing: 2) {
                    Text("Powerlifting Big 3 Total")
                        .font(.headline)
                    Text("Combined competitive compound total")
                        .font(.caption)
                        .foregroundStyle(.secondary)
                }
                Spacer()

                VStack(alignment: .trailing, spacing: 2) {
                    Text("\(Int(viewModel.bigThreeTotalKg)) kg")
                        .font(.title2.monospacedDigit().weight(.bold))
                        .foregroundStyle(theme.selectedAccent.color)
                    Text("\(String(format: "%.2f", viewModel.relativeRatio))× BW")
                        .font(.caption.weight(.semibold))
                        .foregroundStyle(.secondary)
                }
            }

            Divider()

            // 3-Column PR breakdown
            HStack(spacing: 12) {
                bigThreeColumn(title: "Squat", valueKg: viewModel.squatPrKg, icon: "figure.strengthtraining.traditional")
                Divider()
                bigThreeColumn(title: "Bench", valueKg: viewModel.benchPrKg, icon: "figure.cross.training")
                Divider()
                bigThreeColumn(title: "Deadlift", valueKg: viewModel.deadliftPrKg, icon: "figure.mixed.cardio")
            }
        }
        .themedCard()
    }

    private func bigThreeColumn(title: String, valueKg: Double, icon: String) -> some View {
        VStack(spacing: 4) {
            Image(systemName: icon)
                .font(.caption)
                .foregroundStyle(theme.selectedAccent.color)

            Text(title)
                .font(.caption2)
                .foregroundStyle(.secondary)

            Text("\(Int(valueKg)) kg")
                .font(.subheadline.monospacedDigit().weight(.bold))
        }
        .frame(maxWidth: .infinity)
    }

    @ViewBuilder
    private var recentWeighInsCard: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack {
                Text("Recent Weigh-ins")
                    .font(.headline)
                Spacer()
                Button {
                    showLogWeightSheet = true
                } label: {
                    Label("Log Today", systemImage: "plus")
                        .font(.caption.weight(.semibold))
                }
                .buttonStyle(.borderedProminent)
                .tint(theme.selectedAccent.color)
                .foregroundStyle(theme.selectedAccent.badgeTextColor)
            }

            if viewModel.bodyweightHistory.isEmpty {
                Text("No weigh-in logs found. Tap above to log today's morning weight.")
                    .font(.caption)
                    .foregroundStyle(.secondary)
                    .padding(.vertical, 8)
            } else {
                VStack(spacing: 8) {
                    ForEach(viewModel.bodyweightHistory.suffix(5).reversed()) { entry in
                        HStack {
                            VStack(alignment: .leading, spacing: 2) {
                                Text(entry.measuredDate, format: .dateTime.month(.abbreviated).day().year())
                                    .font(.subheadline)
                                if let notes = entry.notes, !notes.isEmpty {
                                    Text(notes)
                                        .font(.caption2)
                                        .foregroundStyle(.tertiary)
                                }
                            }
                            Spacer()
                            Text("\(String(format: "%.1f", entry.weightKg)) kg")
                                .font(.callout.monospacedDigit().weight(.semibold))

                            Button {
                                Task { await viewModel.deleteBodyweight(id: entry.id) }
                            } label: {
                                Image(systemName: "trash")
                                    .font(.caption2)
                                    .foregroundStyle(.red.opacity(0.8))
                            }
                            .buttonStyle(.plain)
                            .padding(.leading, 8)
                        }
                        if entry.id != viewModel.bodyweightHistory.suffix(5).reversed().last?.id {
                            Divider()
                        }
                    }
                }
            }
        }
        .themedCard()
    }
}

private struct EditProfileSheet: View {
    @Environment(\.dismiss) private var dismiss
    @Environment(\.theme) private var theme
    @State private var displayName: String
    @State private var gender: Gender
    @State private var heightCm: Double
    @State private var experienceLevel: ProgramLevel
    @State private var targetGoal: String
    public let onSave: (AthleteProfile) -> Void

    init(profile: AthleteProfile, onSave: @escaping (AthleteProfile) -> Void) {
        self._displayName = State(initialValue: profile.displayName)
        self._gender = State(initialValue: profile.gender)
        self._heightCm = State(initialValue: profile.heightCm ?? 175.0)
        self._experienceLevel = State(initialValue: profile.experienceLevel)
        self._targetGoal = State(initialValue: profile.targetGoal ?? "")
        self.onSave = onSave
    }

    var body: some View {
        NavigationStack {
            Form {
                Section("Profile Information") {
                    TextField("Display Name", text: $displayName)

                    Picker("Gender", selection: $gender) {
                        ForEach(Gender.allCases) { g in
                            Text(g.displayName).tag(g)
                        }
                    }

                    HStack {
                        Text("Height")
                        Spacer()
                        Stepper("\(Int(heightCm)) cm", value: $heightCm, in: 100...250)
                    }

                    Picker("Experience Level", selection: $experienceLevel) {
                        ForEach(ProgramLevel.allCases) { lvl in
                            Text(lvl.rawValue.capitalized).tag(lvl)
                        }
                    }

                    TextField("Target Goal (e.g. Strength, Hypertrophy)", text: $targetGoal)
                }
            }
            .navigationTitle("Edit Profile")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") { dismiss() }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Done") {
                        let updated = AthleteProfile(
                            id: 1,
                            displayName: displayName,
                            gender: gender,
                            dateOfBirth: nil,
                            heightCm: heightCm,
                            experienceLevel: experienceLevel,
                            targetGoal: targetGoal.isEmpty ? nil : targetGoal,
                            preferredUnit: .kg
                        )
                        onSave(updated)
                        dismiss()
                    }
                    .tint(theme.selectedAccent.color)
                    .fontWeight(.bold)
                }
            }
        }
    }
}
