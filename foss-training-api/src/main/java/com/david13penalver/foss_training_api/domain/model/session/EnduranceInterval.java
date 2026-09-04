package com.david13penalver.foss_training_api.domain.model.session;

import com.david13penalver.foss_training_api.domain.model.common.Distance;
import com.david13penalver.foss_training_api.domain.model.common.Duration;
import com.david13penalver.foss_training_api.domain.model.common.HeartRateZone;
import com.david13penalver.foss_training_api.domain.model.common.Pace;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnduranceInterval {

    private Integer intervalNumber;
    private Distance distance;
    private Duration duration;
    private Pace pace;
    private Integer avgHeartRate;
    private Integer maxHeartRate;
    private Double avgPower;
    private Double cadence;
    private Integer restSeconds;

    /**
     * Determines the heart rate zone for this interval given the athlete's max HR.
     *
     * @param maxHr the athlete's maximum heart rate
     * @return the training zone, or null if avgHeartRate is not set
     */
    public HeartRateZone getHeartRateZone(int maxHr) {
        if (avgHeartRate == null) {
            return null;
        }
        return HeartRateZone.fromHeartRate(avgHeartRate, maxHr);
    }
}
