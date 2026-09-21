package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.export;

import java.time.LocalDate;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.david13penalver.foss_training_api.application.usecases.export.ExportBackupUseCase;
import com.david13penalver.foss_training_api.application.usecases.export.ExportWorkoutsCsvUseCase;
import com.david13penalver.foss_training_api.application.usecases.export.ImportBackupUseCase;
import com.david13penalver.foss_training_api.application.usecases.export.ImportWorkoutsCsvUseCase;
import com.david13penalver.foss_training_api.domain.model.export.FullBackupData;
import com.david13penalver.foss_training_api.domain.model.export.ImportSummary;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.export.DataPortabilityDtoMapper;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.export.FullBackupDataDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.export.ImportSummaryDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
@Tag(name = "Data Portability", description = "Complete JSON backup export/restore and CSV workout data sovereignty")
public class DataPortabilityRestController {

    private final ExportBackupUseCase exportBackupUseCase;
    private final ExportWorkoutsCsvUseCase exportWorkoutsCsvUseCase;
    private final ImportBackupUseCase importBackupUseCase;
    private final ImportWorkoutsCsvUseCase importWorkoutsCsvUseCase;
    private final DataPortabilityDtoMapper mapper;

    @GetMapping("/export/backup")
    @Operation(summary = "Export complete database snapshot as JSON file")
    public ResponseEntity<FullBackupDataDto> exportBackup() {
        FullBackupData data = exportBackupUseCase.execute();
        FullBackupDataDto dto = mapper.toResponseDto(data);

        String filename = "foss-training-backup-" + LocalDate.now() + ".json";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto);
    }

    @GetMapping(value = "/export/workouts.csv", produces = "text/csv")
    @Operation(summary = "Export all workout logs and exercise sets as CSV")
    public ResponseEntity<String> exportWorkoutsCsv() {
        String csv = exportWorkoutsCsvUseCase.execute();
        String filename = "foss-training-workouts-" + LocalDate.now() + ".csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8")
                .body(csv);
    }

    @PostMapping("/import/backup")
    @Operation(summary = "Restore database entities from full JSON backup snapshot")
    public ResponseEntity<ImportSummaryDto> importBackup(@RequestBody FullBackupDataDto backupDto) {
        FullBackupData domain = mapper.toDomain(backupDto);
        ImportSummary summary = importBackupUseCase.execute(domain);
        return ResponseEntity.ok(mapper.toSummaryDto(summary));
    }

    @PostMapping(value = "/import/workouts.csv", consumes = {"text/csv", "text/plain", "*/*"})
    @Operation(summary = "Import workout execution logs from CSV format")
    public ResponseEntity<ImportSummaryDto> importWorkoutsCsv(@RequestBody String csvContent) {
        ImportSummary summary = importWorkoutsCsvUseCase.execute(csvContent);
        return ResponseEntity.ok(mapper.toSummaryDto(summary));
    }
}
