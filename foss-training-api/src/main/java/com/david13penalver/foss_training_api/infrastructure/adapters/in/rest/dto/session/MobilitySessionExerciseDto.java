package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.session;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Schema(name = "MobilitySessionExercise")
public class MobilitySessionExerciseDto extends SessionExerciseDto {

    private List<MobilitySetDto> sets = new ArrayList<>();

}
