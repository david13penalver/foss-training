import SwiftUI

public struct SessionEditorSheet: View {
    @Environment(\.dismiss) private var dismiss
    @Environment(\.theme) private var theme
    @State private var viewModel: SessionEditorViewModel
    @State private var showDiscardAlert: Bool = false

    public init(
        sessionRepository: SessionRepository,
        exerciseRepository: ExerciseRepository,
        sessionToEdit: Session? = nil
    ) {
        self._viewModel = State(initialValue: SessionEditorViewModel(
            sessionRepository: sessionRepository,
            exerciseRepository: exerciseRepository,
            sessionToEdit: sessionToEdit
        ))
    }

    public var body: some View {
        NavigationStack {
            Form {
                // Section 1: Template Metadata
                Section("Template Details") {
                    TextField("Template Name (e.g. Upper Body A)", text: $viewModel.name)
                        .font(.body)

                    TextField("Description (Optional)", text: $viewModel.descriptionText)
                        .font(.subheadline)

                    Stepper("Duration: \(viewModel.estimatedDurationMinutes) min", value: $viewModel.estimatedDurationMinutes, in: 5...240, step: 5)

                    TextField("Notes & Cues (Optional)", text: $viewModel.notes, axis: .vertical)
                        .lineLimit(2...4)
                }
                .listRowBackground(theme.surfaceStyle.cardBackgroundColor)

                // Section 2: Warm-Up Phase
                phaseSection(
                    title: "Warm-Up",
                    part: .warmUp,
                    items: viewModel.warmUpExercises
                )

                // Section 3: Main Work Phase
                phaseSection(
                    title: "Main Work",
                    part: .main,
                    items: viewModel.mainExercises
                )

                // Section 4: Cool-Down Phase
                phaseSection(
                    title: "Cool-Down",
                    part: .coolDown,
                    items: viewModel.coolDownExercises
                )

                // Error Message Display
                if let error = viewModel.errorMessage {
                    Section {
                        Text(error)
                            .font(.caption)
                            .foregroundStyle(Color.red)
                    }
                    .listRowBackground(theme.surfaceStyle.cardBackgroundColor)
                }
            }
            .scrollContentBackground(.hidden)
            .background(theme.surfaceStyle.backgroundColor)
            .navigationTitle(viewModel.isNewSession ? "New Template" : "Edit Template")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") {
                        if !viewModel.name.isEmpty || !viewModel.exercises.isEmpty {
                            showDiscardAlert = true
                        } else {
                            dismiss()
                        }
                    }
                }

                ToolbarItem(placement: .confirmationAction) {
                    Button("Save") {
                        Task {
                            let success = await viewModel.save()
                            if success {
                                dismiss()
                            }
                        }
                    }
                    .fontWeight(.semibold)
                    .disabled(!viewModel.isValid || viewModel.isSaving)
                }
            }
            .alert("Discard Changes?", isPresented: $showDiscardAlert) {
                Button("Keep Editing", role: .cancel) {}
                Button("Discard", role: .destructive) { dismiss() }
            } message: {
                Text("Any unsaved edits will be permanently lost.")
            }
            .sheet(isPresented: $viewModel.isPickerPresented) {
                ExercisePickerSheet(
                    exercises: viewModel.availableExercises,
                    part: viewModel.selectedPartForPicker
                ) { selectedExercise in
                    viewModel.addExercise(exercise: selectedExercise, to: viewModel.selectedPartForPicker)
                }
            }
            .task {
                await viewModel.loadCatalogExercises()
            }
        }
    }

    @ViewBuilder
    private func phaseSection(
        title: String,
        part: SessionPartEnum,
        items: [SessionExerciseItem]
    ) -> some View {
        Section {
            if items.isEmpty {
                HStack {
                    Text("No exercises added in \(title.lowercased()).")
                        .font(.subheadline)
                        .foregroundStyle(Color.secondary)
                    Spacer()
                }
                .padding(.vertical, 4)
            } else {
                ForEach(items) { item in
                    ExerciseItemConfigCard(
                        item: item,
                        theme: theme,
                        onAddSet: {
                            viewModel.addSet(to: item.id)
                        },
                        onRemoveSet: { setNum in
                            viewModel.removeSet(from: item.id, setNumber: setNum)
                        },
                        onUpdateSet: { setNum, weight, reps, type in
                            viewModel.updateSet(exerciseItemId: item.id, setNumber: setNum, weightKg: weight, repetitions: reps, setType: type)
                        },
                        onUpdateRest: { rest in
                            viewModel.updateRestSeconds(exerciseItemId: item.id, restSeconds: rest)
                        },
                        onDeleteExercise: {
                            viewModel.removeExercise(id: item.id)
                        }
                    )
                }
                .onMove { source, destination in
                    viewModel.moveExercises(from: source, to: destination, in: part)
                }
            }
        } header: {
            HStack {
                Text(title)
                    .font(.headline)
                    .foregroundStyle(theme.selectedAccent.color)
                Spacer()
                Button {
                    viewModel.selectedPartForPicker = part
                    viewModel.isPickerPresented = true
                } label: {
                    Label("Add", systemImage: "plus")
                        .font(.caption)
                        .fontWeight(.semibold)
                }
            }
        }
        .listRowBackground(theme.surfaceStyle.cardBackgroundColor)
    }
}

