package com.david13penalver.foss_training_api.application.usecases.athlete.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.david13penalver.foss_training_api.application.usecases.athlete.GetBodyweightHistoryUseCase;
import com.david13penalver.foss_training_api.domain.model.athlete.BodyweightEntry;
import com.david13penalver.foss_training_api.domain.ports.out.athlete.BodyweightRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetBodyweightHistoryService implements GetBodyweightHistoryUseCase {

    private final BodyweightRepository bodyweightRepository;

    @Override
    public List<BodyweightEntry> execute() {
        log.debug("Executing GetBodyweightHistoryUseCase");
        return bodyweightRepository.findAll();
    }
}
