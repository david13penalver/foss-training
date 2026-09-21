package com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.training;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
@Table(name = "trainings")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrainingJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(length = 65535)
    private String description;

    @Column(name = "training_date")
    private LocalDate trainingDate;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(nullable = false)
    private String status;

    @Column(length = 65535)
    private String notes;

    @Column(name = "rpe_value")
    private Double rpeValue;

    @Column(name = "session_json", length = 65535)
    private String sessionJson;

    @Column(name = "program_id")
    private Integer programId;
}