private struct ExerciseItemConfigCard: View {
    let item: SessionExerciseItem
    let theme: ThemeManager
    let onAddSet: () -> Void
    let onRemoveSet: (Int) -> Void
    let onUpdateSet: (Int, Double, Int, SetType) -> Void
    let onUpdateRest: (Int) -> Void
    let onDeleteExercise: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            // Exercise Header
            HStack {
                VStack(alignment: .leading, spacing: 2) {
                    Text(item.exerciseName)
                        .font(.headline)
                        .foregroundStyle(Color.primary)

                    HStack(spacing: 8) {
                        Menu {
                            Button("30 seconds") { onUpdateRest(30) }
                            Button("60 seconds") { onUpdateRest(60) }
                            Button("90 seconds") { onUpdateRest(90) }
                            Button("120 seconds") { onUpdateRest(120) }
                            Button("180 seconds") { onUpdateRest(180) }
                        } label: {
                            Label("\(item.restSeconds)s rest", systemImage: "timer")
                                .font(.caption2)
                                .foregroundStyle(Color.secondary)
                        }
                    }
                }

                Spacer()

                Button(role: .destructive, action: onDeleteExercise) {
                    Image(systemName: "trash")
                        .font(.caption)
                        .foregroundStyle(Color.red.opacity(0.8))
                }
                .buttonStyle(.plain)
            }

            // Sets Table Header
            HStack {
                Text("SET")
                    .frame(width: 36, alignment: .leading)
                Text("TYPE")
                    .frame(width: 80, alignment: .leading)
                Text("KG")
                    .frame(width: 60, alignment: .leading)
                Text("REPS")
                    .frame(width: 50, alignment: .leading)
                Spacer()
            }
            .font(.caption2)
            .fontWeight(.semibold)
            .foregroundStyle(Color.secondary)

            // Sets Rows
            ForEach(item.sets) { set in
                SetRowView(
                    set: set,
                    onUpdate: { w, r, t in
                        onUpdateSet(set.setNumber, w, r, t)
                    },
                    onDelete: {
                        onRemoveSet(set.setNumber)
                    }
                )
            }

            // Add Set Button
            Button(action: onAddSet) {
                HStack(spacing: 4) {
                    Image(systemName: "plus")
                    Text("Add Set")
                }
                .font(.caption)
                .fontWeight(.medium)
                .foregroundStyle(theme.selectedAccent.color)
            }
            .buttonStyle(.plain)
            .padding(.top, 4)
        }
        .padding(.vertical, 6)
    }
}

private struct SetRowView: View {
    let set: ResistanceSet
    let onUpdate: (Double, Int, SetType) -> Void
    let onDelete: () -> Void

    @State private var weight: Double
    @State private var reps: Int
    @State private var setType: SetType

    init(set: ResistanceSet, onUpdate: @escaping (Double, Int, SetType) -> Void, onDelete: @escaping () -> Void) {
        self.set = set
        self.onUpdate = onUpdate
        self.onDelete = onDelete
        self._weight = State(initialValue: set.weightKg)
        self._reps = State(initialValue: set.repetitions)
        self._setType = State(initialValue: set.setType)
    }

    var body: some View {
        HStack(spacing: 8) {
            Text("\(set.setNumber)")
                .font(.caption)
                .fontWeight(.bold)
                .frame(width: 36, alignment: .leading)

            Menu {
                ForEach(SetType.allCases) { type in
                    Button(type.displayName) {
                        setType = type
                        onUpdate(weight, reps, type)
                    }
                }
            } label: {
                Text(setType.shortTag)
                    .font(.caption2)
                    .fontWeight(.medium)
                    .padding(.horizontal, 6)
                    .padding(.vertical, 2)
                    .background(Color.secondary.opacity(0.2))
                    .clipShape(RoundedRectangle(cornerRadius: 4))
            }
            .frame(width: 80, alignment: .leading)

            TextField("0", value: $weight, format: .number)
                .keyboardType(.decimalPad)
                .textFieldStyle(.roundedBorder)
                .font(.caption)
                .frame(width: 60)
                .onChange(of: weight) { _, newW in
                    onUpdate(newW, reps, setType)
                }

            TextField("0", value: $reps, format: .number)
                .keyboardType(.numberPad)
                .textFieldStyle(.roundedBorder)
                .font(.caption)
                .frame(width: 50)
                .onChange(of: reps) { _, newR in
                    onUpdate(weight, newR, setType)
                }

            Spacer()

            Button(role: .destructive, action: onDelete) {
                Image(systemName: "minus.circle")
                    .font(.caption)
                    .foregroundStyle(Color.red.opacity(0.7))
            }
            .buttonStyle(.plain)
        }
    }
}
