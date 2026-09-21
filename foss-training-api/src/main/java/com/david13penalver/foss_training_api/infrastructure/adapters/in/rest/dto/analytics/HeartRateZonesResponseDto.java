package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.analytics;

import java.util.List;

import com.david13penalver.foss_training_api.domain.model.analytics.HeartRateZoneMethod;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Cardiovascular training zones and target heart rate ranges")
public class HeartRateZonesResponseDto {

    @Schema(description = "Maximum heart rate (bpm) used in calculation", example = "190")
    private int maxHr;

    @Schema(description = "Resting heart rate (bpm) if provided", example = "60")
    private Integer restingHr;

    @Schema(description = "Athlete age if provided", example = "30")
    private Integer age;

    @Schema(description = "Calculation method applied", example = "KARVONEN")
    private HeartRateZoneMethod method;

    @Schema(description = "Method display name", example = "Karvonen Heart Rate Reserve")
    private String methodDisplayName;

    @Schema(
            description = "Method description",
            example = "Calculates target training zones using Heart Rate Reserve (HRR = HRmax - HRrest)")
    private String methodDescription;

    @Schema(description = "Heart rate reserve (maxHr - restingHr) in bpm", example = "130")
    private Integer heartRateReserve;

    @Schema(description = "Cardiovascular training zones (Zone 1 to Zone 5)")
    private List<CalculatedHeartRateZoneDto> zones;
}
