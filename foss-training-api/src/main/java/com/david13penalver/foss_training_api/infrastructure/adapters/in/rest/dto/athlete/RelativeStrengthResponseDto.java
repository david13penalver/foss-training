package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.athlete;

import com.david13penalver.foss_training_api.domain.model.athlete.Gender;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "RelativeStrengthResponse", description = "Calculated relative strength scores, DOTS and Wilks points")
public class RelativeStrengthResponseDto {

    @Schema(description = "Total weight lifted in kg", example = "500.0")
    private Double totalWeightKg;

    @Schema(description = "Athlete bodyweight in kg", example = "80.0")
    private Double bodyweightKg;

    @Schema(description = "Athlete gender", example = "MALE")
    private Gender gender;

    @Schema(description = "Bodyweight ratio (total / bodyweight)", example = "6.25")
    private Double ratio;

    @Schema(description = "DOTS formula score", example = "356.18")
    private Double dots;

    @Schema(description = "Wilks formula score", example = "342.91")
    private Double wilks;

    @Schema(description = "Powerlifting strength tier classification", example = "Proficient")
    private String classification;
}
