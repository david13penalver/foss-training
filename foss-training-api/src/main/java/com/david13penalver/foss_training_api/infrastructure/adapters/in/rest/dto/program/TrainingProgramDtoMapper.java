package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.program;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.domain.model.program.ProgramWorkout;
import com.david13penalver.foss_training_api.domain.model.program.TrainingProgram;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.session.SessionDtoMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TrainingProgramDtoMapper {

    private final SessionDtoMapper sessionDtoMapper;

    public TrainingProgram toEntity(TrainingProgramRequestDto dto) {
        if (dto == null) {
            return null;
        }
        TrainingProgram program = new TrainingProgram();
        program.setId(dto.getId());
        program.setName(dto.getName());
        program.setDescription(dto.getDescription());
        program.setDurationWeeks(dto.getDurationWeeks());
        program.setPeriodizationType(dto.getPeriodizationType());
        program.setLevel(dto.getLevel());
        program.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);

        if (dto.getWorkouts() != null) {
            List<ProgramWorkout> workouts = new ArrayList<>();
            for (ProgramWorkoutRequestDto wDto : dto.getWorkouts()) {
                if (wDto != null) {
                    ProgramWorkout workout = new ProgramWorkout();
                    workout.setDayOfWeek(wDto.getDayOfWeek());
                    workout.setFocus(wDto.getFocus());
                    workout.setSession(sessionDtoMapper.toEntity(wDto.getSession()));
                    workouts.add(workout);
                }
            }
            program.setWorkouts(workouts);
        }

        return program;
    }

    public TrainingProgramResponseDto toResponseDto(TrainingProgram program) {
        if (program == null) {
            return null;
        }
        List<ProgramWorkoutResponseDto> workoutDtos = Collections.emptyList();
        if (program.getWorkouts() != null) {
            workoutDtos = program.getWorkouts().stream()
                    .map(w -> ProgramWorkoutResponseDto.builder()
                            .dayOfWeek(w.getDayOfWeek())
                            .focus(w.getFocus())
                            .session(sessionDtoMapper.toResponseDto(w.getSession()))
                            .build())
                    .toList();
        }

        return TrainingProgramResponseDto.builder()
                .id(program.getId())
                .name(program.getName())
                .description(program.getDescription())
                .durationWeeks(program.getDurationWeeks())
                .periodizationType(program.getPeriodizationType())
                .level(program.getLevel())
                .workouts(workoutDtos)
                .isActive(program.getIsActive())
                .build();
    }

    public List<TrainingProgramResponseDto> toResponseDtoList(List<TrainingProgram> programs) {
        if (programs == null) {
            return Collections.emptyList();
        }
        return programs.stream().map(this::toResponseDto).toList();
    }
}
