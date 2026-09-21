package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.training;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;
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

import com.david13penalver.foss_training_api.application.usecases.session.SessionExistsUseCase;
import com.david13penalver.foss_training_api.application.usecases.training.CancelTrainingUseCase;
import com.david13penalver.foss_training_api.application.usecases.training.CompleteTrainingUseCase;
import com.david13penalver.foss_training_api.application.usecases.training.CreateTrainingFromSessionUseCase;
import com.david13penalver.foss_training_api.application.usecases.training.DeleteTrainingIntervalUseCase;
import com.david13penalver.foss_training_api.application.usecases.training.DeleteTrainingSetUseCase;
import com.david13penalver.foss_training_api.application.usecases.training.DeleteTrainingUseCase;
import com.david13penalver.foss_training_api.application.usecases.training.FindAllTrainingsUseCase;
import com.david13penalver.foss_training_api.application.usecases.training.FindTrainingByIdUseCase;
import com.david13penalver.foss_training_api.application.usecases.training.GetWorkoutSummaryUseCase;
import com.david13penalver.foss_training_api.application.usecases.training.LogTrainingIntervalUseCase;
import com.david13penalver.foss_training_api.application.usecases.training.LogTrainingSetUseCase;
import com.david13penalver.foss_training_api.application.usecases.training.PauseTrainingUseCase;
import com.david13penalver.foss_training_api.application.usecases.training.ResumeTrainingUseCase;
import com.david13penalver.foss_training_api.application.usecases.training.SaveTrainingUseCase;
import com.david13penalver.foss_training_api.application.usecases.training.StartTrainingUseCase;
import com.david13penalver.foss_training_api.application.usecases.training.TrainingExistsUseCase;
import com.david13penalver.foss_training_api.domain.model.session.EnduranceInterval;
import com.david13penalver.foss_training_api.domain.model.session.ResistanceSet;
import com.david13penalver.foss_training_api.domain.model.training.Training;
import com.david13penalver.foss_training_api.domain.model.training.WorkoutSummary;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.training.CompleteTrainingRequestDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.training.LogIntervalRequestDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.training.LogSetRequestDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.training.TrainingDtoMapper;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.training.TrainingRequestDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.training.TrainingResponseDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.training.WorkoutSummaryResponseDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/trainings")
@RequiredArgsConstructor
public class TrainingRestController {

    private final FindAllTrainingsUseCase findAllTrainingsUseCase;
    private final FindTrainingByIdUseCase findTrainingByIdUseCase;
    private final SaveTrainingUseCase saveTrainingUseCase;
    private final DeleteTrainingUseCase deleteTrainingUseCase;
    private final TrainingExistsUseCase trainingExistsUseCase;
    private final StartTrainingUseCase startTrainingUseCase;
    private final PauseTrainingUseCase pauseTrainingUseCase;
    private final ResumeTrainingUseCase resumeTrainingUseCase;
    private final CompleteTrainingUseCase completeTrainingUseCase;
    private final CancelTrainingUseCase cancelTrainingUseCase;
    private final CreateTrainingFromSessionUseCase createTrainingFromSessionUseCase;
    private final SessionExistsUseCase sessionExistsUseCase;
    private final LogTrainingSetUseCase logTrainingSetUseCase;
    private final DeleteTrainingSetUseCase deleteTrainingSetUseCase;
    private final LogTrainingIntervalUseCase logTrainingIntervalUseCase;
    private final DeleteTrainingIntervalUseCase deleteTrainingIntervalUseCase;
    private final GetWorkoutSummaryUseCase getWorkoutSummaryUseCase;
    private final com.david13penalver.foss_training_api.application.usecases.training.SearchTrainingsUseCase searchTrainingsUseCase;
    private final TrainingDtoMapper trainingDtoMapper;

    @GetMapping
    public ResponseEntity<List<TrainingResponseDto>> getAllTrainings(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(required = false) Integer programId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDirection) {

        String query = (search != null && !search.isBlank()) ? search : q;
        com.david13penalver.foss_training_api.domain.model.training.TrainingStatusEnum statusEnum =
                (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status))
                ? com.david13penalver.foss_training_api.domain.model.training.TrainingStatusEnum.fromString(status)
                : null;

        boolean hasFilters = startDate != null || endDate != null || statusEnum != null || query != null || programId != null;
        boolean hasPagination = page != null || size != null;

        if (!hasFilters && !hasPagination) {
            List<Training> trainings = findAllTrainingsUseCase.execute();
            return ResponseEntity.ok(trainingDtoMapper.toResponseDtoList(trainings));
        }

        com.david13penalver.foss_training_api.domain.model.training.TrainingSearchCriteria criteria =
                new com.david13penalver.foss_training_api.domain.model.training.TrainingSearchCriteria(
                        startDate,
                        endDate,
                        statusEnum,
                        query,
                        programId
                );

