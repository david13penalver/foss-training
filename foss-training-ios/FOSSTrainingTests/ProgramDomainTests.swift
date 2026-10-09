import Testing
import Foundation
@testable import FOSSTraining

@Suite("SPEC-04: Program Domain, Schedule Generation & Adherence Math Tests")
struct ProgramDomainTests {

    private func createSampleSession(id: Int = 10, name: String = "Upper Power") -> Session {
        Session(
            id: id,
            name: name,
            description: "Heavy compound lifts",
            estimatedDurationMinutes: 60,
            exercises: []
        )
    }

    private func createValidProgram() -> TrainingProgram {
        let w1 = ProgramWorkout(
            dayOfWeek: 1,
            focus: "Chest & Back",
            session: createSampleSession(id: 1, name: "Upper A")
        )
        let w2 = ProgramWorkout(
            dayOfWeek: 3,
            focus: "Quads & Hamstrings",
            session: createSampleSession(id: 2, name: "Lower A")
        )
        let w3 = ProgramWorkout(
            dayOfWeek: 5,
            focus: "Shoulders & Arms",
            session: createSampleSession(id: 3, name: "Upper B")
        )
        return TrainingProgram(
            id: 100,
            name: "Hypertrophy Block",
            description: "8-week undulating mass building",
            durationWeeks: 8,
            periodizationType: .undulating,
            level: .intermediate,
            workouts: [w1, w2, w3],
            isActive: true
        )
    }

    // MARK: - Task 1.1: TrainingProgram Domain Validation Tests

