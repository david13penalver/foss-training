package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.training;

import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.common.DistanceDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.common.DurationDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.common.PaceDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "LogIntervalRequest", description = "Granular interval details for live endurance workout logging")
public class LogIntervalRequestDto {

    @Schema(description = "Interval sequence number (1-based). If null or <= 0, automatically appends next interval.", example = "1")
    @Min(1)
    private Integer intervalNumber;

    @Valid
    @Schema(description = "Distance covered")
    private DistanceDto distance;

    @Valid
    @Schema(description = "Duration of the interval")
    private DurationDto duration;

    @Valid
    @Schema(description = "Pace achieved")
    private PaceDto pace;

    @Schema(description = "Average heart rate during interval (bpm)", example = "155")
    private Integer avgHeartRate;

    @Schema(description = "Maximum heart rate reached during interval (bpm)", example = "172")
    private Integer maxHeartRate;

    @Schema(description = "Average power output in watts", example = "250.0")
    private Double avgPower;

    @Schema(description = "Cadence (rpm or spm)", example = "85.0")
    private Double cadence;

    @Schema(description = "Rest period after interval in seconds", example = "60")
    @Min(0)
    private Integer restSeconds;
}
