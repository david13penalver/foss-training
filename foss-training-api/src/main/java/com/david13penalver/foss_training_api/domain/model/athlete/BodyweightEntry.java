package com.david13penalver.foss_training_api.domain.model.athlete;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BodyweightEntry {

    private Integer id;
    private LocalDate entryDate;
    private Double weightKg;
    private Double bodyFatPercentage;
    private String notes;
    private LocalDateTime createdAt;

    public BodyweightEntry(LocalDate entryDate, Double weightKg, Double bodyFatPercentage, String notes) {
        if (entryDate == null) {
            throw new IllegalArgumentException("Entry date cannot be null");
        }
        if (weightKg == null || weightKg <= 0) {
            throw new IllegalArgumentException("Weight must be positive");
        }
        this.entryDate = entryDate;
        this.weightKg = weightKg;
        this.bodyFatPercentage = bodyFatPercentage;
        this.notes = notes;
        this.createdAt = LocalDateTime.now();
    }
}
