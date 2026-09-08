package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.program;

import java.util.List;

import com.david13penalver.foss_training_api.domain.model.program.PeriodizationType;
import com.david13penalver.foss_training_api.domain.model.program.ProgramLevel;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "TrainingProgramRequest")
public class TrainingProgramRequestDto {

    private Integer id;

    @NotBlank(message = "Program name cannot be blank")
    @Schema(description = "Program title", example = "12-Week Push Pull Legs")
    private String name;

    @Schema(description = "Program description and training objectives")
    private String description;

    @Min(value = 1, message = "Duration must be at least 1 week")
    @Schema(description = "Program duration in weeks", example = "12")
    private int durationWeeks;

    @Schema(description = "Periodization methodology", example = "LINEAR")
    private PeriodizationType periodizationType;

    @Schema(description = "Target athlete experience level", example = "INTERMEDIATE")
    private ProgramLevel level;

    @Schema(description = "List of scheduled workout days in a microcycle")
    private List<@Valid ProgramWorkoutRequestDto> workouts;

    @Schema(description = "Whether the program is active and available", example = "true")
    private Boolean isActive;
}
