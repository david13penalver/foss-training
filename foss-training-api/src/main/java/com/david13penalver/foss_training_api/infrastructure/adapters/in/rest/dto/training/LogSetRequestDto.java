package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.training;

import com.david13penalver.foss_training_api.domain.model.session.SetType;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.common.RpeDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.common.WeightDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
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
@Schema(name = "LogSetRequest", description = "Granular set performance details for live workout logging")
public class LogSetRequestDto {

    @Schema(description = "Set sequence number (1-based). If null or <= 0, automatically appends next set.", example = "1")
    @Min(1)
    private Integer setNumber;

    @Schema(description = "Classification of the set", example = "WORKING")
    private SetType setType;

    @Valid
    @NotNull(message = "Weight details must be provided")
    @Schema(description = "Weight lifted")
    private WeightDto weight;

    @NotNull(message = "Repetitions must not be null")
    @Min(value = 0, message = "Repetitions cannot be negative")
    @Schema(description = "Number of completed repetitions", example = "8")
    private Integer repetitions;

    @Valid
    @Schema(description = "Rate of Perceived Exertion (1-10 scale)")
    private RpeDto rpe;

    @Schema(description = "Rest duration following this set in seconds", example = "90")
    @Min(0)
    private Integer restSeconds;

    @Schema(description = "Whether the set was completed", example = "true")
    private Boolean completed;
}
