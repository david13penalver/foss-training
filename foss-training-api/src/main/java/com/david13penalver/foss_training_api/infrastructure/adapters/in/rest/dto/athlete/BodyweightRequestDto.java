package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.athlete;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "BodyweightRequest", description = "Bodyweight logging request payload")
public class BodyweightRequestDto {

    @NotNull(message = "Entry date is required")
    @Schema(description = "Date of the bodyweight measurement", example = "2026-09-21")
    private LocalDate entryDate;

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    @Schema(description = "Athlete weight in kilograms", example = "82.5")
    private Double weightKg;

    @PositiveOrZero(message = "Body fat percentage must be zero or positive")
    @Schema(description = "Optional body fat percentage estimate", example = "14.2")
    private Double bodyFatPercentage;

    @Schema(description = "Optional measurement context or notes", example = "Morning weigh-in before breakfast")
    private String notes;
}
