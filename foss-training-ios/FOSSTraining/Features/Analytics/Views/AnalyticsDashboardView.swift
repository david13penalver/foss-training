import SwiftUI
import Charts

public struct AnalyticsDashboardView: View {
    @Environment(\.theme) private var theme
    @State private var dashboardViewModel: AnalyticsDashboardViewModel
    @State private var legacyViewModel: AnalyticsViewModel
    private let analyticsRepository: AnalyticsRepository

    public init(
        trainingRepository: TrainingRepository,
        athleteRepository: AthleteRepository,
        analyticsRepository: AnalyticsRepository,
        exerciseRepository: ExerciseRepository
    ) {
        self.analyticsRepository = analyticsRepository
        self._dashboardViewModel = State(initialValue: AnalyticsDashboardViewModel(
            analyticsRepository: analyticsRepository,
            exerciseRepository: exerciseRepository
        ))
        self._legacyViewModel = State(initialValue: AnalyticsViewModel(
            trainingRepository: trainingRepository,
            athleteRepository: athleteRepository
        ))
    }

    public var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                // Tab Selection Bar
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 8) {
                        ForEach(AnalyticsTab.allCases) { tab in
                            Button {
                                dashboardViewModel.selectedTab = tab
                            } label: {
                                Text(tab.displayName)
                                    .font(.subheadline.weight(dashboardViewModel.selectedTab == tab ? .bold : .medium))
                                    .padding(.horizontal, 14)
                                    .padding(.vertical, 8)
                                    .background(
                                        dashboardViewModel.selectedTab == tab
                                            ? theme.selectedAccent.color
                                            : theme.surfaceStyle.backgroundColor
                                    )
                                    .foregroundStyle(
                                        dashboardViewModel.selectedTab == tab
                                            ? theme.selectedAccent.badgeTextColor
                                            : .primary
                                    )
                                    .clipShape(Capsule())
                            }
                            .buttonStyle(.plain)
                        }
                    }
                    .padding(.horizontal)
                    .padding(.vertical, 10)
                }
                .background(theme.surfaceStyle.backgroundColor.opacity(0.5))

                // Tab Content
                ScrollView {
                    VStack(spacing: 20) {
                        if dashboardViewModel.isLoading {
                            ProgressView("Loading sports analytics...")
                                .padding(.vertical, 40)
                        } else {
                            switch dashboardViewModel.selectedTab {
                            case .overview:
                                overviewSection
                            case .acwr:
                                acwrSection
                            case .hypertrophy:
                                hypertrophySection
                            case .progression:
                                progressionSection
                            case .records:
                                recordsSection
                            case .oneRepMax:
                                OneRepMaxCalculatorView(analyticsRepository: analyticsRepository)
                            }
                        }
                    }
                    .padding()
                }
            }
            .background(theme.surfaceStyle.backgroundColor)
            .navigationTitle("Analytics")
            .task {
                await dashboardViewModel.loadDashboard()
                await legacyViewModel.loadAnalytics()
            }
            .refreshable {
                await dashboardViewModel.loadDashboard()
                await legacyViewModel.loadAnalytics()
            }
        }
    }

    // MARK: - Sections

    @ViewBuilder
    private var overviewSection: some View {
        if let acwr = dashboardViewModel.workloadRatio {
            AcwrGaugeCard(workloadRatio: acwr)
        }

        if let volume = dashboardViewModel.weeklyMuscleVolume {
            HypertrophyVolumeChart(weeklyVolume: volume)
        }

        if !dashboardViewModel.personalRecords.isEmpty {
            PersonalRecordsCard(records: Array(dashboardViewModel.personalRecords.prefix(5)))
        }

        OneRepMaxCalculatorCard()
        RelativeStrengthScoreCard()
        bodyweightTrackingCard
    }

    @ViewBuilder
    private var acwrSection: some View {
        if let acwr = dashboardViewModel.workloadRatio {
            AcwrGaugeCard(workloadRatio: acwr)

            if !acwr.dailyWorkloads.isEmpty {
                VStack(alignment: .leading, spacing: 12) {
                    Text("Daily Workload Breakdown (Last 28 Days)")
                        .font(.headline)

                    ForEach(acwr.dailyWorkloads.suffix(7).reversed()) { daily in
                        HStack {
                            Text(daily.date, format: .dateTime.month(.abbreviated).day())
                                .font(.subheadline)
                                .foregroundStyle(.secondary)
                            Spacer()
                            Text("\(daily.completedSessions) session(s)")
                                .font(.caption)
                                .foregroundStyle(.tertiary)
                            Text("\(Int(daily.workloadAu)) AU")
                                .font(.subheadline.monospacedDigit().weight(.semibold))
                                .foregroundStyle(theme.selectedAccent.color)
                        }
                        Divider()
                    }
                }
                .themedCard()
            }
        } else {
            emptySectionPlaceholder(title: "No ACWR Data", message: "Complete sessions to monitor your acute to chronic workload balance.")
        }
    }

    @ViewBuilder
    private var hypertrophySection: some View {
        if let volume = dashboardViewModel.weeklyMuscleVolume {
            HypertrophyVolumeChart(weeklyVolume: volume)

            if !volume.recommendations.isEmpty {
                VStack(alignment: .leading, spacing: 10) {
                    Text("Hypertrophy Recommendations")
                        .font(.headline)

                    ForEach(volume.recommendations, id: \.self) { rec in
                        HStack(alignment: .top, spacing: 8) {
                            Image(systemName: "lightbulb.fill")
                                .foregroundStyle(.yellow)
                                .font(.caption)
                                .padding(.top, 2)
                            Text(rec)
                                .font(.caption)
                                .foregroundStyle(.secondary)
                        }
                    }
                }
                .themedCard()
            }
        } else {
            emptySectionPlaceholder(title: "No Volume Data", message: "Log workout exercises to analyze hypertrophy set distribution.")
        }
    }

    @ViewBuilder
    private var progressionSection: some View {
        VStack(alignment: .leading, spacing: 14) {
            // Exercise picker & Timeframe controls
            HStack {
                Menu {
                    ForEach(dashboardViewModel.exercises) { ex in
                        Button(ex.name) {
                            Task {
                                await dashboardViewModel.selectExerciseForProgression(ex)
                            }
                        }
                    }
                } label: {
                    HStack {
                        Text(dashboardViewModel.selectedExerciseForProgression?.name ?? "Select Exercise")
                            .font(.headline)
                        Image(systemName: "chevron.down")
                            .font(.caption)
                    }
                    .padding(.horizontal, 12)
                    .padding(.vertical, 6)
                    .background(theme.surfaceStyle.backgroundColor)
                    .clipShape(Capsule())
                }

                Spacer()

                HStack(spacing: 4) {
                    timeframeButton(months: 1, label: "1M")
                    timeframeButton(months: 3, label: "3M")
                    timeframeButton(months: 6, label: "6M")
                    timeframeButton(months: 12, label: "1Y")
                }
            }

            if let progression = dashboardViewModel.exerciseProgression {
                ExerciseProgressionChart(progression: progression)
            } else {
                emptySectionPlaceholder(title: "No Progression Found", message: "Complete multiple workouts containing this exercise to track progression.")
            }
        }
    }

    @ViewBuilder
    private func timeframeButton(months: Int, label: String) -> some View {
        Button {
            Task {
                await dashboardViewModel.setProgressionMonths(months)
            }
        } label: {
            Text(label)
                .font(.caption.weight(dashboardViewModel.progressionMonths == months ? .bold : .medium))
                .padding(.horizontal, 8)
                .padding(.vertical, 4)
                .background(dashboardViewModel.progressionMonths == months ? theme.selectedAccent.color : Color.clear)
                .foregroundStyle(dashboardViewModel.progressionMonths == months ? theme.selectedAccent.badgeTextColor : .secondary)
                .clipShape(Capsule())
        }
        .buttonStyle(.plain)
    }

    @ViewBuilder
    private var recordsSection: some View {
        PersonalRecordsCard(records: dashboardViewModel.personalRecords)
    }

    @ViewBuilder
    private var bodyweightTrackingCard: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Athlete Bodyweight")
                .font(.headline)

            HStack {
                TextField("e.g. 78.5", text: $legacyViewModel.newBodyweightString)
                    .keyboardType(.decimalPad)
                    .textFieldStyle(.roundedBorder)

                Button("Log kg") {
                    Task { await legacyViewModel.logCurrentBodyweight() }
                }
                .buttonStyle(.borderedProminent)
                .tint(theme.selectedAccent.color)
                .foregroundStyle(theme.selectedAccent.badgeTextColor)
            }

            if !legacyViewModel.bodyweightHistory.isEmpty {
                VStack(alignment: .leading, spacing: 6) {
                    ForEach(legacyViewModel.bodyweightHistory.suffix(5).reversed()) { entry in
                        HStack {
                            Text(entry.measuredDate, style: .date)
                                .font(.caption)
                                .foregroundStyle(.secondary)
                            Spacer()
                            Text("\(String(format: "%.1f", entry.weightKg)) kg")
                                .font(.subheadline.monospacedDigit().weight(.semibold))
                        }
                    }
                }
                .padding(.top, 4)
            }
        }
        .themedCard()
    }

    private func emptySectionPlaceholder(title: String, message: String) -> some View {
        VStack(spacing: 8) {
            Image(systemName: "chart.xyaxis.line")
                .font(.largeTitle)
                .foregroundStyle(.secondary)
            Text(title)
                .font(.headline)
            Text(message)
                .font(.subheadline)
                .foregroundStyle(.secondary)
                .multilineTextAlignment(.center)
        }
        .frame(maxWidth: .infinity, minHeight: 180)
        .themedCard()
    }
}

