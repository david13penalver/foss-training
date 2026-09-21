package com.david13penalver.foss_training_api.domain.model.export;

public record ImportSummary(
        int exercisesImported,
        int sessionsImported,
        int programsImported,
        int trainingsImported,
        int bodyweightImported
) {

    public int totalImported() {
        return exercisesImported + sessionsImported + programsImported + trainingsImported + bodyweightImported;
    }
}
