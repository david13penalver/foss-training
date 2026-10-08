import SwiftUI

public struct GenerateScheduleSheet: View {
    @Environment(\.dismiss) private var dismiss
    @Environment(\.theme) private var theme

    public let program: TrainingProgram
    public let onGenerate: (Date) -> Void

    @State private var selectedStartDate: Date = Date()

    public init(program: TrainingProgram, onGenerate: @escaping (Date) -> Void) {
        self.program = program
        self.onGenerate = onGenerate
    }

    private var previewWorkouts: [Training] {
        program.generateSchedule(startDate: selectedStartDate)
    }

    private var dateFormatter: DateFormatter {
        let df = DateFormatter()
        df.dateStyle = .medium
        df.timeStyle = .none
        return df
    }

    public var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                // Settings Section
                VStack(alignment: .leading, spacing: 12) {
                    Text("Select Start Date")
                        .font(.headline)
                        .foregroundStyle(.primary)

                    DatePicker(
                        "Start Date",
                        selection: $selectedStartDate,
                        displayedComponents: [.date]
                    )
                    .datePickerStyle(.graphical)
                    .tint(theme.selectedAccent.color)
                    .padding(8)
                    .background(Color.white.opacity(0.04))
                    .clipShape(RoundedRectangle(cornerRadius: 12))
                }
                .padding()

                // Preview Section
                VStack(alignment: .leading, spacing: 8) {
                    HStack {
                        Text("Scheduled Workouts Preview")
                            .font(.subheadline.weight(.semibold))
                            .foregroundStyle(.secondary)
                        Spacer()
                        Text("\(previewWorkouts.count) sessions")
                            .font(.caption)
                            .themedBadge()
                    }
                    .padding(.horizontal)

                    List {
                        ForEach(Array(previewWorkouts.prefix(10).enumerated()), id: \.offset) { _, workout in
                            HStack {
                                VStack(alignment: .leading, spacing: 2) {
                                    Text(workout.name)
                                        .font(.subheadline.weight(.medium))
                                    if let focus = workout.description, !focus.isEmpty {
                                        Text(focus)
                                            .font(.caption)
                                            .foregroundStyle(.secondary)
                                    }
                                }
                                Spacer()
                                Text(dateFormatter.string(from: workout.trainingDate))
                                    .font(.caption)
                                    .foregroundStyle(.secondary)
                            }
                        }

                        if previewWorkouts.count > 10 {
                            Text("... and \(previewWorkouts.count - 10) more workouts")
                                .font(.caption)
                                .foregroundStyle(.secondary)
                        }
                    }
                    .listStyle(.plain)
                }

                Spacer()

                // Action Button
                Button {
                    onGenerate(selectedStartDate)
                    dismiss()
                } label: {
                    Text("Generate \(previewWorkouts.count) Workouts")
                        .font(.headline)
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(theme.selectedAccent.color)
                        .foregroundStyle(.white)
                        .clipShape(RoundedRectangle(cornerRadius: 14))
                }
                .padding()
            }
            .navigationTitle("Generate Schedule")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") {
                        dismiss()
                    }
                }
            }
        }
    }
}
