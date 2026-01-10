package com.david13penalver.foss_training_api.domain.model.session;

import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class SessionExercise {

    private int id;
    private SessionPart sessionPart;
    private Session session;
    private Exercise exercise;
    private String notes;

}
