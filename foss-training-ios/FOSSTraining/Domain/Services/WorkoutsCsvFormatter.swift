import Foundation

public enum WorkoutsCsvFormatter {
    public static let csvHeader = "training_id,training_date,training_name,training_status,session_rpe,exercise_name,exercise_category,item_number,set_type,weight_kg,reps,set_rpe,distance_m,duration_s,notes\n"

    public static func escape(_ value: String?) -> String {
        guard let value = value, !value.isEmpty else { return "" }
        if value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r") {
            let replaced = value.replacingOccurrences(of: "\"", with: "\"\"")
            return "\"\(replaced)\""
        }
        return value
    }

    public static func formatWorkoutsCsv(trainings: [Training]) -> String {
        let sorted = trainings.sorted { $0.trainingDate < $1.trainingDate }
        var result = csvHeader

        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd"
        dateFormatter.timeZone = TimeZone(secondsFromGMT: 0)

        for t in sorted {
            let trainingId = String(t.id)
            let trainingDate = dateFormatter.string(from: t.trainingDate)
            let trainingName = escape(t.name)
            let trainingStatus = t.status.rawValue.uppercased()
            let sessionRpe = t.overallRpe.map { String(format: "%.1f", $0) } ?? ""

            if t.loggedExercises.isEmpty {
                let notes = escape(t.notes)
                result.append("\(trainingId),\(trainingDate),\(trainingName),\(trainingStatus),\(sessionRpe),\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\(notes)\n")
                continue
            }

            for ex in t.loggedExercises {
                let exerciseName = escape(ex.exerciseName)
                if ex.sets.isEmpty {
                    let notes = escape(t.notes)
                    result.append("\(trainingId),\(trainingDate),\(trainingName),\(trainingStatus),\(sessionRpe),\(exerciseName),RESISTANCE,\"\",\"\",\"\",\"\",\"\",\"\",\"\",\(notes)\n")
                } else {
                    for set in ex.sets {
                        let setNum = String(set.setNumber)
                        let setType = set.setType.rawValue.uppercased()
                        let weight = String(format: "%.1f", set.weightKg)
                        let reps = String(set.repetitions)
                        let setRpe = set.rpe.map { String(format: "%.1f", $0) } ?? ""
                        let notes = escape(t.notes)

                        result.append("\(trainingId),\(trainingDate),\(trainingName),\(trainingStatus),\(sessionRpe),\(exerciseName),RESISTANCE,\(setNum),\(setType),\(weight),\(reps),\(setRpe),\"\",\"\",\(notes)\n")
                    }
                }
            }
        }

        return result
    }

    public static func parseWorkoutsCsv(_ csvContent: String) -> [Training] {
        var rows: [[String]] = []
        var currentRow: [String] = []
        var currentField = ""
        var insideQuotes = false

        let characters = Array(csvContent)
        var i = 0

        while i < characters.count {
            let ch = characters[i]

            if insideQuotes {
                if ch == "\"" {
                    if i + 1 < characters.count && characters[i + 1] == "\"" {
                        currentField.append("\"")
                        i += 2
                        continue
                    } else {
                        insideQuotes = false
                    }
                } else {
                    currentField.append(ch)
                }
            } else {
                if ch == "\"" {
                    insideQuotes = true
                } else if ch == "," {
                    currentRow.append(currentField)
                    currentField = ""
                } else if ch == "\n" || ch == "\r" || ch == "\r\n" {
                    currentRow.append(currentField)
                    currentField = ""
                    if !currentRow.isEmpty && !(currentRow.count == 1 && currentRow[0].isEmpty) {
                        rows.append(currentRow)
                    }
                    currentRow = []
                } else {
                    currentField.append(ch)
                }
            }
            i += 1
        }

        if !currentField.isEmpty || !currentRow.isEmpty {
            currentRow.append(currentField)
            if !currentRow.isEmpty && !(currentRow.count == 1 && currentRow[0].isEmpty) {
                rows.append(currentRow)
            }
        }

        guard rows.count > 1 else { return [] }
        let dataRows = rows.dropFirst()

        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd"
        dateFormatter.timeZone = TimeZone(secondsFromGMT: 0)

        var trainingsById: [Int: Training] = [:]
        var exerciseItemsByTraining: [Int: [String: [ResistanceSet]]] = [:]
        var trainingOrder: [Int] = []

        for row in dataRows {
            guard row.count >= 4 else { continue }
            guard let trId = Int(row[0].trimmingCharacters(in: .whitespaces)) else { continue }
            let dateStr = row.count > 1 ? row[1].trimmingCharacters(in: .whitespaces) : ""
            let name = row.count > 2 ? row[2] : "Workout"
            let statusStr = row.count > 3 ? row[3].uppercased() : "COMPLETED"
            let sessionRpeStr = row.count > 4 ? row[4].trimmingCharacters(in: .whitespaces) : ""
            let sessionRpe = Double(sessionRpeStr)

            let exName = row.count > 5 ? row[5] : ""
            let setNumStr = row.count > 7 ? row[7].trimmingCharacters(in: .whitespaces) : ""
            let setNum = Int(setNumStr) ?? 1
            let setTypeStr = row.count > 8 ? row[8].uppercased() : "NORMAL"
            let setType = SetType(rawValue: setTypeStr) ?? .normal
            let weightStr = row.count > 9 ? row[9].trimmingCharacters(in: .whitespaces) : ""
            let weight = Double(weightStr) ?? 0.0
            let repsStr = row.count > 10 ? row[10].trimmingCharacters(in: .whitespaces) : ""
            let reps = Int(repsStr) ?? 0
            let setRpeStr = row.count > 11 ? row[11].trimmingCharacters(in: .whitespaces) : ""
            let setRpe = Double(setRpeStr)
            let notes = row.count > 14 ? row[14] : ""

            if trainingsById[trId] == nil {
                let date = dateFormatter.date(from: dateStr) ?? Date()
                let status = TrainingStatus(rawValue: statusStr) ?? .completed
                let tr = Training(
                    id: trId,
                    name: name,
                    trainingDate: date,
                    status: status,
                    notes: notes.isEmpty ? nil : notes,
                    overallRpe: sessionRpe,
                    loggedExercises: []
                )
                trainingsById[trId] = tr
                trainingOrder.append(trId)
            }

            if !exName.isEmpty {
                let rSet = ResistanceSet(
                    setNumber: setNum,
                    setType: setType,
                    weightKg: weight,
                    repetitions: reps,
                    rpe: setRpe,
                    isCompleted: true
                )
                if exerciseItemsByTraining[trId] == nil {
                    exerciseItemsByTraining[trId] = [:]
                }
                if exerciseItemsByTraining[trId]?[exName] == nil {
                    exerciseItemsByTraining[trId]?[exName] = []
                }
                exerciseItemsByTraining[trId]?[exName]?.append(rSet)
            }
        }

        var result: [Training] = []
        for id in trainingOrder {
            guard var tr = trainingsById[id] else { continue }
            if let exDict = exerciseItemsByTraining[id] {
                var orderIndex = 1
                var items: [SessionExerciseItem] = []
                for (exName, sets) in exDict {
                    items.append(
                        SessionExerciseItem(
                            orderIndex: orderIndex,
                            exerciseId: orderIndex,
                            exerciseName: exName,
                            sets: sets.sorted { $0.setNumber < $1.setNumber }
                        )
                    )
                    orderIndex += 1
                }
                tr.loggedExercises = items
            }
            result.append(tr)
        }

        return result
    }
}
