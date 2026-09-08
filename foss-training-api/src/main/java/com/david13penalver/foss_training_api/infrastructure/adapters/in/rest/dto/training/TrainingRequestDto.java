package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.training;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.david13penalver.foss_training_api.domain.model.training.TrainingStatusEnum;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.common.RpeDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.session.SessionRequestDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "TrainingRequest")
public class TrainingRequestDto {

    private Integer id;

    @NotBlank(message = "Training name cannot be blank")
    private String name;

    private String description;

    @NotNull(message = "Training session cannot be null")
    @Valid
    private SessionRequestDto session;

    @NotNull(message = "Training date cannot be null")
    private LocalDate trainingDate;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private TrainingStatusEnum status;
    private String notes;
    private RpeDto rpe;

}
