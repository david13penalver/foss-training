package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.analytics;

import com.david13penalver.foss_training_api.domain.model.common.HeartRateZone;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Individual cardiovascular target training zone")
public class CalculatedHeartRateZoneDto {

    @Schema(description = "Cardio zone identifier", example = "ZONE_2")
    private HeartRateZone zone;

    @Schema(description = "Zone number from 1 to 5", example = "2")
    private int zoneNumber;

    @Schema(description = "Display name of the heart rate zone", example = "Aerobic Base")
    private String displayName;

    @Schema(description = "Minimum intensity percentage for this zone", example = "60.0")
    private double minPercentage;

    @Schema(description = "Maximum intensity percentage for this zone", example = "70.0")
    private double maxPercentage;

    @Schema(description = "Lower heart rate bound in beats per minute (bpm)", example = "138")
    private int minBpm;

    @Schema(description = "Upper heart rate bound in beats per minute (bpm)", example = "151")
    private int maxBpm;

    @Schema(description = "Physiological summary of this zone", example = "Builds endurance base, optimizes mitochondrial function")
    private String description;

    @Schema(
            description = "Target adaptations and training guidance for this zone",
            example = "Maximizes fat oxidation and stimulates mitochondrial biogenesis. Core foundation for endurance performance.")
    private String trainingBenefit;
}
