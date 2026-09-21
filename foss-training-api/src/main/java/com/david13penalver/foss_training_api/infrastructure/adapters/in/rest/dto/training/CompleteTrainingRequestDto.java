package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.training;

import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.common.RpeDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "CompleteTrainingRequest", description = "Optional post-workout rating and summary notes")
public class CompleteTrainingRequestDto {

    @Valid
    @Schema(description = "Overall session Rate of Perceived Exertion (RPE 1-10)")
    private RpeDto rpe;

    @Schema(description = "Post-workout athlete notes or subjective reflections", example = "Hit all target reps, energy was high!")
    private String notes;
}
