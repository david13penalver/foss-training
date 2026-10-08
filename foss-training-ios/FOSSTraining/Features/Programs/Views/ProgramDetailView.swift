import SwiftUI

public struct ProgramDetailView: View {
    @Environment(\.theme) private var theme
    private let programRepository: TrainingProgramRepository
    private let sessionRepository: SessionRepository

    @State private var viewModel: ProgramDetailViewModel
    @State private var isEditing: Bool = false
    @State private var isShowingGenerateSheet: Bool = false

    public init(
        programRepository: TrainingProgramRepository,
        sessionRepository: SessionRepository,
        program: TrainingProgram
    ) {
        self.programRepository = programRepository
        self.sessionRepository = sessionRepository
        self._viewModel = State(initialValue: ProgramDetailViewModel(
            programRepository: programRepository,
            program: program
        ))
    }

    public var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
                // Header Info
                VStack(alignment: .leading, spacing: 8) {
                    HStack(spacing: 8) {
                        Text(viewModel.program.level.displayName)
                            .themedBadge()

                        Text(viewModel.program.periodizationType.displayName)
                            .font(.caption.weight(.medium))
                            .padding(.horizontal, 8)
                            .padding(.vertical, 4)
                            .background(Color.white.opacity(0.08))
                            .clipShape(Capsule())

                        Text("\(viewModel.program.durationWeeks) Weeks")
                            .font(.caption.weight(.medium))
                            .padding(.horizontal, 8)
                            .padding(.vertical, 4)
                            .background(Color.white.opacity(0.08))
                            .clipShape(Capsule())

                        Spacer()
                    }

                    if let desc = viewModel.program.description, !desc.isEmpty {
                        Text(desc)
                            .font(.subheadline)
                            .foregroundStyle(.secondary)
                    }
                }
                .padding(.horizontal)

                // Adherence Card
                if let adherence = viewModel.adherence {
                    ProgramAdherenceCard(adherence: adherence)
                        .padding(.horizontal)
                }

                // 7-Day Timetable
                VStack(alignment: .leading, spacing: 12) {
                    Text("Weekly Microcycle")
                        .font(.headline)
                        .foregroundStyle(.primary)
                        .padding(.horizontal)

                    VStack(spacing: 8) {
                        ForEach(viewModel.daysOfWeekSchedule) { item in
                            HStack {
                                Text(item.dayName)
                                    .font(.system(.subheadline, design: .monospaced, weight: .bold))
                                    .frame(width: 44, alignment: .leading)
                                    .foregroundStyle(item.isRestDay ? .secondary : theme.selectedAccent.color)

                                if let workout = item.workout {
                                    VStack(alignment: .leading, spacing: 2) {
                                        Text(workout.session.name)
                                            .font(.subheadline.weight(.semibold))
                                        if let focus = workout.focus, !focus.isEmpty {
                                            Text(focus)
                                                .font(.caption)
                                                .foregroundStyle(.secondary)
                                        }
                                    }
                                } else {
                                    Text("Rest & Recovery")
                                        .font(.subheadline)
                                        .foregroundStyle(.secondary)
                                }

                                Spacer()

                                if !item.isRestDay {
                                    Image(systemName: "dumbbell.fill")
                                        .font(.caption)
                                        .foregroundStyle(theme.selectedAccent.color.opacity(0.7))
                                }
                            }
                            .padding(.horizontal, 14)
                            .padding(.vertical, 10)
                            .background(item.isRestDay ? Color.white.opacity(0.02) : theme.surfaceStyle.cardBackgroundColor)
                            .clipShape(RoundedRectangle(cornerRadius: 12))
                            .overlay(
                                RoundedRectangle(cornerRadius: 12)
                                    .stroke(item.isRestDay ? Color.clear : Color.white.opacity(0.05), lineWidth: 1)
                            )
                        }
                    }
                    .padding(.horizontal)
                }

                // Success Message Banner
                if let success = viewModel.scheduleSuccessMessage {
                    HStack {
                        Image(systemName: "checkmark.circle.fill")
                            .foregroundStyle(.green)
                        Text(success)
                            .font(.caption.weight(.medium))
                            .foregroundStyle(.green)
                    }
                    .padding(.horizontal)
                }

                // Action Buttons
                VStack(spacing: 12) {
                    Button {
                        isShowingGenerateSheet = true
                    } label: {
                        HStack {
                            Image(systemName: "calendar.badge.plus")
                            Text("Generate Calendar Schedule")
                        }
                        .font(.headline)
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(theme.selectedAccent.color)
                        .foregroundStyle(.white)
                        .clipShape(RoundedRectangle(cornerRadius: 14))
                    }

                    Button {
                        isEditing = true
                    } label: {
                        HStack {
                            Image(systemName: "pencil")
                            Text("Edit Program")
                        }
                        .font(.subheadline.weight(.medium))
                        .frame(maxWidth: .infinity)
                        .padding(12)
                        .background(Color.white.opacity(0.08))
                        .foregroundStyle(.primary)
                        .clipShape(RoundedRectangle(cornerRadius: 12))
                    }
                }
                .padding(.horizontal)
                .padding(.top, 8)
            }
            .padding(.vertical)
        }
        .navigationTitle(viewModel.program.name)
        .navigationBarTitleDisplayMode(.inline)
        .sheet(isPresented: $isShowingGenerateSheet) {
            GenerateScheduleSheet(program: viewModel.program) { startDate in
                Task {
                    await viewModel.generateSchedule(startDate: startDate)
                }
            }
        }
        .sheet(isPresented: $isEditing) {
            ProgramEditorSheet(
                programRepository: programRepository,
                sessionRepository: sessionRepository,
                programToEdit: viewModel.program
            )
        }
        .task {
            await viewModel.loadDetails()
        }
    }
}
