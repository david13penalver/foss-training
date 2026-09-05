package com.david13penalver.foss_training_api.domain.model.session;

import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "exerciseType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ResistanceSessionExercise.class, name = "resistance"),
        @JsonSubTypes.Type(value = EnduranceSessionExercise.class, name = "endurance"),
        @JsonSubTypes.Type(value = MobilitySessionExercise.class, name = "mobility")
})
public abstract class SessionExercise {

    private Integer id;
    private Exercise exercise;
    private Integer orderIndex;
    private String notes;

}