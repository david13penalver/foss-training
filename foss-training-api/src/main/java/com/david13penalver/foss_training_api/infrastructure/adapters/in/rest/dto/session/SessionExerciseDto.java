package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.session;

import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.exercise.ExerciseResponseDto;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "exerciseType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ResistanceSessionExerciseDto.class, name = "resistance"),
        @JsonSubTypes.Type(value = EnduranceSessionExerciseDto.class, name = "endurance"),
        @JsonSubTypes.Type(value = MobilitySessionExerciseDto.class, name = "mobility")
})
@Schema(name = "SessionExercise")
public abstract class SessionExerciseDto {

    private Integer id;
    private ExerciseResponseDto exercise;
    private Integer orderIndex;
    private String notes;

}
