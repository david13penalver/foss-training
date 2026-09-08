package com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.program;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersistenceProgramWorkout {

    private Integer dayOfWeek;
    private String focus;
    private String sessionJson;
}
