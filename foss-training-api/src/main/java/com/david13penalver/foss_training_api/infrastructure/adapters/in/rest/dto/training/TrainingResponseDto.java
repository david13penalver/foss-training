package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.training;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.david13penalver.foss_training_api.domain.model.training.TrainingStatusEnum;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.common.DurationDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.common.RpeDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.session.SessionResponseDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "Training")
public class TrainingResponseDto {

    private Integer id;
    private String name;
    private String description;
    private SessionResponseDto session;
    private LocalDate trainingDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private DurationDto duration;
    private Double totalVolume;
    private TrainingStatusEnum status;
    private String notes;
    private RpeDto rpe;

}
