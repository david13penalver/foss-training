package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.athlete;

import java.time.LocalDate;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "BodyweightResponse", description = "Logged bodyweight entry record")
public class BodyweightResponseDto {

    @Schema(description = "Unique record ID", example = "1")
    private Integer id;

    @Schema(description = "Date of measurement", example = "2026-09-21")
    private LocalDate entryDate;

    @Schema(description = "Athlete weight in kilograms", example = "82.5")
    private Double weightKg;

    @Schema(description = "Body fat percentage estimate", example = "14.2")
    private Double bodyFatPercentage;

    @Schema(description = "Measurement context or notes", example = "Morning weigh-in")
    private String notes;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;
}
