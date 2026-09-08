package com.david13penalver.foss_training_api.infrastructure.adapters.out.program;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.david13penalver.foss_training_api.domain.model.program.TrainingProgram;
import com.david13penalver.foss_training_api.domain.ports.out.program.TrainingProgramRepository;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.program.SpringDataTrainingProgramRepository;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.program.TrainingProgramJpaEntity;
import com.david13penalver.foss_training_api.infrastructure.adapters.out.persistence.jpa.program.TrainingProgramPersistenceMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TrainingProgramRepositoryImpl implements TrainingProgramRepository {

    private final SpringDataTrainingProgramRepository programRepository;
    private final TrainingProgramPersistenceMapper mapper;

    @Override
    public List<TrainingProgram> findAll() {
        return programRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<TrainingProgram> findById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return programRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public TrainingProgram save(TrainingProgram program) {
        TrainingProgramJpaEntity entity = mapper.toJpaEntity(program);
        TrainingProgramJpaEntity saved = programRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void deleteById(Integer id) {
        if (id != null) {
            programRepository.deleteById(id);
        }
    }

    @Override
    public boolean existsById(Integer id) {
        return id != null && programRepository.existsById(id);
    }
}
