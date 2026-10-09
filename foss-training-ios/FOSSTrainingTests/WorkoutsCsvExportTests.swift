import Foundation
import Testing
@testable import FOSSTraining

@Suite("Workouts CSV Export & Import Parity Tests")
struct WorkoutsCsvExportTests {

    @Test("CSV formatting: header matches canonical Spring Boot standard exactly")
    func testCsvHeaderMatchesStandard() {
        let emptyCsv = WorkoutsCsvFormatter.formatWorkoutsCsv(trainings: [])
        let expectedHeader = "training_id,training_date,training_name,training_status,session_rpe,exercise_name,exercise_category,item_number,set_type,weight_kg,reps,set_rpe,distance_m,duration_s,notes\n"
        #expect(emptyCsv == expectedHeader)
    }

    @Test("CSV formatting: escaping of commas, quotes, and newlines in exercise and workout names")
    func testCsvSpecialCharactersEscaping() {
        let set1 = ResistanceSet(
            setNumber: 1,
            setType: .normal,
            weightKg: 100.0,
            repetitions: 5,
            rpe: 8.0,
            isCompleted: true
        )
        let ex = SessionExerciseItem(
            orderIndex: 1,
            exerciseId: 1,
            exerciseName: "Bench Press, Incline (with \"Quote\")",
            sets: [set1]
        )
        let date = Date(timeIntervalSince1970: 1760000000) // Deterministic date
        let training = Training(
            id: 101,
            name: "Upper, Body \"Heavy\"",
            trainingDate: date,
            status: .completed,
            notes: "Felt strong, good bar speed\nNo shoulder pain",
            overallRpe: 8.5,
            loggedExercises: [ex]
        )

        let csv = WorkoutsCsvFormatter.formatWorkoutsCsv(trainings: [training])
        #expect(csv.contains("\"Upper, Body \"\"Heavy\"\"\""))
        #expect(csv.contains("\"Bench Press, Incline (with \"\"Quote\"\")\""))
        #expect(csv.contains("\"Felt strong, good bar speed\nNo shoulder pain\""))
    }

    @Test("CSV formatting: training without exercises outputs empty exercise columns")
    func testCsvTrainingWithoutExercises() {
        let date = Date(timeIntervalSince1970: 1760000000)
        let training = Training(
            id: 102,
            name: "Rest Day Mobility",
            trainingDate: date,
            status: .completed,
            notes: "Light walk",
            overallRpe: nil,
            loggedExercises: []
        )

        let csv = WorkoutsCsvFormatter.formatWorkoutsCsv(trainings: [training])
        let lines = csv.components(separatedBy: "\n")
        #expect(lines.count >= 2)
        let dataRow = lines[1]
        #expect(dataRow.contains("102,"))
        #expect(dataRow.contains("Rest Day Mobility"))
    }

    @Test("CSV parsing: roundtrip parsing rows back into Training objects")
    func testCsvParseRoundtrip() {
        let sampleCsv = """
        training_id,training_date,training_name,training_status,session_rpe,exercise_name,exercise_category,item_number,set_type,weight_kg,reps,set_rpe,distance_m,duration_s,notes
        201,2026-10-08,"Leg Day",COMPLETED,8.0,"Back Squat",RESISTANCE,1,NORMAL,140.0,5,8.0,,,Notes here
        201,2026-10-08,"Leg Day",COMPLETED,8.0,"Back Squat",RESISTANCE,2,NORMAL,140.0,5,8.5,,,Notes here
        202,2026-10-09,"Empty Session",PLANNED,,,,,,,,,,,No exercises
        """

        let parsed = WorkoutsCsvFormatter.parseWorkoutsCsv(sampleCsv)
        #expect(parsed.count == 2)
        #expect(parsed[0].id == 201)
        #expect(parsed[0].name == "Leg Day")
        #expect(parsed[0].loggedExercises.count == 1)
        #expect(parsed[0].loggedExercises[0].sets.count == 2)
        #expect(parsed[0].loggedExercises[0].sets[0].weightKg == 140.0)
        #expect(parsed[1].id == 202)
        #expect(parsed[1].name == "Empty Session")
        #expect(parsed[1].loggedExercises.isEmpty)
    }

    @Test("CSV formatter escape helper branches")
    func testEscapeHelperBranches() {
        #expect(WorkoutsCsvFormatter.escape(nil) == "")
        #expect(WorkoutsCsvFormatter.escape("") == "")
        #expect(WorkoutsCsvFormatter.escape("CleanText") == "CleanText")
        #expect(WorkoutsCsvFormatter.escape("Contains,Comma") == "\"Contains,Comma\"")
        #expect(WorkoutsCsvFormatter.escape("Contains\"Quote") == "\"Contains\"\"Quote\"")
        #expect(WorkoutsCsvFormatter.escape("Contains\nNewline") == "\"Contains\nNewline\"")
        #expect(WorkoutsCsvFormatter.escape("Contains\rReturn") == "\"Contains\rReturn\"")
    }

