package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.export;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ImportSummary", description = "Summary of restored records count")
public class ImportSummaryDto {

    @Schema(description = "Number of exercises restored", example = "42")
    private int exercisesImported;

    @Schema(description = "Number of sessions restored", example = "8")
    private int sessionsImported;

    @Schema(description = "Number of training programs restored", example = "2")
    private int programsImported;

    @Schema(description = "Number of workout trainings restored", example = "15")
    private int trainingsImported;

    @Schema(description = "Number of bodyweight records restored", example = "30")
    private int bodyweightImported;

    @Schema(description = "Total number of entities restored across all modules", example = "97")
    private int totalImported;
}
