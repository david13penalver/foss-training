package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.program;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "CloneProgramRequest", description = "Optional customization parameters for cloning a training program")
public class CloneProgramRequestDto {

    @Schema(description = "Optional custom name for the cloned program", example = "12-Week Hypertrophy Mesocycle (Cycle 2)")
    private String name;
}
