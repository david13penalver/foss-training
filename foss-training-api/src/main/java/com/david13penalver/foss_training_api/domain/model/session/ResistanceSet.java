package com.david13penalver.foss_training_api.domain.model.session;

import com.david13penalver.foss_training_api.domain.model.common.Rpe;
import com.david13penalver.foss_training_api.domain.model.common.Weight;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResistanceSet {

    private Integer setNumber;
    private SetType setType;
    private Weight weight;
    private Integer repetitions;
    private Rpe rpe;
    private Integer restSeconds;

    /**
     * Calculates volume for this set (weight in kg × reps).
     * Only working sets (non-warmup) typically count towards training volume.
     *
     * @return volume in kg, or 0.0 if weight or reps are null
     */
    public double calculateVolume() {
        if (weight == null || repetitions == null) {
            return 0.0;
        }
        return weight.toKg().getValue() * repetitions;
    }

    /**
     * @return true if this set counts as effective training volume (non-warmup)
     */
    public boolean isWorkingVolume() {
        return setType != null && setType.countsAsWorkingVolume();
    }
}
