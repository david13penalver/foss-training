import SwiftUI
import Charts

public struct AnalyticsDashboardView: View {
    @Environment(\.theme) private var theme
    @State private var viewModel: AnalyticsViewModel

    public init(trainingRepository: TrainingRepository, athleteRepository: AthleteRepository) {
        self._viewModel = State(initialValue: AnalyticsViewModel(
            trainingRepository: trainingRepository,
            athleteRepository: athleteRepository
        ))
    }

    public var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 20) {
                    // ACWR Workload Ratio Card
                    VStack(alignment: .leading, spacing: 12) {
                        HStack {
                            Text("Workload Ratio (ACWR)")
                                .font(.headline)
                            Spacer()
                            Text("Optimal Zone")
                                .themedBadge()
                        }

                        Gauge(value: viewModel.currentAcwrRatio, in: 0.5...2.0) {
                            Text("ACWR")
                        } currentValueLabel: {
                            Text(String(format: "%.2f", viewModel.currentAcwrRatio))
                                .font(.title3.monospacedDigit().weight(.bold))
                        }
                        .gaugeStyle(.accessoryLinearCapacity)
                        .tint(Gradient(colors: [.green, theme.selectedAccent.color, .orange, .red]))

                        Text("Acute (7-day) vs Chronic (28-day) workload ratio. Range between 0.8 and 1.3 optimizes fitness and minimizes injury risk.")
                            .font(.caption)
                            .foregroundStyle(.secondary)
                    }
                    .themedCard()

                    // Muscle Group Volume Chart
                    VStack(alignment: .leading, spacing: 12) {
                        Text("Volume Load by Muscle Group")
                            .font(.headline)

                        if viewModel.muscleVolumes.allSatisfy({ $0.volumeKg == 0 }) {
                            Text("Complete workouts to see your volume distribution across muscle groups.")
                                .font(.subheadline)
                                .foregroundStyle(.secondary)
                                .padding(.vertical, 20)
                        } else {
                            Chart(viewModel.muscleVolumes) { item in
                                BarMark(
                                    x: .value("Volume (kg)", item.volumeKg),
                                    y: .value("Muscle", item.muscle)
                                )
                                .foregroundStyle(theme.selectedAccent.color)
                                .cornerRadius(4)
                            }
                            .frame(height: 200)
                        }
                    }
                    .themedCard()

                    // Bodyweight Tracking Card
                    VStack(alignment: .leading, spacing: 12) {
                        Text("Athlete Bodyweight")
                            .font(.headline)

                        HStack {
                            TextField("e.g. 78.5", text: $viewModel.newBodyweightString)
                                .keyboardType(.decimalPad)
                                .textFieldStyle(.roundedBorder)

                            Button("Log kg") {
                                Task { await viewModel.logCurrentBodyweight() }
                            }
                            .buttonStyle(.borderedProminent)
                            .tint(theme.selectedAccent.color)
                            .foregroundStyle(theme.selectedAccent.badgeTextColor)
                        }

                        if !viewModel.bodyweightHistory.isEmpty {
                            VStack(alignment: .leading, spacing: 6) {
                                ForEach(viewModel.bodyweightHistory.suffix(5).reversed()) { entry in
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

                    // 1RM Estimator Card (Sports Science)
                    OneRepMaxCalculatorCard()

                    // Relative Strength Scoring Card (Powerlifting Wilks/DOTS)
                    RelativeStrengthScoreCard()
                }
                .padding()
            }
            .background(theme.surfaceStyle.backgroundColor)
            .navigationTitle("Analytics")
            .task {
                await viewModel.loadAnalytics()
            }
        }
    }
}

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
                Text("1RM Estimator")
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
