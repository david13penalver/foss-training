package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.program;

import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.session.SessionRequestDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ProgramWorkoutRequest", description = "Scheduled workout day within a program microcycle")
public class ProgramWorkoutRequestDto {

    @NotNull(message = "Day of week cannot be null")
    @Min(value = 1, message = "Day of week must be between 1 (Monday) and 7 (Sunday)")
    @Max(value = 7, message = "Day of week must be between 1 (Monday) and 7 (Sunday)")
    @Schema(description = "Day of week (1 = Monday, 7 = Sunday)", example = "1")
    private Integer dayOfWeek;

    @Schema(description = "Training focus or target muscle groups", example = "Upper Body Strength")
    private String focus;

    @NotNull(message = "Session template cannot be null")
    @Valid
    @Schema(description = "Session template executed on this day")
    private SessionRequestDto session;
}
