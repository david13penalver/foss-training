import SwiftUI

public struct RestTimerBannerView: View {
    @Environment(\.theme) private var theme
    @Bindable var viewModel: ActiveWorkoutViewModel

    public init(viewModel: ActiveWorkoutViewModel) {
        self.viewModel = viewModel
    }

    public var body: some View {
        HStack(spacing: 12) {
            Image(systemName: "timer")
                .font(.headline)

            VStack(alignment: .leading, spacing: 2) {
                Text("REST TIMER")
                    .font(.caption2.weight(.bold))
                    .opacity(0.8)
                Text(viewModel.formattedRestTimer)
                    .font(.title3.monospacedDigit().weight(.bold))
            }

            Spacer()

            Button("-15s") {
                viewModel.adjustRestTimer(by: -15)
            }
            .font(.caption.weight(.bold))
            .padding(.horizontal, 10)
            .padding(.vertical, 6)
            .background(Color.white.opacity(0.15))
            .clipShape(Capsule())

            Button("+30s") {
                viewModel.adjustRestTimer(by: 30)
            }
            .font(.caption.weight(.bold))
            .padding(.horizontal, 10)
            .padding(.vertical, 6)
            .background(Color.white.opacity(0.15))
            .clipShape(Capsule())

            Button("Skip") {
                viewModel.skipRestTimer()
            }
            .font(.caption.weight(.bold))
            .padding(.horizontal, 12)
            .padding(.vertical, 6)
            .background(theme.selectedAccent.color)
            .foregroundStyle(theme.selectedAccent.badgeTextColor)
            .clipShape(Capsule())
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 10)
        .background(
            RoundedRectangle(cornerRadius: 16)
                .fill(Color.orange.opacity(0.25))
                .overlay(
                    RoundedRectangle(cornerRadius: 16)
                        .stroke(Color.orange.opacity(0.5), lineWidth: 1)
                )
        )
        .foregroundStyle(.orange)
    }
}
