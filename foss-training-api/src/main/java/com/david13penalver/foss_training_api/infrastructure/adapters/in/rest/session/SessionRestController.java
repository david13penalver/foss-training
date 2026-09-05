package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.session;

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

    @GetMapping
    public ResponseEntity<List<Session>> getAllSessions() {
        List<Session> sessions = findAllSessionsUseCase.execute();
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Session> getSessionById(@PathVariable Integer id) {
        Optional<Session> session = findSessionByIdUseCase.execute(id);
        return session.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Session> createSession(@RequestBody Session session) {
        Session savedSession = saveSessionUseCase.execute(session);
        return ResponseEntity.ok(savedSession);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Session> updateSession(@PathVariable Integer id, @RequestBody Session session) {
        session.setId(id);
        Session savedSession = saveSessionUseCase.execute(session);
        return ResponseEntity.ok(savedSession);
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