// MARK: - Legacy Embedded Cards

private struct OneRepMaxCalculatorCard: View {
    @Environment(\.theme) private var theme
    @State private var weightKg: Double = 100.0
    @State private var reps: Int = 5
    @State private var formula: OneRepMaxFormula = .epley

    private var estimated1RM: Double {
        formula.calculate(weightKg: weightKg, repetitions: reps)
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 14) {
            HStack {
                Text("Quick 1RM Estimator")
                    .font(.headline)
                Spacer()
                Text("\(String(format: "%.1f", estimated1RM)) kg")
                    .font(.title3.monospacedDigit().weight(.bold))
                    .foregroundStyle(theme.selectedAccent.color)
            }

            Picker("Formula", selection: $formula) {
                ForEach(OneRepMaxFormula.allCases) { f in
                    Text(f.displayName).tag(f)
                }
            }
            .pickerStyle(.segmented)

            HStack {
                Text("Lifted Weight:")
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
                Spacer()
                Stepper("\(Int(weightKg)) kg", value: $weightKg, in: 10...500, step: 2.5)
            }

            HStack {
                Text("Repetitions:")
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
                Spacer()
                Stepper("\(reps) reps", value: $reps, in: 1...30)
            }
        }
        .themedCard()
    }
}

private struct RelativeStrengthScoreCard: View {
    @Environment(\.theme) private var theme
    @State private var totalKg: Double = 450.0
    @State private var bwKg: Double = 80.0
    @State private var gender: AthleteGender = .male

