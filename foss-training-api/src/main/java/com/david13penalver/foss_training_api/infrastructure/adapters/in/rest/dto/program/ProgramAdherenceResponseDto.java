package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.program;

import java.util.List;

import com.david13penalver.foss_training_api.domain.model.program.ProgramAdherenceStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ProgramAdherenceResponse", description = "Comprehensive adherence and compliance metrics for a training program")
public class ProgramAdherenceResponseDto {

    @Schema(description = "Training program ID", example = "1")
    private Integer programId;

    @Schema(description = "Training program name", example = "12-Week Hypertrophy Mesocycle")
    private String programName;

    @Schema(description = "Program duration in weeks", example = "12")
    private int durationWeeks;

    @Schema(description = "Total scheduled workouts across the program duration", example = "48")
    private int totalScheduledWorkouts;

    @Schema(description = "Number of workouts successfully completed", example = "24")
    private int completedWorkouts;

    @Schema(description = "Number of workouts currently in progress or paused", example = "1")
    private int inProgressWorkouts;

    @Schema(description = "Number of planned upcoming workouts", example = "22")
    private int plannedWorkouts;

    @Schema(description = "Number of missed workouts", example = "1")
    private int missedWorkouts;

    @Schema(description = "Number of cancelled workouts", example = "0")
    private int cancelledWorkouts;

    @Schema(description = "Overall program completion rate percentage (completed / total scheduled * 100)", example = "50.0")
    private double overallCompletionRate;

    @Schema(description = "Current adherence rate percentage against elapsed workouts (completed / expected elapsed * 100)", example = "96.0")
    private double currentAdherenceRate;

    @Schema(description = "Current active consecutive completed workout streak", example = "5")
    private int currentStreak;

    @Schema(description = "Longest consecutive completed workout streak achieved", example = "12")
    private int longestStreak;

    @Schema(description = "Current program adherence status level", example = "ON_TRACK")
    private ProgramAdherenceStatus status;

    @Schema(description = "Sports science interpretation and recommendation for adherence status", example = "Excellent adherence to program schedule. Consistency is optimal for athletic adaptations.")
    private String statusDescription;

    @Schema(description = "Week-by-week adherence metrics")
    private List<WeeklyAdherenceDto> weeklyBreakdowns;

    @Schema(description = "Workout-level compliance tracking history")
    private List<WorkoutAdherenceItemDto> workoutDetails;
}
