package com.david13penalver.foss_training_api.application.usecases.athlete.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.athlete.GetLatestBodyweightUseCase;
import com.david13penalver.foss_training_api.domain.model.athlete.BodyweightEntry;
import com.david13penalver.foss_training_api.domain.ports.out.athlete.BodyweightRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetLatestBodyweightService implements GetLatestBodyweightUseCase {

    private final BodyweightRepository bodyweightRepository;

    @Override
    public Optional<BodyweightEntry> execute() {
        log.debug("Executing GetLatestBodyweightUseCase");
        return bodyweightRepository.findLatest();
    }
}
