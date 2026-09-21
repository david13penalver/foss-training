package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.session;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "CloneSessionRequest", description = "Optional customization parameters for cloning a session template")
public class CloneSessionRequestDto {

    @Schema(description = "Optional custom name for the cloned session template", example = "Upper Body Strength - Variation B")
    private String name;
}
