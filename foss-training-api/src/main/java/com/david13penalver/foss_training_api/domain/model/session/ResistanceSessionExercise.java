package com.david13penalver.foss_training_api.domain.model.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.david13penalver.foss_training_api.domain.model.common.Weight;
import com.david13penalver.foss_training_api.domain.model.common.WeightUnit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ResistanceSessionExercise extends SessionExercise {

    private List<ResistanceSet> sets = new ArrayList<>();

    /**
     * Adds a set and assigns its set number automatically.
     */
    public void addSet(ResistanceSet set) {
        if (set.getSetNumber() == null) {
            set.setSetNumber(sets.size() + 1);
        }
        sets.add(set);
    }

    /**
     * Logs or updates a set. If setNumber is provided and exists, updates it.
     * Otherwise assigns the next sequential set number and appends it.
     */
    public void logSet(ResistanceSet set) {
        if (set == null) {
            return;
        }
        if (set.getCompleted() == null) {
            set.setCompleted(true);
        }
        if (set.getSetNumber() == null || set.getSetNumber() <= 0) {
            set.setSetNumber(sets.size() + 1);
            sets.add(set);
            return;
        }
        for (int i = 0; i < sets.size(); i++) {
            if (sets.get(i).getSetNumber() != null && sets.get(i).getSetNumber().equals(set.getSetNumber())) {
                sets.set(i, set);
                return;
            }
        }
        sets.add(set);
    }

    /**
     * Removes a set by set number and re-indexes remaining sets sequentially.
     */
    public boolean removeSet(int setNumber) {
        boolean removed = sets.removeIf(s -> s.getSetNumber() != null && s.getSetNumber() == setNumber);
        if (removed) {
            for (int i = 0; i < sets.size(); i++) {
                sets.get(i).setSetNumber(i + 1);
            }
        }
        return removed;
    }

    /**
     * Returns an unmodifiable view of the sets.
     */
    public List<ResistanceSet> getSets() {
        return Collections.unmodifiableList(sets);
    }

    /**
     * Returns the total number of sets.
     */
    public int getTotalSetsCount() {
        return sets.size();
    }

    /**
     * Returns the number of working (non-warmup) sets.
     */
    public int getWorkingSetsCount() {
        return (int) sets.stream()
                .filter(ResistanceSet::isWorkingVolume)
                .count();
    }

    /**
     * Calculates total training volume (sum of weight × reps) across all working sets (in kg).
     *
     * @return total volume in kg
     */
    public double calculateVolume() {
        return sets.stream()
                .filter(ResistanceSet::isWorkingVolume)
                .mapToDouble(ResistanceSet::calculateVolume)
                .sum();
    }

    /**
     * Returns the heaviest weight used across all sets.
     *
     * @return the heaviest Weight, or Weight.zero(KG) if no sets
     */
    public Weight getHeaviestWeight() {
        return sets.stream()
                .map(ResistanceSet::getWeight)
                .filter(w -> w != null)
                .max(Weight::compareTo)
                .orElse(Weight.zero(WeightUnit.KG));
    }

    /**
     * Returns the total number of repetitions across all working sets.
     */
    public int getTotalReps() {
        return sets.stream()
                .filter(ResistanceSet::isWorkingVolume)
                .mapToInt(s -> s.getRepetitions() != null ? s.getRepetitions() : 0)
                .sum();
    }
}
