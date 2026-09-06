package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.session;

import java.time.LocalDateTime;
import java.util.List;

import com.david13penalver.foss_training_api.domain.model.session.SessionStatusEnum;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.common.RpeDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "SessionRequest")
public class SessionRequestDto {

    private Integer id;

    @NotBlank(message = "Session name cannot be blank")
    private String name;

    private String description;
    private SessionStatusEnum sessionStatus;
    private List<@Valid SessionExerciseDto> sessionExercises;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String notes;
    private RpeDto rpe;

}
