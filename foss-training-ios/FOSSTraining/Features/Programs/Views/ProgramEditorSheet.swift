import SwiftUI

public struct ProgramEditorSheet: View {
    @Environment(\.dismiss) private var dismiss
    @Environment(\.theme) private var theme

    @State private var viewModel: ProgramEditorViewModel

    // State for adding a workout day
    @State private var showingAddWorkoutSection: Bool = false
    @State private var newWorkoutDayOfWeek: Int = 1
    @State private var selectedSessionId: Int? = nil
    @State private var newWorkoutFocus: String = ""

    public init(
        programRepository: TrainingProgramRepository,
        sessionRepository: SessionRepository,
        programToEdit: TrainingProgram? = nil
    ) {
        self._viewModel = State(initialValue: ProgramEditorViewModel(
            programRepository: programRepository,
            sessionRepository: sessionRepository,
            programToEdit: programToEdit
        ))
    }

    private let dayNames = [
        (1, "Monday"),
        (2, "Tuesday"),
        (3, "Wednesday"),
        (4, "Thursday"),
        (5, "Friday"),
        (6, "Saturday"),
        (7, "Sunday")
    ]

    public var body: some View {
        NavigationStack {
            Form {
                Section("Program Information") {
                    TextField("Program Name", text: $viewModel.name)

                    TextField("Description (optional)", text: $viewModel.descriptionText, axis: .vertical)
                        .lineLimit(2...4)

                    Stepper("Duration: \(viewModel.durationWeeks) weeks", value: $viewModel.durationWeeks, in: 1...52)

                    Picker("Periodization", selection: $viewModel.periodizationType) {
                        ForEach(PeriodizationType.allCases, id: \.self) { type in
                            Text(type.displayName).tag(type)
                        }
                    }

                    Picker("Experience Level", selection: $viewModel.level) {
                        ForEach(ProgramLevel.allCases, id: \.self) { lvl in
                            Text(lvl.displayName).tag(lvl)
                        }
                    }

                    Toggle("Active Program", isOn: $viewModel.isActive)
                }

                Section("Microcycle Schedule (\(viewModel.workouts.count) days/week)") {
                    if viewModel.workouts.isEmpty {
                        Text("No workout days added yet. Configure at least one workout day.")
                            .font(.caption)
                            .foregroundStyle(.secondary)
                    } else {
                        ForEach(viewModel.workouts) { workout in
                            HStack {
                                VStack(alignment: .leading, spacing: 2) {
                                    Text(workout.dayName)
                                        .font(.subheadline.weight(.semibold))
                                    Text(workout.session.name)
                                        .font(.caption)
                                        .foregroundStyle(.secondary)
                                    if let focus = workout.focus, !focus.isEmpty {
                                        Text(focus)
                                            .font(.caption2)
                                            .foregroundStyle(theme.selectedAccent.color)
                                    }
                                }
                                Spacer()
                            }
                        }
                        .onDelete { indexSet in
                            for idx in indexSet {
                                viewModel.removeWorkout(id: viewModel.workouts[idx].id)
                            }
                        }
                    }

                    if showingAddWorkoutSection {
                        VStack(alignment: .leading, spacing: 10) {
                            Picker("Day of Week", selection: $newWorkoutDayOfWeek) {
                                ForEach(dayNames, id: \.0) { day in
                                    Text(day.1).tag(day.0)
                                }
                            }

                            if viewModel.availableSessions.isEmpty {
                                Text("No session templates found. Create sessions first.")
                                    .font(.caption)
                                    .foregroundStyle(.orange)
                            } else {
                                Picker("Session Template", selection: Binding(
                                    get: { selectedSessionId ?? viewModel.availableSessions.first?.id ?? 0 },
                                    set: { selectedSessionId = $0 }
                                )) {
                                    ForEach(viewModel.availableSessions) { ses in
                                        Text(ses.name).tag(ses.id)
                                    }
                                }
                            }

                            TextField("Focus (e.g., Heavy Bench, Hypertrophy)", text: $newWorkoutFocus)
                                .textFieldStyle(.roundedBorder)

                            HStack {
                                Button("Add to Schedule") {
                                    let sId = selectedSessionId ?? viewModel.availableSessions.first?.id ?? 0
                                    if let ses = viewModel.availableSessions.first(where: { $0.id == sId }) {
                                        viewModel.addWorkout(
                                            dayOfWeek: newWorkoutDayOfWeek,
                                            session: ses,
                                            focus: newWorkoutFocus.isEmpty ? nil : newWorkoutFocus
                                        )
                                        newWorkoutFocus = ""
                                        showingAddWorkoutSection = false
                                    }
                                }
                                .disabled(viewModel.availableSessions.isEmpty)
                                .buttonStyle(.borderedProminent)
                                .tint(theme.selectedAccent.color)

                                Button("Cancel") {
                                    showingAddWorkoutSection = false
                                }
                                .buttonStyle(.bordered)
                            }
                        }
                        .padding(.vertical, 6)
                    } else {
                        Button {
                            showingAddWorkoutSection = true
                            if selectedSessionId == nil {
                                selectedSessionId = viewModel.availableSessions.first?.id
                            }
                        } label: {
                            Label("Add Workout Day", systemImage: "plus.circle.fill")
                                .foregroundStyle(theme.selectedAccent.color)
                        }
                    }
                }

                if let error = viewModel.errorMessage {
                    Section {
                        Text(error)
                            .font(.caption)
                            .foregroundStyle(.red)
                    }
                }
            }
            .navigationTitle(viewModel.isNewProgram ? "New Program" : "Edit Program")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") {
                        dismiss()
                    }
                }

                ToolbarItem(placement: .confirmationAction) {
                    Button(viewModel.isSaving ? "Saving..." : "Save") {
                        Task {
                            let success = await viewModel.saveProgram()
                            if success {
                                dismiss()
                            }
                        }
                    }
                    .disabled(!viewModel.isValid || viewModel.isSaving)
                }
            }
            .task {
                await viewModel.loadSessions()
            }
        }
    }
}
