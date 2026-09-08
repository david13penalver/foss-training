package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.program;

import java.util.List;

import com.david13penalver.foss_training_api.domain.model.program.PeriodizationType;
import com.david13penalver.foss_training_api.domain.model.program.ProgramLevel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "TrainingProgramResponse")
public class TrainingProgramResponseDto {

    private Integer id;
    private String name;
    private String description;
    private int durationWeeks;
    private PeriodizationType periodizationType;
    private ProgramLevel level;
    private List<ProgramWorkoutResponseDto> workouts;
    private Boolean isActive;
}
