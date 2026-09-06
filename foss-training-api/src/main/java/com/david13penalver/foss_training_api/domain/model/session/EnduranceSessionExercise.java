package com.david13penalver.foss_training_api.domain.model.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.david13penalver.foss_training_api.domain.model.common.Distance;
import com.david13penalver.foss_training_api.domain.model.common.DistanceUnit;
import com.david13penalver.foss_training_api.domain.model.common.Duration;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class EnduranceSessionExercise extends SessionExercise {

    private List<EnduranceInterval> intervals = new ArrayList<>();

    /**
     * Adds an interval and assigns its interval number automatically.
     */
    public void addInterval(EnduranceInterval interval) {
        if (interval.getIntervalNumber() == null) {
            interval.setIntervalNumber(intervals.size() + 1);
        }
        intervals.add(interval);
    }

    /**
     * Returns an unmodifiable view of the intervals.
     */
    public List<EnduranceInterval> getIntervals() {
        return Collections.unmodifiableList(intervals);
    }

    /**
     * Calculates total distance across all intervals.
     *
     * @return total distance in meters, or zero if no intervals have distance data
     */
    public Distance calculateTotalDistance() {
        Distance total = Distance.zero(DistanceUnit.METERS);
        for (EnduranceInterval interval : intervals) {
            if (interval.getDistance() != null) {
                total = total.plus(interval.getDistance());
            }
        }
        return total;
    }

    /**
     * Calculates total active duration across all intervals (excluding rest).
     *
     * @return total active duration
     */
    public Duration calculateTotalDuration() {
        int totalSeconds = intervals.stream()
                .filter(i -> i.getDuration() != null)
                .mapToInt(i -> i.getDuration().getTotalSeconds())
                .sum();
        return Duration.seconds(totalSeconds);
    }

    /**
     * Returns the total number of intervals.
     */
    public int getIntervalsCount() {
        return intervals.size();
    }

    /**
     * Calculates the average heart rate across all intervals that have HR data.
     *
     * @return average heart rate, or 0 if no HR data
     */
    public int calculateAverageHeartRate() {
        return (int) intervals.stream()
                .filter(i -> i.getAvgHeartRate() != null)
                .mapToInt(EnduranceInterval::getAvgHeartRate)
                .average()
                .orElse(0);
    }
}
