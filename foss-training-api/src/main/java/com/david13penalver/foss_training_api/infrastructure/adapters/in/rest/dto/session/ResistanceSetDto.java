package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.session;

import com.david13penalver.foss_training_api.domain.model.session.SetType;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.common.RpeDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.common.WeightDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ResistanceSet")
public class ResistanceSetDto {

    private Integer setNumber;
    private SetType setType;
    private WeightDto weight;
    private Integer repetitions;
    private RpeDto rpe;
    private Integer restSeconds;

}