        if (hasPagination) {
            int pageIndex = page != null ? Math.max(0, page) : 0;
            int pageSize = size != null && size > 0 ? size : 20;
            com.david13penalver.foss_training_api.domain.model.common.PageQuery pageQuery =
                    com.david13penalver.foss_training_api.domain.model.common.PageQuery.of(pageIndex, pageSize, sortBy, sortDirection);
            com.david13penalver.foss_training_api.domain.model.common.PagedResult<Training> pagedResult =
                    searchTrainingsUseCase.execute(criteria, pageQuery);

            return ResponseEntity.ok()
                    .header("X-Total-Count", String.valueOf(pagedResult.totalElements()))
                    .header("X-Total-Pages", String.valueOf(pagedResult.totalPages()))
                    .header("X-Page-Number", String.valueOf(pagedResult.page()))
                    .header("X-Page-Size", String.valueOf(pagedResult.size()))
                    .body(trainingDtoMapper.toResponseDtoList(pagedResult.content()));
        }

        List<Training> trainings = searchTrainingsUseCase.execute(criteria);
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(trainings.size()))
                .body(trainingDtoMapper.toResponseDtoList(trainings));
    }

    @GetMapping("/search")
    public ResponseEntity<com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.common.PageResponseDto<TrainingResponseDto>> searchTrainings(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(required = false) Integer programId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDirection) {

        String query = (search != null && !search.isBlank()) ? search : q;
        com.david13penalver.foss_training_api.domain.model.training.TrainingStatusEnum statusEnum =
                (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status))
                ? com.david13penalver.foss_training_api.domain.model.training.TrainingStatusEnum.fromString(status)
                : null;

        com.david13penalver.foss_training_api.domain.model.training.TrainingSearchCriteria criteria =
                new com.david13penalver.foss_training_api.domain.model.training.TrainingSearchCriteria(
                        startDate,
                        endDate,
                        statusEnum,
                        query,
                        programId
                );
        com.david13penalver.foss_training_api.domain.model.common.PageQuery pageQuery =
                com.david13penalver.foss_training_api.domain.model.common.PageQuery.of(page, size, sortBy, sortDirection);
        com.david13penalver.foss_training_api.domain.model.common.PagedResult<Training> pagedResult =
                searchTrainingsUseCase.execute(criteria, pageQuery);

        return ResponseEntity.ok(trainingDtoMapper.toPageResponseDto(pagedResult));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainingResponseDto> getTrainingById(@PathVariable Integer id) {
        Optional<Training> training = findTrainingByIdUseCase.execute(id);
        return training.map(trainingDtoMapper::toResponseDto)
                       .map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TrainingResponseDto> createTraining(@Valid @RequestBody TrainingRequestDto requestDto) {
        Training training = trainingDtoMapper.toEntity(requestDto);
        Training savedTraining = saveTrainingUseCase.execute(training);
        URI location = URI.create("/api/trainings/" + savedTraining.getId());
        return ResponseEntity.created(location).body(trainingDtoMapper.toResponseDto(savedTraining));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrainingResponseDto> updateTraining(@PathVariable Integer id, @Valid @RequestBody TrainingRequestDto requestDto) {
        if (!trainingExistsUseCase.execute(id)) {
            return ResponseEntity.notFound().build();
        }
        requestDto.setId(id);
        Training training = trainingDtoMapper.toEntity(requestDto);
        Training savedTraining = saveTrainingUseCase.execute(training);
        return ResponseEntity.ok(trainingDtoMapper.toResponseDto(savedTraining));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTraining(@PathVariable Integer id) {
        if (!trainingExistsUseCase.execute(id)) {
            return ResponseEntity.notFound().build();
        }
        deleteTrainingUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> trainingExists(@PathVariable Integer id) {
        boolean exists = trainingExistsUseCase.execute(id);
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<TrainingResponseDto> startTraining(@PathVariable Integer id) {
        if (!trainingExistsUseCase.execute(id)) {
            return ResponseEntity.notFound().build();
        }
        Training startedTraining = startTrainingUseCase.execute(id);
        return ResponseEntity.ok(trainingDtoMapper.toResponseDto(startedTraining));
    }

    @PostMapping("/{id}/pause")
    public ResponseEntity<TrainingResponseDto> pauseTraining(@PathVariable Integer id) {
        if (!trainingExistsUseCase.execute(id)) {
            return ResponseEntity.notFound().build();
        }
        Training pausedTraining = pauseTrainingUseCase.execute(id);
        return ResponseEntity.ok(trainingDtoMapper.toResponseDto(pausedTraining));
    }

    @PostMapping("/{id}/resume")
    public ResponseEntity<TrainingResponseDto> resumeTraining(@PathVariable Integer id) {
        if (!trainingExistsUseCase.execute(id)) {
            return ResponseEntity.notFound().build();
        }
        Training resumedTraining = resumeTrainingUseCase.execute(id);
        return ResponseEntity.ok(trainingDtoMapper.toResponseDto(resumedTraining));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<TrainingResponseDto> completeTraining(
            @PathVariable Integer id,
            @Valid @RequestBody(required = false) CompleteTrainingRequestDto requestDto) {
        if (!trainingExistsUseCase.execute(id)) {
            return ResponseEntity.notFound().build();
        }
        Training completedTraining;
        if (requestDto != null) {
            Double rpeVal = requestDto.getRpe() != null ? requestDto.getRpe().getValue() : null;
            completedTraining = completeTrainingUseCase.execute(id, rpeVal, requestDto.getNotes());
        } else {
            completedTraining = completeTrainingUseCase.execute(id);
        }
        return ResponseEntity.ok(trainingDtoMapper.toResponseDto(completedTraining));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<TrainingResponseDto> cancelTraining(@PathVariable Integer id) {
        if (!trainingExistsUseCase.execute(id)) {
            return ResponseEntity.notFound().build();
        }
        Training cancelledTraining = cancelTrainingUseCase.execute(id);
        return ResponseEntity.ok(trainingDtoMapper.toResponseDto(cancelledTraining));
    }

    @PostMapping("/{id}/exercises/{exerciseId}/sets")
    public ResponseEntity<TrainingResponseDto> logSet(
            @PathVariable Integer id,
            @PathVariable Integer exerciseId,
            @Valid @RequestBody LogSetRequestDto requestDto) {
        if (!trainingExistsUseCase.execute(id)) {
            return ResponseEntity.notFound().build();
        }
        ResistanceSet set = trainingDtoMapper.toSetEntity(requestDto);
        Training updated = logTrainingSetUseCase.execute(id, exerciseId, set);
        return ResponseEntity.ok(trainingDtoMapper.toResponseDto(updated));
    }

    @PutMapping("/{id}/exercises/{exerciseId}/sets/{setNumber}")
    public ResponseEntity<TrainingResponseDto> updateSet(
            @PathVariable Integer id,
            @PathVariable Integer exerciseId,
            @PathVariable Integer setNumber,
            @Valid @RequestBody LogSetRequestDto requestDto) {
        if (!trainingExistsUseCase.execute(id)) {
            return ResponseEntity.notFound().build();
        }
        requestDto.setSetNumber(setNumber);
        ResistanceSet set = trainingDtoMapper.toSetEntity(requestDto);
        Training updated = logTrainingSetUseCase.execute(id, exerciseId, set);
        return ResponseEntity.ok(trainingDtoMapper.toResponseDto(updated));
    }

    @DeleteMapping("/{id}/exercises/{exerciseId}/sets/{setNumber}")
    public ResponseEntity<TrainingResponseDto> deleteSet(
            @PathVariable Integer id,
            @PathVariable Integer exerciseId,
            @PathVariable Integer setNumber) {
        if (!trainingExistsUseCase.execute(id)) {
            return ResponseEntity.notFound().build();
        }
        Training updated = deleteTrainingSetUseCase.execute(id, exerciseId, setNumber);
        return ResponseEntity.ok(trainingDtoMapper.toResponseDto(updated));
    }

    @PostMapping("/{id}/exercises/{exerciseId}/intervals")
    public ResponseEntity<TrainingResponseDto> logInterval(
            @PathVariable Integer id,
            @PathVariable Integer exerciseId,
            @Valid @RequestBody LogIntervalRequestDto requestDto) {
        if (!trainingExistsUseCase.execute(id)) {
            return ResponseEntity.notFound().build();
        }
        EnduranceInterval interval = trainingDtoMapper.toIntervalEntity(requestDto);
        Training updated = logTrainingIntervalUseCase.execute(id, exerciseId, interval);
        return ResponseEntity.ok(trainingDtoMapper.toResponseDto(updated));
    }

    @DeleteMapping("/{id}/exercises/{exerciseId}/intervals/{intervalNumber}")
    public ResponseEntity<TrainingResponseDto> deleteInterval(
            @PathVariable Integer id,
            @PathVariable Integer exerciseId,
            @PathVariable Integer intervalNumber) {
        if (!trainingExistsUseCase.execute(id)) {
            return ResponseEntity.notFound().build();
        }
        Training updated = deleteTrainingIntervalUseCase.execute(id, exerciseId, intervalNumber);
        return ResponseEntity.ok(trainingDtoMapper.toResponseDto(updated));
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<WorkoutSummaryResponseDto> getWorkoutSummary(@PathVariable Integer id) {
        if (!trainingExistsUseCase.execute(id)) {
            return ResponseEntity.notFound().build();
        }
        WorkoutSummary summary = getWorkoutSummaryUseCase.execute(id);
        return ResponseEntity.ok(trainingDtoMapper.toSummaryDto(summary));
    }

    @PostMapping("/from-session/{sessionId}")
    public ResponseEntity<TrainingResponseDto> createTrainingFromSession(
            @PathVariable Integer sessionId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String customName) {
        if (!sessionExistsUseCase.execute(sessionId)) {
            return ResponseEntity.notFound().build();
        }
        Training training = createTrainingFromSessionUseCase.execute(sessionId, date, customName);
        URI location = URI.create("/api/trainings/" + training.getId());
        return ResponseEntity.created(location).body(trainingDtoMapper.toResponseDto(training));
    }
}
