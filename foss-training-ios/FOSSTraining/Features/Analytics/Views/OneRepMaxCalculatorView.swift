import SwiftUI

public struct OneRepMaxCalculatorView: View {
    @Environment(\.theme) private var theme
    @State private var viewModel: OneRepMaxCalculatorViewModel

    public init(analyticsRepository: AnalyticsRepository) {
        self._viewModel = State(initialValue: OneRepMaxCalculatorViewModel(analyticsRepository: analyticsRepository))
    }

    public init(viewModel: OneRepMaxCalculatorViewModel) {
        self._viewModel = State(initialValue: viewModel)
    }

    private func repEquivalent(for percentage: Int) -> String {
        switch percentage {
        case 95: return "~2 reps"
        case 90: return "~4 reps"
        case 85: return "~6 reps"
        case 80: return "~8 reps"
        case 75: return "~10 reps"
        case 70: return "~12 reps"
        case 65: return "~15 reps"
        case 60: return "~20 reps"
        case 55: return "~25 reps"
        case 50: return "~30 reps"
        default: return ""
        }
    }

    public var body: some View {
        ScrollView {
            VStack(spacing: 20) {
                // Main Calculation Card
                VStack(alignment: .leading, spacing: 16) {
                    HStack {
                        VStack(alignment: .leading, spacing: 4) {
                            Text("1-Rep Max Projection")
                                .font(.headline)
                            Text("Theoretical maximum single lift")
                                .font(.caption)
                                .foregroundStyle(.secondary)
                        }
                        Spacer()
                        VStack(alignment: .trailing, spacing: 2) {
                            Text("\(String(format: "%.1f", viewModel.currentEstimated1Rm)) kg")
                                .font(.system(size: 32, weight: .bold, design: .rounded))
                                .foregroundStyle(theme.selectedAccent.color)
                            Text(viewModel.selectedFormula.displayName)
                                .font(.caption2.weight(.semibold))
                                .foregroundStyle(.secondary)
                        }
                    }

                    Divider()

                    // Formula Picker
                    VStack(alignment: .leading, spacing: 6) {
                        Text("Calculation Formula")
                            .font(.caption)
                            .foregroundStyle(.secondary)

                        Picker("Formula", selection: Binding(
                            get: { viewModel.selectedFormula },
                            set: { viewModel.setFormula($0) }
                        )) {
                            ForEach(OneRepMaxFormula.allCases) { formula in
                                Text(formula.displayName).tag(formula)
                            }
                        }
                        .pickerStyle(.segmented)
                    }

                    // Weight Input
                    HStack {
                        VStack(alignment: .leading, spacing: 2) {
                            Text("Weight Lifted")
                                .font(.subheadline.weight(.medium))
                            Text("Actual training load")
                                .font(.caption2)
                                .foregroundStyle(.secondary)
                        }
                        Spacer()
                        HStack(spacing: 8) {
                            Button {
                                viewModel.setWeight(viewModel.weightKg - 2.5)
                            } label: {
                                Image(systemName: "minus.circle.fill")
                                    .font(.title3)
                                    .foregroundStyle(theme.selectedAccent.color)
                            }
                            .buttonStyle(.plain)

                            Text("\(String(format: "%.1f", viewModel.weightKg)) kg")
                                .font(.headline.monospacedDigit())
                                .frame(minWidth: 70, alignment: .center)

                            Button {
                                viewModel.setWeight(viewModel.weightKg + 2.5)
                            } label: {
                                Image(systemName: "plus.circle.fill")
                                    .font(.title3)
                                    .foregroundStyle(theme.selectedAccent.color)
                            }
                            .buttonStyle(.plain)
                        }
                    }

                    // Repetitions Input
                    HStack {
                        VStack(alignment: .leading, spacing: 2) {
                            Text("Repetitions Completed")
                                .font(.subheadline.weight(.medium))
                            Text("Valid range 1–36 reps")
                                .font(.caption2)
                                .foregroundStyle(.secondary)
                        }
                        Spacer()
                        HStack(spacing: 8) {
                            Button {
                                viewModel.setReps(viewModel.repetitions - 1)
                            } label: {
                                Image(systemName: "minus.circle.fill")
                                    .font(.title3)
                                    .foregroundStyle(theme.selectedAccent.color)
                            }
                            .buttonStyle(.plain)

                            Text("\(viewModel.repetitions) reps")
                                .font(.headline.monospacedDigit())
                                .frame(minWidth: 70, alignment: .center)

                            Button {
                                viewModel.setReps(viewModel.repetitions + 1)
                            } label: {
                                Image(systemName: "plus.circle.fill")
                                    .font(.title3)
                                    .foregroundStyle(theme.selectedAccent.color)
                            }
                            .buttonStyle(.plain)
                        }
                    }
                }
                .themedCard()

                // Training Percentages Table Card
                VStack(alignment: .leading, spacing: 14) {
                    HStack {
                        VStack(alignment: .leading, spacing: 4) {
                            Text("Training Zones & Percentages")
                                .font(.headline)
                            Text("Prescribed loads derived from \(viewModel.selectedFormula.displayName) 1RM")
                                .font(.caption)
                                .foregroundStyle(.secondary)
                        }
                        Spacer()
                    }

                    VStack(spacing: 8) {
                        ForEach(viewModel.percentages) { pct in
                            HStack {
                                Text("\(pct.percentage)%")
                                    .font(.subheadline.monospacedDigit().weight(.bold))
                                    .frame(width: 50, alignment: .leading)

                                Text(repEquivalent(for: pct.percentage))
                                    .font(.caption)
                                    .foregroundStyle(.secondary)

                                Spacer()

                                Text("\(String(format: "%.1f", pct.weightKg)) kg")
                                    .font(.callout.monospacedDigit().weight(.semibold))
                                    .foregroundStyle(theme.selectedAccent.color)
                            }
                            .padding(.vertical, 4)
                            .padding(.horizontal, 8)
                            .background(pct.percentage >= 85 ? theme.selectedAccent.color.opacity(0.08) : Color.clear)
                            .clipShape(RoundedRectangle(cornerRadius: 6))

                            if pct.percentage != viewModel.percentages.last?.percentage {
                                Divider()
                            }
                        }
                    }
                }
                .themedCard()

                // Scientific Formulas Comparison Card
                VStack(alignment: .leading, spacing: 14) {
                    HStack {
                        Text("Formula Divergence Comparison")
                            .font(.headline)
                        Spacer()
                    }

                    VStack(spacing: 6) {
                        ForEach(viewModel.estimates) { estimate in
                            HStack {
                                Text(estimate.formula.displayName)
                                    .font(.subheadline)
                                    .foregroundStyle(estimate.formula == viewModel.selectedFormula ? theme.selectedAccent.color : .primary)
                                    .fontWeight(estimate.formula == viewModel.selectedFormula ? .bold : .regular)

                                Spacer()

                                Text("\(String(format: "%.2f", estimate.estimatedOneRepMax)) kg")
                                    .font(.subheadline.monospacedDigit())
                                    .foregroundStyle(estimate.formula == viewModel.selectedFormula ? theme.selectedAccent.color : .secondary)
                                    .fontWeight(estimate.formula == viewModel.selectedFormula ? .bold : .regular)
                            }
                            .padding(.vertical, 4)
                        }
                    }
                }
                .themedCard()
            }
            .padding()
        }
        .background(theme.surfaceStyle.backgroundColor)
        .navigationTitle("1RM Calculator")
    }
}
