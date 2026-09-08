package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.program;

import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.session.SessionResponseDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ProgramWorkoutResponse")
public class ProgramWorkoutResponseDto {

    @Schema(description = "Day of week (1 = Monday, 7 = Sunday)", example = "1")
    private Integer dayOfWeek;

    @Schema(description = "Training focus or target muscle groups", example = "Upper Body Strength")
    private String focus;

    @Schema(description = "Session template executed on this day")
    private SessionResponseDto session;
}