    @Test("Valid training program passes validation")
    func testValidProgramValidation() throws {
        let program = createValidProgram()
        #expect(throws: Never.self) {
            try program.validate()
        }
        #expect(program.totalWorkoutsPerCycle == 24)
        #expect(program.isActive == true)
    }

    @Test("TrainingProgram with blank or short name throws nameTooShort")
    func testProgramNameTooShort() {
        var p1 = createValidProgram()
        p1.name = "A"
        #expect(throws: ProgramValidationError.nameTooShort) {
            try p1.validate()
        }

        var p2 = createValidProgram()
        p2.name = "   "
        #expect(throws: ProgramValidationError.nameTooShort) {
            try p2.validate()
        }
    }

    @Test("TrainingProgram with name > 100 chars throws nameTooLong")
    func testProgramNameTooLong() {
        var program = createValidProgram()
        program.name = String(repeating: "P", count: 101)
        #expect(throws: ProgramValidationError.nameTooLong) {
            try program.validate()
        }
    }

    @Test("TrainingProgram with duration < 1 or > 52 throws invalidDurationWeeks")
    func testProgramInvalidDuration() {
        var p1 = createValidProgram()
        p1.durationWeeks = 0
        #expect(throws: ProgramValidationError.invalidDurationWeeks) {
            try p1.validate()
        }

        var p2 = createValidProgram()
        p2.durationWeeks = -4
        #expect(throws: ProgramValidationError.invalidDurationWeeks) {
            try p2.validate()
        }

        var p3 = createValidProgram()
        p3.durationWeeks = 53
        #expect(throws: ProgramValidationError.invalidDurationWeeks) {
            try p3.validate()
        }
    }

    @Test("ProgramWorkout with dayOfWeek outside 1...7 throws invalidDayOfWeek")
    func testProgramWorkoutInvalidDay() {
        var p1 = createValidProgram()
        p1.workouts = [
            ProgramWorkout(dayOfWeek: 0, session: createSampleSession())
        ]
        #expect(throws: ProgramValidationError.invalidDayOfWeek) {
            try p1.validate()
        }

        var p2 = createValidProgram()
        p2.workouts = [
            ProgramWorkout(dayOfWeek: 8, session: createSampleSession())
        ]
        #expect(throws: ProgramValidationError.invalidDayOfWeek) {
            try p2.validate()
        }
    }

    @Test("PeriodizationType displayNames and rawValues")
    func testPeriodizationType() {
        #expect(PeriodizationType.linear.rawValue == "LINEAR")
        #expect(PeriodizationType.linear.displayName == "Linear Periodization")
        #expect(PeriodizationType.undulating.rawValue == "UNDULATING")
        #expect(PeriodizationType.undulating.displayName == "Daily Undulating (DUP)")
        #expect(PeriodizationType.block.rawValue == "BLOCK")
        #expect(PeriodizationType.block.displayName == "Block Periodization")
        #expect(PeriodizationType.reverseLinear.rawValue == "REVERSE_LINEAR")
        #expect(PeriodizationType.reverseLinear.displayName == "Reverse Linear")
        #expect(PeriodizationType.conjugated.rawValue == "CONJUGATED")
        #expect(PeriodizationType.conjugated.displayName == "Conjugated / Westside")
        #expect(PeriodizationType.allCases.count == 5)
    }

    @Test("ProgramLevel displayNames and rawValues")
    func testProgramLevel() {
        #expect(ProgramLevel.beginner.rawValue == "BEGINNER")
        #expect(ProgramLevel.beginner.displayName == "Beginner")
        #expect(ProgramLevel.intermediate.rawValue == "INTERMEDIATE")
        #expect(ProgramLevel.intermediate.displayName == "Intermediate")
        #expect(ProgramLevel.advanced.rawValue == "ADVANCED")
        #expect(ProgramLevel.advanced.displayName == "Advanced")
        #expect(ProgramLevel.elite.rawValue == "ELITE")
        #expect(ProgramLevel.elite.displayName == "Elite")
        #expect(ProgramLevel.allCases.count == 4)
        for level in ProgramLevel.allCases {
            #expect(level.id == level.rawValue)
        }
    }

    @Test("ProgramWorkout dayName formatting")
    func testProgramWorkoutDayName() {
        let days = [
            (1, "Monday"),
            (2, "Tuesday"),
            (3, "Wednesday"),
            (4, "Thursday"),
            (5, "Friday"),
            (6, "Saturday"),
            (7, "Sunday"),
            (99, "Day 99")
        ]
        for (day, expected) in days {
            let pw = ProgramWorkout(dayOfWeek: day, session: createSampleSession())
            #expect(pw.dayName == expected)
        }
    }

    @Test("ProgramValidationError localized error descriptions")
    func testProgramValidationErrorDescriptions() {
        #expect(ProgramValidationError.nameTooShort.errorDescription?.contains("at least 2") == true)
        #expect(ProgramValidationError.nameTooLong.errorDescription?.contains("100 characters") == true)
        #expect(ProgramValidationError.invalidDurationWeeks.errorDescription?.contains("between 1 and 52") == true)
        #expect(ProgramValidationError.invalidDayOfWeek.errorDescription?.contains("between 1 (Monday) and 7 (Sunday)") == true)
        #expect(ProgramValidationError.noWorkoutsConfigured.errorDescription?.contains("at least one workout day") == true)
    }

    // MARK: - Task 1.3: Program Schedule Generation Tests

    @Test("Schedule generation creates exactly durationWeeks * workouts.count trainings")
    func testScheduleGenerationCount() throws {
        let program = createValidProgram() // 8 weeks, 3 workouts = 24 trainings
        let calendar = Calendar.current
        var comps = DateComponents()
        comps.year = 2026
        comps.month = 10
        comps.day = 12 // Monday
        let startDate = calendar.date(from: comps)!

        let scheduled = program.generateSchedule(startDate: startDate)
        #expect(scheduled.count == 24)

        // Verify first workout
        let first = scheduled[0]
        #expect(first.name == "Hypertrophy Block - W1D1: Upper A")
        #expect(first.description == "Chest & Back")
        #expect(first.status == .planned)
        #expect(first.programId == 100)
        #expect(first.loggedExercises.isEmpty)

        // Verify date of first workout matches Monday Oct 12, 2026
        let firstDay = calendar.component(.day, from: first.trainingDate)
        #expect(firstDay == 12)

        // Verify second workout: Wednesday (day 3 = dayOffset 2 = Oct 14)
        let second = scheduled[1]
        #expect(second.name == "Hypertrophy Block - W1D3: Lower A")
        let secondDay = calendar.component(.day, from: second.trainingDate)
        #expect(secondDay == 14)

        // Verify last workout: Week 8, Day 5 (Friday)
        let last = scheduled[23]
        #expect(last.name == "Hypertrophy Block - W8D5: Upper B")
        #expect(last.programId == 100)
    }

    @Test("Schedule generation with fallback workout session title")
    func testScheduleGenerationFallbackSession() {
        let emptySession = Session(id: 99, name: "")
        let pw = ProgramWorkout(dayOfWeek: 2, focus: nil, session: emptySession)
        let prog = TrainingProgram(
            id: 5,
            name: "Simple",
            durationWeeks: 1,
            workouts: [pw]
        )
        let scheduled = prog.generateSchedule(startDate: Date())
        #expect(scheduled.count == 1)
        #expect(scheduled[0].name == "Simple - W1D2: Workout")
        #expect(scheduled[0].description == nil)
    }

    // MARK: - Task 1.5: Program Adherence Calculator Math Parity Tests

    @Test("Adherence calculation with empty trainings returns notStarted")
    func testAdherenceEmptyTrainings() {
        let program = createValidProgram()
        let adherence = ProgramAdherenceCalculator.calculate(program: program, trainings: [], referenceDate: Date())

        #expect(adherence.programId == 100)
        #expect(adherence.programName == "Hypertrophy Block")
        #expect(adherence.durationWeeks == 8)
        #expect(adherence.totalScheduledWorkouts == 24)
        #expect(adherence.completedWorkouts == 0)
        #expect(adherence.overallCompletionRate == 0.0)
        #expect(adherence.currentAdherenceRate == 0.0)
        #expect(adherence.currentStreak == 0)
        #expect(adherence.longestStreak == 0)
        #expect(adherence.status == .notStarted)
        #expect(adherence.statusDescription == "Not Started")
        #expect(adherence.weeklyBreakdowns.isEmpty)
        #expect(adherence.workoutDetails.isEmpty)
    }

    @Test("Adherence calculation: onTrack, behindSchedule, atRisk, completed statuses and streaks")
    func testAdherenceStatusesAndStreaks() {
        let program = createValidProgram()
        let calendar = Calendar.current
        var comps = DateComponents()
        comps.year = 2026
        comps.month = 10
        comps.day = 12
        let monday = calendar.date(from: comps)!
        let refDate = calendar.date(byAdding: .day, value: 5, to: monday)! // Saturday

        // 3 trainings in week 1:
        // Day 1 (Monday): Completed
        // Day 3 (Wednesday): Completed
        // Day 5 (Friday): Planned (before Saturday reference date -> overdue -> missed!)
        let t1 = Training(
            id: 1,
            name: "W1D1",
            trainingDate: monday,
            startTime: monday,
            endTime: calendar.date(byAdding: .hour, value: 1, to: monday),
            status: .completed,
            programId: 100
        )
        let wednesday = calendar.date(byAdding: .day, value: 2, to: monday)!
        let t2 = Training(
            id: 2,
            name: "W1D3",
            trainingDate: wednesday,
            status: .completed,
            programId: 100
        )
        let friday = calendar.date(byAdding: .day, value: 4, to: monday)!
        let t3 = Training(
            id: 3,
            name: "W1D5",
            trainingDate: friday,
            status: .planned,
            programId: 100
        )

        let adherence = ProgramAdherenceCalculator.calculate(
            program: program,
            trainings: [t1, t2, t3],
            referenceDate: refDate
        )

        #expect(adherence.completedWorkouts == 2)
        #expect(adherence.missedWorkouts == 1) // Friday is overdue planned
        #expect(adherence.plannedWorkouts == 0)
        // expectedElapsed = 2 (completed) + 1 (missed) = 3
        // currentAdherenceRate = 2 / 3 * 100 = 66.7% -> BEHIND_SCHEDULE
        #expect(adherence.currentAdherenceRate == 66.7)
        #expect(adherence.status == .behindSchedule)
        #expect(adherence.currentStreak == 0) // Friday was missed!
        #expect(adherence.longestStreak == 2) // Monday and Wednesday were completed!
        #expect(adherence.weeklyBreakdowns.count == 8)
        #expect(adherence.weeklyBreakdowns[0].weekNumber == 1)
        #expect(adherence.weeklyBreakdowns[0].scheduledWorkouts == 3)
        #expect(adherence.weeklyBreakdowns[0].completedWorkouts == 2)
        #expect(adherence.weeklyBreakdowns[0].missedWorkouts == 1)
        #expect(adherence.weeklyBreakdowns[0].completed == false)
    }

    @Test("Adherence calculation: all completed returns status .completed")
    func testAdherenceAllCompleted() {
        let prog = TrainingProgram(
            id: 1,
            name: "Mini Cycle",
            durationWeeks: 1,
            workouts: [ProgramWorkout(dayOfWeek: 1, session: createSampleSession())]
        )
        let t = Training(
            id: 1,
            name: "Day 1",
            trainingDate: Date(),
            status: .completed,
            programId: 1
        )
        let adherence = ProgramAdherenceCalculator.calculate(program: prog, trainings: [t], referenceDate: Date())
        #expect(adherence.completedWorkouts == 1)
        #expect(adherence.totalScheduledWorkouts == 1)
        #expect(adherence.overallCompletionRate == 100.0)
        #expect(adherence.currentAdherenceRate == 100.0)
        #expect(adherence.currentStreak == 1)
        #expect(adherence.longestStreak == 1)
        #expect(adherence.status == .completed)
        #expect(adherence.statusDescription == "Completed")
    }

    @Test("ProgramAdherenceStatus displayNames")
    func testProgramAdherenceStatus() {
        #expect(ProgramAdherenceStatus.notStarted.displayName == "Not Started")
        #expect(ProgramAdherenceStatus.onTrack.displayName == "On Track")
        #expect(ProgramAdherenceStatus.behindSchedule.displayName == "Behind Schedule")
        #expect(ProgramAdherenceStatus.atRisk.displayName == "At Risk")
        #expect(ProgramAdherenceStatus.completed.displayName == "Completed")
    }

    @Test("WeeklyAdherence and WorkoutAdherenceItem identifiers and onTime calculation")
    func testAdherenceItemProperties() {
        let weekly = WeeklyAdherence(
            weekNumber: 3,
            scheduledWorkouts: 4,
            completedWorkouts: 4,
            missedWorkouts: 0,
            adherenceRate: 100.0,
            completed: true
        )
        #expect(weekly.id == 3)

        let item = WorkoutAdherenceItem(
            trainingId: 42,
            workoutName: "Leg Day",
            status: .completed,
            volumeKg: 5000.0,
            onTime: true
        )
        #expect(item.id == 42)
    }

    @Test("Adherence calculation branches: onTrack, atRisk, inProgress, cancelled, late completions")
    func testAdherenceBranchesCoverage() {
        let program = createValidProgram() // 8 weeks, 3 workouts/week
        let now = Date()
        let past = now.addingTimeInterval(-86400 * 10)
        let yesterday = now.addingTimeInterval(-86400)
        let future = now.addingTimeInterval(86400 * 2)

        // 1. Completed on time
        let t1 = Training(id: 1, name: "T1", trainingDate: past, startTime: past, endTime: past.addingTimeInterval(3600), status: .completed, programId: 100)
        // 2. Completed late (> 1 day after schedule)
        let t2 = Training(id: 2, name: "T2", trainingDate: past, startTime: past.addingTimeInterval(86400 * 3), endTime: past.addingTimeInterval(86400 * 3 + 3600), status: .completed, programId: 100)
        // 3. Completed with no start/end time fallback to scheduled date
        let t3 = Training(id: 3, name: "T3", trainingDate: yesterday, startTime: nil, endTime: nil, status: .completed, programId: 100)
        // 4. In progress
        let t4 = Training(id: 4, name: "T4", trainingDate: now, status: .inProgress, programId: 100)
        // 5. Paused
        let t5 = Training(id: 5, name: "T5", trainingDate: now, status: .paused, programId: 100)
        // 6. Cancelled
        let t6 = Training(id: 6, name: "T6", trainingDate: yesterday, status: .cancelled, programId: 100)
        // 7. Future planned
        let t7 = Training(id: 7, name: "T7", trainingDate: future, status: .planned, programId: 100)

        // 4 elapsed: 3 completed (t1, t2, t3) + 1 cancelled (t6) = 4 elapsed.
        // currentAdherenceRate = 3 / 4 * 100 = 75.0% -> behindSchedule
        let adh = ProgramAdherenceCalculator.calculate(
            program: program,
            trainings: [t1, t2, t3, t4, t5, t6, t7],
            referenceDate: now
        )
        #expect(adh.inProgressWorkouts == 2) // t4 + t5
        #expect(adh.cancelledWorkouts == 1) // t6
        #expect(adh.plannedWorkouts == 1) // t7
        #expect(adh.completedWorkouts == 3)
        #expect(adh.workoutDetails.first { $0.trainingId == 2 }?.onTime == false)
        #expect(adh.workoutDetails.first { $0.trainingId == 3 }?.onTime == true)

        // 8. Test onTrack: 4 completed, 1 cancelled -> 4/5 = 80.0% -> onTrack
        let tExtra = Training(id: 8, name: "T8", trainingDate: past, status: .completed, programId: 100)
        let adhOnTrack = ProgramAdherenceCalculator.calculate(
            program: program,
            trainings: [t1, t2, t3, tExtra, t6],
            referenceDate: now
        )
        #expect(adhOnTrack.currentAdherenceRate == 80.0)
        #expect(adhOnTrack.status == .onTrack)

        // 9. Test atRisk: 1 completed, 3 cancelled -> 1/4 = 25.0% -> atRisk
        let c1 = Training(id: 11, name: "C1", trainingDate: past, status: .cancelled, programId: 100)
        let c2 = Training(id: 12, name: "C2", trainingDate: past, status: .cancelled, programId: 100)
        let adhAtRisk = ProgramAdherenceCalculator.calculate(
            program: program,
            trainings: [t1, t6, c1, c2],
            referenceDate: now
        )
        #expect(adhAtRisk.currentAdherenceRate == 25.0)
        #expect(adhAtRisk.status == .atRisk)

        // 10. Test durationWeeks = 0 returns empty breakdowns
        var zeroProg = program
        zeroProg.durationWeeks = 0
        let adhZero = ProgramAdherenceCalculator.calculate(program: zeroProg, trainings: [t1], referenceDate: now)
        #expect(adhZero.weeklyBreakdowns.isEmpty)

        // 11. Test schedule generation with default startDate
        let autoSchedule = program.generateSchedule()
        #expect(!autoSchedule.isEmpty)

        // 12. Test adherence when trainings contains only future planned sessions (status becomes notStarted)
        let futureTraining = Training(id: 99, name: "Future", trainingDate: future, status: .planned, programId: 100)
        let adhFutureOnly = ProgramAdherenceCalculator.calculate(program: program, trainings: [futureTraining], referenceDate: now)
        #expect(adhFutureOnly.status == .notStarted)
        #expect(adhFutureOnly.completedWorkouts == 0)
        #expect(adhFutureOnly.plannedWorkouts == 1)

        // 13. Test adherence when trainings has only in-progress sessions (expectedElapsed == 0, inProgressCount > 0)
        let inProgressTraining = Training(id: 101, name: "Active", trainingDate: now, status: .inProgress, programId: 100)
        let adhInProgressOnly = ProgramAdherenceCalculator.calculate(program: program, trainings: [inProgressTraining], referenceDate: now)
        #expect(adhInProgressOnly.status == .atRisk)
        #expect(adhInProgressOnly.inProgressWorkouts == 1)
    }

    @Test("TrainingProgramRepository default protocol extension programExists")
    func testTrainingProgramRepositoryProtocolExtension() async throws {
        struct MockProgramRepo: TrainingProgramRepository {
            func getPrograms() async throws -> [TrainingProgram] { [] }
            func getProgram(id: Int) async throws -> TrainingProgram? {
                if id == 1 {
                    return TrainingProgram(id: 1, name: "Test")
                }
                return nil
            }
            func saveProgram(_ program: TrainingProgram) async throws -> TrainingProgram { program }
            func deleteProgram(id: Int) async throws {}
            func generateSchedule(programId: Int, startDate: Date?) async throws -> [Training] { [] }
            func cloneProgram(id: Int, newName: String?) async throws -> TrainingProgram {
                TrainingProgram(id: 2, name: "Cloned")
            }
            func getProgramAdherence(id: Int) async throws -> ProgramAdherence {
                ProgramAdherenceCalculator.calculate(program: TrainingProgram(id: id, name: "P"), trainings: [])
            }
        }

        let repo = MockProgramRepo()
        let exists = try await repo.programExists(id: 1)
        let notExists = try await repo.programExists(id: 999)
        #expect(exists == true)
        #expect(notExists == false)
    }
}