    private var score: RelativeStrengthScore {
        RelativeStrengthCalculator.calculate(totalWeightKg: totalKg, bodyweightKg: bwKg, gender: gender)
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 14) {
            HStack {
                Text("Relative Strength")
                    .font(.headline)
                Spacer()
                Text(score.classification)
                    .themedBadge()
            }

            Picker("Gender", selection: $gender) {
                ForEach(AthleteGender.allCases) { g in
                    Text(g.displayName).tag(g)
                }
            }
            .pickerStyle(.segmented)

            HStack {
                VStack(alignment: .leading) {
                    Text("Total (S+B+D)")
                        .font(.caption)
                        .foregroundStyle(.secondary)
                    Stepper("\(Int(totalKg)) kg", value: $totalKg, in: 50...1200, step: 5)
                }
            }

            HStack {
                VStack(alignment: .leading) {
                    Text("Bodyweight")
                        .font(.caption)
                        .foregroundStyle(.secondary)
                    Stepper("\(String(format: "%.1f", bwKg)) kg", value: $bwKg, in: 40...200, step: 0.5)
                }
            }

            Divider()

            HStack {
                VStack(alignment: .leading) {
                    Text("DOTS Score")
                        .font(.caption)
                        .foregroundStyle(.secondary)
                    Text(String(format: "%.1f", score.dotsScore))
                        .font(.headline.monospacedDigit())
                        .foregroundStyle(theme.selectedAccent.color)
                }
                Spacer()
                VStack(alignment: .trailing) {
                    Text("Wilks Score")
                        .font(.caption)
                        .foregroundStyle(.secondary)
                    Text(String(format: "%.1f", score.wilksScore))
                        .font(.headline.monospacedDigit())
                        .foregroundStyle(theme.selectedAccent.color)
                }
            }
        }
        .themedCard()
    }
}

