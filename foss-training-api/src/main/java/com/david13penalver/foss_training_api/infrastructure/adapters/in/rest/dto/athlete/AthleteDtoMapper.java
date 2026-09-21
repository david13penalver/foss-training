package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.athlete;

import java.util.List;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.domain.model.athlete.BodyweightEntry;
import com.david13penalver.foss_training_api.domain.model.athlete.RelativeStrengthScore;

@Component
public class AthleteDtoMapper {

    public BodyweightEntry toDomain(BodyweightRequestDto dto) {
        if (dto == null) {
            return null;
        }
        return BodyweightEntry.builder()
                .entryDate(dto.getEntryDate())
                .weightKg(dto.getWeightKg())
                .bodyFatPercentage(dto.getBodyFatPercentage())
                .notes(dto.getNotes())
                .build();
    }

    public BodyweightEntry toDomain(BodyweightResponseDto dto) {
        if (dto == null) {
            return null;
        }
        return BodyweightEntry.builder()
                .id(dto.getId())
                .entryDate(dto.getEntryDate())
                .weightKg(dto.getWeightKg())
                .bodyFatPercentage(dto.getBodyFatPercentage())
                .notes(dto.getNotes())
                .createdAt(dto.getCreatedAt())
                .build();
    }

    public BodyweightResponseDto toResponseDto(BodyweightEntry domain) {
        if (domain == null) {
            return null;
        }
        return BodyweightResponseDto.builder()
                .id(domain.getId())
                .entryDate(domain.getEntryDate())
                .weightKg(domain.getWeightKg())
                .bodyFatPercentage(domain.getBodyFatPercentage())
                .notes(domain.getNotes())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    public List<BodyweightResponseDto> toResponseDtoList(List<BodyweightEntry> list) {
        if (list == null) {
            return List.of();
        }
        return list.stream().map(this::toResponseDto).toList();
    }

    public RelativeStrengthResponseDto toResponseDto(RelativeStrengthScore score) {
        if (score == null) {
            return null;
        }
        return RelativeStrengthResponseDto.builder()
                .totalWeightKg(score.totalWeightKg())
                .bodyweightKg(score.bodyweightKg())
                .gender(score.gender())
                .ratio(score.relativeStrengthRatio())
                .dots(score.dotsScore())
                .wilks(score.wilksScore())
                .classification(score.classification())
                .build();
    }
}
