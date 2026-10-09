import Foundation
import Testing
@testable import FOSSTraining

@Suite("Backup Serialization & Value Objects Tests")
struct BackupSerializationTests {

    @Test("FullBackupData: complete encoding and decoding roundtrip with 5 aggregates")
    func testBackupSerializationRoundtrip() throws {
        let ex = Exercise(id: 1, name: "Squat", primaryCategory: .resistance)
        let ses = Session(id: 2, name: "Leg Day", exercises: [])
        let prog = TrainingProgram(id: 3, name: "Strength Cycle", durationWeeks: 4)
        let tr = Training(id: 4, name: "Morning Squat", status: .completed)
        let bw = BodyweightEntry(id: 5, weightKg: 82.5, measuredDate: Date(), notes: "Fast")

        let original = FullBackupData(
            exportVersion: "1.0",
            exportedAt: Date(),
            exercises: [ex],
            sessions: [ses],
            programs: [prog],
            trainings: [tr],
            bodyweightEntries: [bw]
        )

        let encoder = JSONEncoder()
        encoder.dateEncodingStrategy = .iso8601
        encoder.outputFormatting = .prettyPrinted

        let data = try encoder.encode(original)
        #expect(!data.isEmpty)

        let decoder = JSONDecoder()
        decoder.dateDecodingStrategy = .iso8601
        let decoded = try decoder.decode(FullBackupData.self, from: data)

        #expect(decoded.exportVersion == "1.0")
        #expect(decoded.exercises.count == 1)
        #expect(decoded.exercises[0].id == 1)
        #expect(decoded.sessions.count == 1)
        #expect(decoded.sessions[0].id == 2)
        #expect(decoded.programs.count == 1)
        #expect(decoded.programs[0].id == 3)
        #expect(decoded.trainings.count == 1)
        #expect(decoded.trainings[0].id == 4)
        #expect(decoded.bodyweightEntries.count == 1)
        #expect(decoded.bodyweightEntries[0].id == 5)
        #expect(decoded.bodyweightEntries[0].weightKg == 82.5)
    }

    @Test("FullBackupData: default initializers and empty aggregates")
    func testBackupDefaultInit() {
        let empty = FullBackupData()
        #expect(empty.exportVersion == "1.0")
        #expect(empty.exercises.isEmpty)
        #expect(empty.sessions.isEmpty)
        #expect(empty.programs.isEmpty)
        #expect(empty.trainings.isEmpty)
        #expect(empty.bodyweightEntries.isEmpty)

        let alias: BackupDataPayload = empty
        #expect(alias.exportVersion == "1.0")
    }

    @Test("ImportMode: cases, rawValues, id, displayNames, and descriptions")
    func testImportModeEnums() {
        for mode in ImportMode.allCases {
            #expect(!mode.id.isEmpty)
            #expect(mode.id == mode.rawValue)
            #expect(!mode.displayName.isEmpty)
            #expect(!mode.description.isEmpty)
        }
    }

    @Test("ImportSummary: total count and properties")
    func testImportSummary() {
        let summary = ImportSummary(
            exercisesImported: 10,
            sessionsImported: 4,
            programsImported: 2,
            trainingsImported: 8,
            bodyweightImported: 15
        )
        #expect(summary.exercisesImported == 10)
        #expect(summary.sessionsImported == 4)
        #expect(summary.programsImported == 2)
        #expect(summary.trainingsImported == 8)
        #expect(summary.bodyweightImported == 15)
        #expect(summary.totalImported == 39)

        let defaultSummary = ImportSummary()
        #expect(defaultSummary.totalImported == 0)
    }
}
