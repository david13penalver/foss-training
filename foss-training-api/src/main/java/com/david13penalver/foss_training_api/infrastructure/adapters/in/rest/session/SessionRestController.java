package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.session;

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
import org.springframework.web.bind.annotation.RestController;

import com.david13penalver.foss_training_api.application.usecases.session.DeleteSessionUseCase;
import com.david13penalver.foss_training_api.application.usecases.session.FindAllSessionsUseCase;
import com.david13penalver.foss_training_api.application.usecases.session.FindSessionByIdUseCase;
import com.david13penalver.foss_training_api.application.usecases.session.SaveSessionUseCase;
import com.david13penalver.foss_training_api.application.usecases.session.SessionExistsUseCase;
import com.david13penalver.foss_training_api.domain.model.session.Session;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.session.SessionDtoMapper;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.session.SessionRequestDto;
import com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.session.SessionResponseDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionRestController {

    private final FindAllSessionsUseCase findAllSessionsUseCase;
    private final FindSessionByIdUseCase findSessionByIdUseCase;
    private final SaveSessionUseCase saveSessionUseCase;
    private final DeleteSessionUseCase deleteSessionUseCase;
    private final SessionExistsUseCase sessionExistsUseCase;
    private final SessionDtoMapper sessionDtoMapper;

    @GetMapping
    public ResponseEntity<List<SessionResponseDto>> getAllSessions() {
        List<Session> sessions = findAllSessionsUseCase.execute();
        return ResponseEntity.ok(sessionDtoMapper.toResponseDtoList(sessions));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessionResponseDto> getSessionById(@PathVariable Integer id) {
        Optional<Session> session = findSessionByIdUseCase.execute(id);
        return session.map(sessionDtoMapper::toResponseDto)
                      .map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SessionResponseDto> createSession(@Valid @RequestBody SessionRequestDto requestDto) {
        Session session = sessionDtoMapper.toEntity(requestDto);
        Session savedSession = saveSessionUseCase.execute(session);
        URI location = URI.create("/api/sessions/" + savedSession.getId());
        return ResponseEntity.created(location).body(sessionDtoMapper.toResponseDto(savedSession));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SessionResponseDto> updateSession(@PathVariable Integer id, @Valid @RequestBody SessionRequestDto requestDto) {
        if (!sessionExistsUseCase.execute(id)) {
            return ResponseEntity.notFound().build();
        }
        requestDto.setId(id);
        Session session = sessionDtoMapper.toEntity(requestDto);
        Session savedSession = saveSessionUseCase.execute(session);
        return ResponseEntity.ok(sessionDtoMapper.toResponseDto(savedSession));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Integer id) {
        if (!sessionExistsUseCase.execute(id)) {
            return ResponseEntity.notFound().build();
        }
        deleteSessionUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> sessionExists(@PathVariable Integer id) {
        boolean exists = sessionExistsUseCase.execute(id);
        return ResponseEntity.ok(exists);
    }
}