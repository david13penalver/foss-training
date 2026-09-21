package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.program;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.david13penalver.foss_training_api.application.usecases.program.CloneTrainingProgramUseCase;
import com.david13penalver.foss_training_api.application.usecases.program.DeleteTrainingProgramUseCase;
import com.david13penalver.foss_training_api.application.usecases.program.FindAllTrainingProgramsUseCase;
import com.david13penalver.foss_training_api.application.usecases.program.FindTrainingProgramByIdUseCase;
import com.david13penalver.foss_training_api.application.usecases.program.GenerateProgramScheduleUseCase;
import com.david13penalver.foss_training_api.application.usecases.program.GetProgramAdherenceUseCase;
import com.david13penalver.foss_training_api.application.usecases.program.SaveTrainingProgramUseCase;
import com.david13penalver.foss_training_api.application.usecases.program.TrainingProgramExistsUseCase;
import com.david13penalver.foss_training_api.domain.model.program.ProgramAdherence;
import com.david13penalver.foss_training_api.domain.model.program.TrainingProgram;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.program.CloneProgramRequestDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.program.ProgramAdherenceResponseDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.program.TrainingProgramDtoMapper;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.program.TrainingProgramRequestDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.program.TrainingProgramResponseDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.training.TrainingDtoMapper;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.training.TrainingResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/programs")
@RequiredArgsConstructor
@Tag(name = "Training Programs", description = "Periodization and multi-week training program templates")
public class TrainingProgramRestController {

    private final FindAllTrainingProgramsUseCase findAllTrainingProgramsUseCase;
    private final FindTrainingProgramByIdUseCase findTrainingProgramByIdUseCase;
    private final SaveTrainingProgramUseCase saveTrainingProgramUseCase;
    private final DeleteTrainingProgramUseCase deleteTrainingProgramUseCase;
    private final TrainingProgramExistsUseCase trainingProgramExistsUseCase;
    private final GenerateProgramScheduleUseCase generateProgramScheduleUseCase;
    private final CloneTrainingProgramUseCase cloneTrainingProgramUseCase;
    private final GetProgramAdherenceUseCase getProgramAdherenceUseCase;
    private final TrainingProgramDtoMapper trainingProgramDtoMapper;
    private final TrainingDtoMapper trainingDtoMapper;

    @GetMapping
    @Operation(summary = "Get all training programs")
    public ResponseEntity<List<TrainingProgramResponseDto>> getAllPrograms() {
        List<TrainingProgram> programs = findAllTrainingProgramsUseCase.execute();
        return ResponseEntity.ok(trainingProgramDtoMapper.toResponseDtoList(programs));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a training program by ID")
    public ResponseEntity<TrainingProgramResponseDto> getProgramById(@PathVariable Integer id) {
        Optional<TrainingProgram> program = findTrainingProgramByIdUseCase.execute(id);
        return program.map(trainingProgramDtoMapper::toResponseDto)
                      .map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new training program")
    public ResponseEntity<TrainingProgramResponseDto> createProgram(@Valid @RequestBody TrainingProgramRequestDto requestDto) {
        TrainingProgram program = trainingProgramDtoMapper.toEntity(requestDto);
        TrainingProgram savedProgram = saveTrainingProgramUseCase.execute(program);
        URI location = URI.create("/api/programs/" + savedProgram.getId());
        return ResponseEntity.created(location).body(trainingProgramDtoMapper.toResponseDto(savedProgram));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing training program")
    public ResponseEntity<TrainingProgramResponseDto> updateProgram(@PathVariable Integer id, @Valid @RequestBody TrainingProgramRequestDto requestDto) {
        if (!trainingProgramExistsUseCase.execute(id)) {
            return ResponseEntity.notFound().build();
        }
        requestDto.setId(id);
        TrainingProgram program = trainingProgramDtoMapper.toEntity(requestDto);
        TrainingProgram savedProgram = saveTrainingProgramUseCase.execute(program);
        return ResponseEntity.ok(trainingProgramDtoMapper.toResponseDto(savedProgram));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a training program by ID")
    public ResponseEntity<Void> deleteProgram(@PathVariable Integer id) {
        if (!trainingProgramExistsUseCase.execute(id)) {
            return ResponseEntity.notFound().build();
        }
        deleteTrainingProgramUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/exists")
    @Operation(summary = "Check if a training program exists by ID")
    public ResponseEntity<Boolean> programExists(@PathVariable Integer id) {
        boolean exists = trainingProgramExistsUseCase.execute(id);
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/{id}/generate-schedule")
    @Operation(summary = "Generate scheduled calendar workouts across the program duration")
    public ResponseEntity<List<TrainingResponseDto>> generateSchedule(
            @PathVariable Integer id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {

        if (!trainingProgramExistsUseCase.execute(id)) {
            return ResponseEntity.notFound().build();
        }
        List<Training> trainings = generateProgramScheduleUseCase.execute(id, startDate);
        return ResponseEntity.status(HttpStatus.CREATED).body(trainingDtoMapper.toResponseDtoList(trainings));
    }

    @PostMapping("/{id}/clone")
    @Operation(summary = "Clone an existing multi-week training program")
    public ResponseEntity<TrainingProgramResponseDto> cloneProgram(
            @PathVariable Integer id,
            @RequestBody(required = false) CloneProgramRequestDto requestDto) {

        if (!trainingProgramExistsUseCase.execute(id)) {
            return ResponseEntity.notFound().build();
        }
        String customName = (requestDto != null) ? requestDto.getName() : null;
        TrainingProgram cloned = cloneTrainingProgramUseCase.execute(id, customName);
        URI location = URI.create("/api/programs/" + cloned.getId());
        return ResponseEntity.created(location).body(trainingProgramDtoMapper.toResponseDto(cloned));
    }

    @GetMapping("/{id}/adherence")
    @Operation(summary = "Get program adherence and compliance tracking metrics")
    public ResponseEntity<ProgramAdherenceResponseDto> getProgramAdherence(@PathVariable Integer id) {
        if (!trainingProgramExistsUseCase.execute(id)) {
            return ResponseEntity.notFound().build();
        }
        ProgramAdherence adherence = getProgramAdherenceUseCase.execute(id);
        return ResponseEntity.ok(trainingProgramDtoMapper.toAdherenceDto(adherence));
    }
}
