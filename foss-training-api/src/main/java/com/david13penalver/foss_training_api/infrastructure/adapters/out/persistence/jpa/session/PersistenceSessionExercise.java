package com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.session;

import java.util.List;

import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.session.EnduranceInterval;
import com.david13penalver.foss_training_api.domain.model.session.MobilitySet;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersistenceSessionExercise {

    private String type; // "RESISTANCE", "ENDURANCE", "MOBILITY"
    private Integer id;
    private Exercise exercise;
    private Integer orderIndex;
    private String notes;
    private List<ResistanceSet> resistanceSets;
    private List<EnduranceInterval> enduranceIntervals;
    private List<MobilitySet> mobilitySets;
}
