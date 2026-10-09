import SwiftUI

public struct RelativeStrengthCard: View {
    @Environment(\.theme) private var theme
    public let score: RelativeStrengthScore?
    public let selectedFormula: ScoringFormula
    public let onFormulaChange: (ScoringFormula) -> Void

    public init(
        score: RelativeStrengthScore?,
        selectedFormula: ScoringFormula = .dots,
        onFormulaChange: @escaping (ScoringFormula) -> Void
    ) {
        self.score = score
        self.selectedFormula = selectedFormula
        self.onFormulaChange = onFormulaChange
    }

    private var tierColor: Color {
        guard let score else { return .secondary }
        switch score.tier {
        case .novice: return .blue
        case .intermediate: return .green
        case .advanced: return .orange
        case .elite: return .purple
        case .internationalElite: return Color(red: 1.0, green: 0.84, blue: 0.0) // Gold
        }
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            // Header with formula picker
            HStack {
                VStack(alignment: .leading, spacing: 4) {
                    Text("Relative Strength Score")
                        .font(.headline)
                    Text("Powerlifting performance normalized by bodyweight")
                        .font(.caption)
                        .foregroundStyle(.secondary)
                }
                Spacer()

                Picker("Formula", selection: Binding(
                    get: { selectedFormula },
                    set: { onFormulaChange($0) }
                )) {
                    ForEach(ScoringFormula.allCases) { f in
                        Text(f.displayName).tag(f)
                    }
                }
                .pickerStyle(.segmented)
                .frame(width: 140)
            }

            if let score {
                // Score Meter
                HStack(alignment: .firstTextBaseline) {
                    VStack(alignment: .leading, spacing: 4) {
                        HStack(alignment: .firstTextBaseline, spacing: 6) {
                            Text(String(format: "%.1f", score.score))
                                .font(.system(size: 40, weight: .bold, design: .rounded))
                                .foregroundStyle(theme.selectedAccent.color)
                            Text(selectedFormula.displayName)
                                .font(.headline)
                                .foregroundStyle(.secondary)
                        }

                        Text("Total: \(Int(score.totalKg)) kg • \(String(format: "%.2f", score.strengthToWeightRatio))× BW")
                            .font(.caption)
                            .foregroundStyle(.secondary)
                    }

                    Spacer()

                    VStack(alignment: .trailing, spacing: 4) {
                        Text(score.tier.displayName)
                            .font(.subheadline.weight(.bold))
                            .padding(.horizontal, 12)
                            .padding(.vertical, 6)
                            .background(tierColor.opacity(0.18))
                            .foregroundStyle(tierColor)
                            .clipShape(Capsule())

                        Text(score.gender.displayName)
                            .font(.caption2)
                            .foregroundStyle(.secondary)
                    }
                }

                // Description
                Text(score.tierDescription)
                    .font(.caption)
                    .foregroundStyle(.secondary)
                    .padding(.top, 4)
            } else {
                VStack(spacing: 8) {
                    ProgressView()
                    Text("Calculating relative strength...")
                        .font(.caption)
                        .foregroundStyle(.secondary)
                }
                .frame(maxWidth: .infinity, minHeight: 80)
            }
        }
        .themedCard()
    }
}
