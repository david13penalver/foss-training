package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.athlete;

import com.david13penalver.foss_training_api.domain.model.athlete.Gender;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "RelativeStrengthRequest", description = "Relative strength score calculation input")
public class RelativeStrengthRequestDto {

    @NotNull(message = "Total lifted weight is required")
    @Positive(message = "Total lifted weight must be greater than 0")
    @Schema(description = "Total lifted weight or 1RM in kilograms", example = "500.0")
    private Double totalWeightKg;

    @NotNull(message = "Bodyweight is required")
    @Positive(message = "Bodyweight must be greater than 0")
    @Schema(description = "Athlete bodyweight in kilograms", example = "80.0")
    private Double bodyweightKg;

    @Schema(description = "Athlete biological sex for coefficient formulas", example = "MALE")
    private Gender gender;
}
