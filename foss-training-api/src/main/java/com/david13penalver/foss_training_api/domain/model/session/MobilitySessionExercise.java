package com.david13penalver.foss_training_api.domain.model.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.david13penalver.foss_training_api.domain.model.common.Duration;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class MobilitySessionExercise extends SessionExercise {

    private List<MobilitySet> sets = new ArrayList<>();

    /**
     * Adds a set and assigns its set number automatically.
     */
    public void addSet(MobilitySet set) {
        if (set.getSetNumber() == null) {
            set.setSetNumber(sets.size() + 1);
        }
        sets.add(set);
    }

    /**
     * Returns an unmodifiable view of the sets.
     */
    public List<MobilitySet> getSets() {
        return Collections.unmodifiableList(sets);
    }

    /**
     * Returns the total number of sets.
     */
    public int getTotalSetsCount() {
        return sets.size();
    }

    /**
     * Calculates total time under stretch/hold across all sets.
     *
     * @return total hold duration
     */
    public Duration calculateTotalHoldDuration() {
        int totalSeconds = sets.stream()
                .filter(s -> s.getHoldDuration() != null)
                .mapToInt(s -> {
                    int holdSecs = s.getHoldDuration().getTotalSeconds();
                    int reps = s.getRepetitions() != null ? s.getRepetitions() : 1;
                    int sides = s.isBilateral() ? 2 : 1;
                    return holdSecs * reps * sides;
                })
                .sum();
        return Duration.seconds(totalSeconds);
    }
}
