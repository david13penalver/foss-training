package com.david13penalver.foss_training_api.domain.model.session;

import java.util.List;

import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Session {

    private int id;
    private String name;
    private String description;
    private SessionStatusEnum sessionStatus;
    private List<SessionExercise> sessionExercises;

}
