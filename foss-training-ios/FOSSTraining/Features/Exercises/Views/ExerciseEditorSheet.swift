import SwiftUI

public struct ExerciseEditorSheet: View {
    @Environment(\.dismiss) private var dismiss
    @Environment(\.theme) private var theme
    @State private var viewModel: ExerciseEditorViewModel
    public var onSaved: (() -> Void)?

    public init(exerciseRepository: ExerciseRepository, exerciseToEdit: Exercise? = nil, onSaved: (() -> Void)? = nil) {
        self._viewModel = State(initialValue: ExerciseEditorViewModel(exerciseRepository: exerciseRepository, exerciseToEdit: exerciseToEdit))
        self.onSaved = onSaved
    }

    public var body: some View {
        NavigationStack {
            Form {
                if let error = viewModel.errorMessage {
                    Section {
                        Label(error, systemImage: "exclamationmark.triangle.fill")
                            .font(.subheadline)
                            .foregroundStyle(.red)
                    }
                }

                Section("Basic Information") {
                    TextField("Exercise Name (e.g. Incline Bench)", text: $viewModel.name)
                        .autocorrectionDisabled()

                    TextField("Description (Optional)", text: $viewModel.descriptionText, axis: .vertical)
                        .lineLimit(2...4)

                    Picker("Primary Category", selection: $viewModel.primaryCategory) {
                        ForEach(ExerciseCategory.allCases) { category in
                            Text(category.displayName).tag(category)
                        }
                    }

                    Picker("Difficulty Level", selection: $viewModel.difficultyLevel) {
                        ForEach(DifficultyLevel.allCases) { level in
                            Text(level.displayName).tag(level)
                        }
                    }
                }

                if viewModel.primaryCategory == .resistance {
                    Section("Resistance Details") {
                        TextField("Primary Muscle (e.g. Chest)", text: $viewModel.primaryMuscleGroup)
                        TextField("Secondary Muscles (comma separated)", text: $viewModel.secondaryMuscleGroupsText)

                        Picker("Movement Pattern", selection: $viewModel.movementPattern) {
                            Text("Select Pattern").tag(nil as MovementPattern?)
                            ForEach(MovementPattern.allCases) { pattern in
                                Text(pattern.displayName).tag(pattern as MovementPattern?)
                            }
                        }
                    }
                } else if viewModel.primaryCategory == .endurance {
                    Section("Endurance Details") {
                        TextField("Endurance Type (e.g. Running, Rowing)", text: $viewModel.enduranceType)
                    }
                } else if viewModel.primaryCategory == .mobility {
                    Section("Mobility Details") {
                        TextField("Mobility Type (e.g. Dynamic, PNF)", text: $viewModel.mobilityType)
                        TextField("Target Joints (comma separated)", text: $viewModel.targetJointsText)
                    }
                }

                Section("Equipment Required") {
                    LazyVGrid(columns: [GridItem(.adaptive(minimum: 100))], spacing: 8) {
                        ForEach(EquipmentCategory.allCases) { equipment in
                            let isSelected = viewModel.selectedEquipment.contains(equipment)
                            Button {
                                if isSelected {
                                    viewModel.selectedEquipment.remove(equipment)
                                } else {
                                    viewModel.selectedEquipment.insert(equipment)
                                }
                            } label: {
                                Text(equipment.displayName)
                                    .font(.caption.weight(isSelected ? .bold : .regular))
                                    .padding(.horizontal, 10)
                                    .padding(.vertical, 6)
                                    .frame(maxWidth: .infinity)
                                    .background(isSelected ? theme.selectedAccent.color : Color.secondary.opacity(0.12))
                                    .foregroundStyle(isSelected ? Color.black : Color.primary)
                                    .clipShape(RoundedRectangle(cornerRadius: 8))
                            }
                            .buttonStyle(.plain)
                        }
                    }
                    .padding(.vertical, 4)
                }

                Section("Instructions") {
                    ForEach(viewModel.stepByStepInstructions.indices, id: \.self) { index in
                        HStack(alignment: .top) {
                            Text("\(index + 1).")
                                .font(.caption.weight(.bold))
                                .foregroundStyle(theme.selectedAccent.color)
                            Text(viewModel.stepByStepInstructions[index])
                                .font(.subheadline)
                        }
                    }
                    .onDelete(perform: viewModel.removeInstruction)

                    HStack {
                        TextField("Add new step...", text: $viewModel.newInstructionText)
                        Button("Add") {
                            viewModel.addInstruction()
                        }
                        .disabled(viewModel.newInstructionText.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty)
                    }
                }
            }
            .navigationTitle(viewModel.existingExerciseId != nil ? "Edit Exercise" : "New Exercise")
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
                            if await viewModel.save() {
                                onSaved?()
                                dismiss()
                            }
                        }
                    }
                    .fontWeight(.bold)
                    .tint(theme.selectedAccent.color)
                    .disabled(viewModel.isSaving)
                }
            }
        }
    }
}
