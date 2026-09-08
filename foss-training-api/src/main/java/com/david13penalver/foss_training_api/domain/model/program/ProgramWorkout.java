package com.david13penalver.foss_training_api.domain.model.program;

import com.david13penalver.foss_training_api.domain.model.session.Session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProgramWorkout {

    private Integer dayOfWeek; // 1 = Monday, 7 = Sunday
    private String focus;
    private Session session;

    public void validate() {
        if (dayOfWeek == null || dayOfWeek < 1 || dayOfWeek > 7) {
            throw new IllegalArgumentException("Day of week must be between 1 (Monday) and 7 (Sunday)");
        }
        if (session == null) {
            throw new IllegalArgumentException("Workout session template must not be null");
        }
    }
}
