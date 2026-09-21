package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.exercise;

import java.net.URI;
import java.util.List;
import java.util.Optional;

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

import com.david13penalver.foss_training_api.application.usecases.exercise.exercise.DeleteExerciseUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.exercise.ExerciseExistsUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.exercise.FindAllExercisesUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.exercise.FindExerciseByIdUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.exercise.SaveExerciseUseCase;
import com.david13penalver.foss_training_api.application.usecases.exercise.exercise.SearchExercisesUseCase;
import com.david13penalver.foss_training_api.domain.model.common.PageQuery;
import com.david13penalver.foss_training_api.domain.model.common.PagedResult;
import com.david13penalver.foss_training_api.domain.model.exercise.DifficultyLevel;
import com.david13penalver.foss_training_api.domain.model.exercise.Equipment;
import com.david13penalver.foss_training_api.domain.model.exercise.Exercise;
import com.david13penalver.foss_training_api.domain.model.exercise.ExerciseCategory;
import com.david13penalver.foss_training_api.domain.model.exercise.ExerciseSearchCriteria;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.common.PageResponseDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.exercise.ExerciseDtoMapper;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.exercise.ExerciseRequestDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.exercise.ExerciseResponseDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
public class ExerciseRestController {

    private final FindAllExercisesUseCase findAllExercisesUseCase;
    private final FindExerciseByIdUseCase findExerciseByIdUseCase;
    private final SaveExerciseUseCase saveExerciseUseCase;
    private final DeleteExerciseUseCase deleteExerciseUseCase;
    private final ExerciseExistsUseCase exerciseExistsUseCase;
    private final SearchExercisesUseCase searchExercisesUseCase;
    private final ExerciseDtoMapper exerciseDtoMapper;

    @GetMapping
    public ResponseEntity<List<ExerciseResponseDto>> getAllExercises(
            @RequestParam(required = false) String search,
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(required = false) ExerciseCategory primaryCategory,
            @RequestParam(required = false) String muscleGroup,
            @RequestParam(required = false) Equipment equipment,
            @RequestParam(required = false) DifficultyLevel difficultyLevel,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {

        String searchTerm = (search != null && !search.isBlank()) ? search : q;
        boolean hasFilters = searchTerm != null || primaryCategory != null || muscleGroup != null
                || equipment != null || difficultyLevel != null;
        boolean hasPagination = page != null || size != null;

        if (!hasFilters && !hasPagination) {
            List<Exercise> exercises = findAllExercisesUseCase.execute();
            return ResponseEntity.ok(exerciseDtoMapper.toResponseDtoList(exercises));
        }

        ExerciseSearchCriteria criteria = new ExerciseSearchCriteria(
                searchTerm,
                primaryCategory,
                muscleGroup,
                equipment,
                difficultyLevel
        );

        if (hasPagination) {
            int pageIndex = page != null ? Math.max(0, page) : 0;
            int pageSize = size != null && size > 0 ? size : 20;
            PageQuery pageQuery = PageQuery.of(pageIndex, pageSize, sortBy, sortDirection);
            PagedResult<Exercise> pagedResult = searchExercisesUseCase.execute(criteria, pageQuery);

            return ResponseEntity.ok()
                    .header("X-Total-Count", String.valueOf(pagedResult.totalElements()))
                    .header("X-Total-Pages", String.valueOf(pagedResult.totalPages()))
                    .header("X-Page-Number", String.valueOf(pagedResult.page()))
                    .header("X-Page-Size", String.valueOf(pagedResult.size()))
                    .body(exerciseDtoMapper.toResponseDtoList(pagedResult.content()));
        }

        List<Exercise> exercises = searchExercisesUseCase.execute(criteria);
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(exercises.size()))
                .body(exerciseDtoMapper.toResponseDtoList(exercises));
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponseDto<ExerciseResponseDto>> searchExercises(
            @RequestParam(required = false) String search,
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(required = false) ExerciseCategory primaryCategory,
            @RequestParam(required = false) String muscleGroup,
            @RequestParam(required = false) Equipment equipment,
            @RequestParam(required = false) DifficultyLevel difficultyLevel,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {

        String searchTerm = (search != null && !search.isBlank()) ? search : q;
        ExerciseSearchCriteria criteria = new ExerciseSearchCriteria(
                searchTerm,
                primaryCategory,
                muscleGroup,
                equipment,
                difficultyLevel
        );
        PageQuery pageQuery = PageQuery.of(page, size, sortBy, sortDirection);
        PagedResult<Exercise> pagedResult = searchExercisesUseCase.execute(criteria, pageQuery);

        return ResponseEntity.ok(exerciseDtoMapper.toPageResponseDto(pagedResult));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseResponseDto> getExerciseById(@PathVariable Integer id) {
        Optional<Exercise> exercise = findExerciseByIdUseCase.execute(id);
        return exercise.map(exerciseDtoMapper::toResponseDto)
                      .map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ExerciseResponseDto> createExercise(@Valid @RequestBody ExerciseRequestDto requestDto) {
        Exercise exercise = exerciseDtoMapper.toEntity(requestDto);
        Exercise savedExercise = saveExerciseUseCase.execute(exercise);
        URI location = URI.create("/api/exercises/" + savedExercise.getId());
        return ResponseEntity.created(location).body(exerciseDtoMapper.toResponseDto(savedExercise));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExerciseResponseDto> updateExercise(@PathVariable Integer id, @Valid @RequestBody ExerciseRequestDto requestDto) {
        if (!exerciseExistsUseCase.execute(id)) {
            return ResponseEntity.notFound().build();
        }
        requestDto.setId(id);
        Exercise exercise = exerciseDtoMapper.toEntity(requestDto);
        Exercise savedExercise = saveExerciseUseCase.execute(exercise);
        return ResponseEntity.ok(exerciseDtoMapper.toResponseDto(savedExercise));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExercise(@PathVariable Integer id) {
        if (!exerciseExistsUseCase.execute(id)) {
            return ResponseEntity.notFound().build();
        }
        deleteExerciseUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> exerciseExists(@PathVariable Integer id) {
        boolean exists = exerciseExistsUseCase.execute(id);
        return ResponseEntity.ok(exists);
    }
}
