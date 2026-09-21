package com.david13penalver.foss_training_api.application.usecases.athlete.impl;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.athlete.DeleteBodyweightUseCase;
import com.david13penalver.foss_training_api.domain.ports.out.athlete.BodyweightRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteBodyweightService implements DeleteBodyweightUseCase {

    private final BodyweightRepository bodyweightRepository;

    @Override
    public void execute(Integer id) {
        log.debug("Executing DeleteBodyweightUseCase for id: {}", id);
        if (id != null) {
            bodyweightRepository.deleteById(id);
        }
    }
}
