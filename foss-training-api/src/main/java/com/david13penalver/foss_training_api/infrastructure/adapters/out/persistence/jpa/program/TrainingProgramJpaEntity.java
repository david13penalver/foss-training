package com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.program;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "training_programs")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrainingProgramJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(length = 65535)
    private String description;

    @Column(name = "duration_weeks", nullable = false)
    private int durationWeeks;

    @Column(name = "periodization_type")
    private String periodizationType;

    private String level;

    @Column(name = "workouts_json", length = 65535)
    private String workoutsJson;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;
}
