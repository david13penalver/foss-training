import SwiftUI

public struct LogWeightSheet: View {
    @Environment(\.dismiss) private var dismiss
    @Environment(\.theme) private var theme
    @State private var viewModel: BodyweightLogViewModel
    public let onSaved: () -> Void

    public init(
        athleteRepository: AthleteRepository,
        initialWeight: Double? = nil,
        lastWeight: Double? = nil,
        onSaved: @escaping () -> Void
    ) {
        self._viewModel = State(initialValue: BodyweightLogViewModel(
            athleteRepository: athleteRepository,
            initialWeight: initialWeight,
            lastWeight: lastWeight
        ))
        self.onSaved = onSaved
    }

    public var body: some View {
        NavigationStack {
            Form {
                Section("Morning Weight") {
                    HStack {
                        TextField("Weight in kg", text: Binding(
                            get: { viewModel.weightString },
                            set: { viewModel.updateWeightFromString($0) }
                        ))
                        .keyboardType(.decimalPad)
                        .font(.system(size: 32, weight: .bold, design: .rounded))
                        .foregroundStyle(theme.selectedAccent.color)

                        Text("kg")
                            .font(.title2.weight(.medium))
                            .foregroundStyle(.secondary)
                    }
                    .padding(.vertical, 8)

                    // Quick adjustments
                    HStack(spacing: 10) {
                        Button("-0.2 kg") {
                            viewModel.adjustWeight(delta: -0.2)
                        }
                        .buttonStyle(.bordered)

                        if viewModel.lastLoggedWeight != nil {
                            Button("Same as yesterday") {
                                viewModel.setSameAsYesterday()
                            }
                            .buttonStyle(.bordered)
                        }

                        Button("+0.2 kg") {
                            viewModel.adjustWeight(delta: 0.2)
                        }
                        .buttonStyle(.bordered)
                    }
                    .font(.caption.weight(.semibold))
                }

                Section("Details") {
                    DatePicker("Measurement Date", selection: $viewModel.entryDate, displayedComponents: [.date])

                    TextField("Notes (e.g., Post-refeed, Dehydrated)", text: $viewModel.notes)
                }

                if let error = viewModel.errorMessage {
                    Section {
                        Text(error)
                            .font(.caption)
                            .foregroundStyle(.red)
                    }
                }
            }
            .navigationTitle("Log Weight")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") {
                        dismiss()
                    }
                }

                ToolbarItem(placement: .confirmationAction) {
                    Button("Save") {
                        Task {
                            let success = await viewModel.save()
                            if success {
                                onSaved()
                                dismiss()
                            }
                        }
                    }
                    .fontWeight(.bold)
                    .tint(theme.selectedAccent.color)
                    .disabled(viewModel.isLoading)
                }
            }
        }
    }
}
