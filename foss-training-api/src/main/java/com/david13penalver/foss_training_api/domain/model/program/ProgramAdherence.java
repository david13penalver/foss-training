package com.david13penalver.foss_training_api.domain.model.program;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProgramAdherence {

    private Integer programId;
    private String programName;
    private int durationWeeks;
    private int totalScheduledWorkouts;
    private int completedWorkouts;
    private int inProgressWorkouts;
    private int plannedWorkouts;
    private int missedWorkouts;
    private int cancelledWorkouts;
    private double overallCompletionRate; // completed / totalScheduled * 100
    private double currentAdherenceRate;   // completed / (completed + missed + cancelled) * 100
    private int currentStreak;
    private int longestStreak;
    private ProgramAdherenceStatus status;
    private String statusDescription;
    private List<WeeklyAdherence> weeklyBreakdowns;
    private List<WorkoutAdherenceItem> workoutDetails;
}
