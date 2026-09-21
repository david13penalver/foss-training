package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.athlete;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.david13penalver.foss_training_api.application.usecases.athlete.CalculateRelativeStrengthUseCase;
import com.david13penalver.foss_training_api.application.usecases.athlete.DeleteBodyweightUseCase;
import com.david13penalver.foss_training_api.application.usecases.athlete.GetBodyweightHistoryUseCase;
import com.david13penalver.foss_training_api.application.usecases.athlete.GetLatestBodyweightUseCase;
import com.david13penalver.foss_training_api.application.usecases.athlete.LogBodyweightUseCase;
import com.david13penalver.foss_training_api.domain.model.athlete.BodyweightEntry;
import com.david13penalver.foss_training_api.domain.model.athlete.RelativeStrengthScore;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.athlete.AthleteDtoMapper;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.athlete.BodyweightRequestDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.athlete.BodyweightResponseDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.athlete.RelativeStrengthRequestDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.athlete.RelativeStrengthResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/athlete")
@RequiredArgsConstructor
@Tag(name = "Athlete", description = "Athlete bodyweight tracking, anthropometrics, and powerlifting relative strength scoring")
public class AthleteRestController {

    private final LogBodyweightUseCase logBodyweightUseCase;
    private final GetBodyweightHistoryUseCase getBodyweightHistoryUseCase;
    private final GetLatestBodyweightUseCase getLatestBodyweightUseCase;
    private final DeleteBodyweightUseCase deleteBodyweightUseCase;
    private final CalculateRelativeStrengthUseCase calculateRelativeStrengthUseCase;
    private final AthleteDtoMapper athleteDtoMapper;

    @PostMapping("/bodyweight")
    @Operation(summary = "Log or update an athlete bodyweight measurement")
    public ResponseEntity<BodyweightResponseDto> logBodyweight(@Valid @RequestBody BodyweightRequestDto requestDto) {
        BodyweightEntry entry = athleteDtoMapper.toDomain(requestDto);
        BodyweightEntry saved = logBodyweightUseCase.execute(entry);
        return ResponseEntity.status(HttpStatus.CREATED).body(athleteDtoMapper.toResponseDto(saved));
    }

    @GetMapping("/bodyweight/history")
    @Operation(summary = "Get historical bodyweight logs sorted chronologically")
    public ResponseEntity<List<BodyweightResponseDto>> getBodyweightHistory() {
        List<BodyweightEntry> history = getBodyweightHistoryUseCase.execute();
        return ResponseEntity.ok(athleteDtoMapper.toResponseDtoList(history));
    }

    @GetMapping("/bodyweight/latest")
    @Operation(summary = "Get the most recent bodyweight measurement")
    public ResponseEntity<BodyweightResponseDto> getLatestBodyweight() {
        Optional<BodyweightEntry> latest = getLatestBodyweightUseCase.execute();
        return latest.map(athleteDtoMapper::toResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/bodyweight/{id}")
    @Operation(summary = "Delete a bodyweight record by ID")
    public ResponseEntity<Void> deleteBodyweight(@PathVariable Integer id) {
        deleteBodyweightUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/relative-strength")
    @Operation(summary = "Calculate relative strength ratio, DOTS formula, and Wilks score")
    public ResponseEntity<RelativeStrengthResponseDto> calculateRelativeStrength(
            @Valid @RequestBody RelativeStrengthRequestDto requestDto) {
        RelativeStrengthScore score = calculateRelativeStrengthUseCase.execute(
                requestDto.getTotalWeightKg(),
                requestDto.getBodyweightKg(),
                requestDto.getGender()
        );
        return ResponseEntity.ok(athleteDtoMapper.toResponseDto(score));
    }
}