    @Test("CSV formatting: logged exercise without sets outputs empty set columns")
    func testCsvExerciseWithoutSets() {
        let ex = SessionExerciseItem(
            orderIndex: 1,
            exerciseId: 1,
            exerciseName: "Squat",
            sets: []
        )
        let tr = Training(id: 1, name: "Legs", trainingDate: Date(), status: .completed, notes: "No sets completed", loggedExercises: [ex])
        let csv = WorkoutsCsvFormatter.formatWorkoutsCsv(trainings: [tr])
        #expect(csv.contains("Squat,RESISTANCE,\"\",\"\",\"\",\"\",\"\",\"\",\"\",No sets completed"))
    }

    @Test("CSV parsing: escaped quotes inside field and Windows CRLF line endings")
    func testCsvParseEscapedQuotesAndCRLF() {
        let csvWithEscapedQuotes = "training_id,training_date,training_name,training_status,session_rpe,exercise_name,exercise_category,item_number,set_type,weight_kg,reps,set_rpe,distance_m,duration_s,notes\r\n101,2026-10-08,\"Name with \"\"Quotes\"\"\",COMPLETED,8.0,\"Bench Press\",RESISTANCE,1,NORMAL,100.0,5,8.0,,,\"Notes with \"\"Quotes\"\"\"\r\n"
        let parsed = WorkoutsCsvFormatter.parseWorkoutsCsv(csvWithEscapedQuotes)
        #expect(parsed.count == 1)
        #expect(parsed[0].name == "Name with \"Quotes\"")
        #expect(parsed[0].notes == "Notes with \"Quotes\"")
    }

    @Test("CSV parsing: invalid or empty rows fallback branches")
    func testCsvParseFallbackBranches() {
        // empty input
        #expect(WorkoutsCsvFormatter.parseWorkoutsCsv("").isEmpty)
        #expect(WorkoutsCsvFormatter.parseWorkoutsCsv("header_only\n").isEmpty)

        // row with fewer than 4 columns and non-integer id
        let malformedCsv = """
        training_id,training_date,training_name,training_status
        short,row
        invalid_id,2026-10-08,Name,COMPLETED
        100,invalid-date,FallbackWorkout,UNKNOWN_STATUS,,Squat,RESISTANCE,bad_num,UNKNOWN_TYPE,bad_weight,bad_reps,bad_rpe,,,
        """
        let parsed = WorkoutsCsvFormatter.parseWorkoutsCsv(malformedCsv)
        #expect(parsed.count == 1)
        #expect(parsed[0].id == 100)
        #expect(parsed[0].name == "FallbackWorkout")
        #expect(parsed[0].status == .completed)
        #expect(parsed[0].notes == nil)
        #expect(parsed[0].loggedExercises.count == 1)
        let set = parsed[0].loggedExercises[0].sets[0]
        #expect(set.setNumber == 1)
        #expect(set.setType == .normal)
        #expect(set.weightKg == 0.0)
        #expect(set.repetitions == 0)
        #expect(set.rpe == nil)
    }

    @Test("CSV formatting: out-of-order dates, nil set RPE, and 4-column rows without trailing newline")
    func testCsvRemainingEdgeCases() {
        let t1 = Training(id: 1, name: "Second", trainingDate: Date(timeIntervalSince1970: 2000), status: .completed)
        let t2 = Training(id: 2, name: "First", trainingDate: Date(timeIntervalSince1970: 1000), status: .completed, loggedExercises: [
            SessionExerciseItem(orderIndex: 1, exerciseId: 1, exerciseName: "Squat", sets: [
                ResistanceSet(setNumber: 1, setType: .normal, weightKg: 100, repetitions: 5, rpe: nil, isCompleted: true)
            ])
        ])
        let csv = WorkoutsCsvFormatter.formatWorkoutsCsv(trainings: [t1, t2])
        #expect(csv.contains("First"))

        // CSV with blank lines in the middle and trailing, multiple exercises and sets out of order
        let edgeCsv = """
        training_id,training_date,training_name,training_status,session_rpe,exercise_name,exercise_category,item_number,set_type,weight_kg,reps,set_rpe,distance_m,duration_s,notes

        105,2026-10-08,ShortWorkout,COMPLETED,8.0,"Squat",RESISTANCE,2,NORMAL,120.0,5,8.0,,,
        105,2026-10-08,ShortWorkout,COMPLETED,8.0,"Squat",RESISTANCE,1,NORMAL,100.0,5,7.0,,,
        105,2026-10-08,ShortWorkout,COMPLETED,8.0,"Bench",RESISTANCE,1,NORMAL,80.0,5,8.0,,,
        

        """
        let parsedEdge = WorkoutsCsvFormatter.parseWorkoutsCsv(edgeCsv)
        #expect(parsedEdge.count == 1)
        #expect(parsedEdge[0].id == 105)
        #expect(parsedEdge[0].loggedExercises.count == 2)
        let squatEx = parsedEdge[0].loggedExercises.first { $0.exerciseName == "Squat" }
        #expect(squatEx?.sets.first?.setNumber == 1)

        let fourColCsvNoTrailingNewline = "training_id,training_date,training_name,training_status\n105,2026-10-08,ShortWorkout,COMPLETED"
        let parsed = WorkoutsCsvFormatter.parseWorkoutsCsv(fourColCsvNoTrailingNewline)
        #expect(parsed.count == 1)
        #expect(parsed[0].id == 105)
        #expect(parsed[0].name == "ShortWorkout")
    }
}
